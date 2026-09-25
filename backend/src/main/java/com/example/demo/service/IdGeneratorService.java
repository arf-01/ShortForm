package com.example.demo.service;

import com.example.demo.model.SequenceAllocator;
import com.example.demo.repository.SequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGeneratorService {

    private static final long RANGE_SIZE = 10_000;
    private static final long XOR_KEY = 0x5DEECE66DL;
    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private final SequenceRepository sequenceRepository;
    private final AtomicLong currentId = new AtomicLong();
    private long rangeEnd;

    public IdGeneratorService(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public synchronized String getNextCode() {
        if (currentId.get() >= rangeEnd) {
            allocateNewBlock();
        }
        long counter = currentId.getAndIncrement();
        return encodeBase62(counter ^ XOR_KEY);
    }

    private void allocateNewBlock() {
        SequenceAllocator sequence = sequenceRepository.findById("url_seq")
                .orElseThrow(() -> new IllegalStateException("Sequence url_seq was not initialized"));

        long firstId = sequence.getNextId();
        long nextId = firstId + RANGE_SIZE;
        sequence.setNextId(nextId);
        sequenceRepository.save(sequence);

        currentId.set(firstId);
        rangeEnd = nextId;
    }

    private String encodeBase62(long value) {
        if (value == 0) {
            return String.valueOf(BASE62[0]);
        }

        StringBuilder code = new StringBuilder();
        while (value > 0) {
            code.append(BASE62[(int) (value % BASE62.length)]);
            value /= BASE62.length;
        }
        return code.reverse().toString();
    }
}
