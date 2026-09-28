package com.example.demo.controllers;

import com.example.demo.model.Url;
import com.example.demo.repository.UrlRepository;
import com.example.demo.service.UrlCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
public class RedirectController {

    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;

    public RedirectController(UrlRepository urlRepository, UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.urlCacheService = urlCacheService;
    }

    @GetMapping("/{shortCode:[0-9A-Za-z]+}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String cachedUrl = urlCacheService.get(shortCode);
        String destinationUrl = cachedUrl;

        if (destinationUrl == null) {
            Url existing = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No URL with short code " + shortCode));
            destinationUrl = existing.getUrl();
            urlCacheService.put(shortCode, destinationUrl);
        }

        URI destination = validateDestination(destinationUrl);
        urlRepository.incrementStats(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(destination)
                .build();
    }

    private URI validateDestination(String url) {
        final URI destination;
        try {
            destination = URI.create(url.trim());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid destination URL");
        }

        String scheme = destination.getScheme();
        if (destination.getHost() == null
                || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destination must use an HTTP or HTTPS URL");
        }

        return destination;
    }
}