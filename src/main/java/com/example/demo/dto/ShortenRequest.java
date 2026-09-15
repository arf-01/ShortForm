package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

// request body for POST /shorten and PUT /shorten/{shortCode} — the only field a client supplies
public record ShortenRequest(@NotBlank String url) {
}
