package com.example.demo.repository;

import com.example.demo.model.SequenceAllocator;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SequenceRepository extends JpaRepository<SequenceAllocator, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Transactional(readOnly = false)
	Optional<SequenceAllocator> findById(String sequenceName);
}