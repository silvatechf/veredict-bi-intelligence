package com.ajh.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "talents")
public class Talent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String role;
    private Double matchScore; // A Llama-3 calculará isso
    private String assessmentStatus; // 'Elite Match', 'Top Tier', etc.
}