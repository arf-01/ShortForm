package com.example.demo.controllers;

import com.example.demo.model.Url;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class UrlController {

    private final List<Url> urls = new CopyOnWriteArrayList<>();
    private int nextId = 1;

    // POST /shorten  { "url": "https://..." }
    // → server assigns id, generates a random 6-digit short code, stamps timestamps
    @PostMapping("/shorten")
    public Url shorten(@RequestBody Url request) {
        LocalDateTime now = LocalDateTime.now();
        Url saved = new Url(nextId++, request.getUrl(), generateShortCode(), now, now);
        urls.add(saved);
        return saved;
    }

    // PUT /urls/{id}  { "url": "https://new-url" }
    // → update the long URL; updatedAt changes, createdAt stays
    @PutMapping("/urls/{id}")
    public Url updateUrl(@PathVariable int id, @RequestBody Url request) {
        Url existing = urls.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No URL with id " + id));
        existing.setUrl(request.getUrl());
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    // GET /urls → list everything (handy for checking what's stored)
    @GetMapping("/urls")
    public List<Url> all() {
        return urls;
    }

    // GET /code/{shortCode} → look up by short code
    @GetMapping("/code/{shortCode}")
    public Url getByCode(@PathVariable String shortCode) {
        return urls.stream()
                .filter(u -> u.getShortCode().equals(shortCode))
                .findFirst()
                .orElse(null);
    }

    // six random digits, e.g. "483920"
    private String generateShortCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }
}
