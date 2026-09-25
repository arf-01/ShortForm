package com.example.demo.repository;

import com.example.demo.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Spring Data implements this at runtime — findByShortCode becomes
// "select * from urls where short_code = ?"
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query("SELECT u FROM Url u WHERE u.shortCode = :shortCode")
    Optional<Url> findByShortCode(@Param("shortCode") String shortCode);
    // bumps only the stats column — bypasses the entity, so @UpdateTimestamp
    // (updatedAt) does NOT get refreshed on a stats hit
    @Modifying
    @Transactional
    @Query("UPDATE Url u SET u.stats = u.stats + 1 WHERE u.shortCode = :shortCode")
    void incrementStats(@Param("shortCode") String shortCode);
}
