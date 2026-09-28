package com.example.demo.controllers;

import com.example.demo.model.Url;
import com.example.demo.repository.UrlRepository;
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

    public RedirectController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @GetMapping("/{shortCode:[0-9A-Za-z]+}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Url existing = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No URL with short code " + shortCode));

        URI destination = validateDestination(existing.getUrl());
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