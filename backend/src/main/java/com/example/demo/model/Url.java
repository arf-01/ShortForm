package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @Column(name = "short_code", length = 16)
    private String shortCode;

    @Lob
    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "creator_id")
    private Long creatorId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private int stats = 0;

    @Column(nullable = false)
    private short status = 1;

    // required by Hibernate to instantiate entities when loading rows
    protected Url() {
    }

    public Url(String url, String shortCode) {
        this.longUrl = url;
        this.shortCode = shortCode;
    }

    // Temporary compatibility accessors until the controller and API are updated.
    public String getUrl() {
        return longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public int getStats() {
        return stats;
    }

    public short getStatus() {
        return status;
    }

    public void setUrl(String url) {
        this.longUrl = url;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
