package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    // DB-assigned AUTO_INCREMENT id — never set it from code
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String url;
    @Column(nullable = false, unique = true)
    private String shortCode;

    // how many times GET /shorten/{shortCode} has fetched this row
    @Column(nullable = false)
    private int stats = 0;

    // Hibernate sets these automatically on insert/update — never touch them in code
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // required by Hibernate to instantiate entities when loading rows
    protected Url() {
    }

    public Url(String url, String shortCode) {
        this.url = url;
        this.shortCode = shortCode;
    }

    // getters — Jackson uses these to serialize the response
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getShortCode() {
        return shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getStats() {
        return stats;
    }

    // setters — only for fields a client may actually change
    public void setUrl(String url) {
        this.url = url;
    }
}
