package com.example.demo.repository;

import com.example.demo.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data implements this at runtime — findByShortCode becomes
// "select * from urls where short_code = ?"
public interface UrlRepository extends JpaRepository<Url, Integer> {
    Optional<Url> findByShortCode(String shortCode);
}
