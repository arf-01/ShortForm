package com.example.demo.controllers;

import com.example.demo.dto.ShortenRequest;
import com.example.demo.model.Url;
import com.example.demo.repository.UrlRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500", "http://127.0.0.1:5500"})
public class UrlController {

    private final UrlRepository urlRepository;

    public UrlController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // POST /shorten  { "url": "https://..." }
    // → DB assigns the id, Hibernate stamps createdAt/updatedAt
    @PostMapping("/shorten")
    public Url shorten(@Valid @RequestBody ShortenRequest request) {
        return urlRepository.save(new Url(validateDestination(request.url()).toString(), generateShortCode()));
    }

    // PUT /shorten/{shortCode}  { "url": "https://new-url" }
    // → update the long URL; @UpdateTimestamp refreshes updatedAt, createdAt stays
    @PutMapping("/shorten/{shortCode}")
    public Url updateUrl(@PathVariable String shortCode, @Valid @RequestBody ShortenRequest request) {
        Url existing = findByCode(shortCode);
        existing.setUrl(validateDestination(request.url()).toString());
        return urlRepository.save(existing);
    }

    // DELETE /shorten/{shortCode} → remove the short URL
    // 204 on success, 404 when the code doesn't exist
    @DeleteMapping("/shorten/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUrl(@PathVariable String shortCode) {
        Url existing = findByCode(shortCode);
        urlRepository.delete(existing);
    }

    // GET /search/{shortCode} → look up a code for the Find button.
    @GetMapping("/search/{shortCode}")
    public Url getByCode(@PathVariable String shortCode){
    return findByCode(shortCode);
    }

    // GET /shorten/{shortCode} -> redirect a browser to the original URL
    @GetMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Url existing = findByCode(shortCode);
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

    private Url findByCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No URL with short code " + shortCode));
    }

    // six random digits, e.g. "483920"
    private String generateShortCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }
}
