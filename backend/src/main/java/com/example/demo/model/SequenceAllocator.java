package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sequence_allocator")
public class SequenceAllocator {
    @Id
    @Column(name = "sequence_name")
    private String sequenceName;

    @Column(name = "next_id")
    private long nextId;

    protected SequenceAllocator() {
    }

    public SequenceAllocator(String sequenceName, long nextId) {
        this.sequenceName = sequenceName;
        this.nextId = nextId;
    }

    // Getters and Setters
    public String getSequenceName() { return sequenceName; }
    public void setSequenceName(String sequenceName) { this.sequenceName = sequenceName; }
    public long getNextId() { return nextId; }
    public void setNextId(long nextId) { this.nextId = nextId; }
}