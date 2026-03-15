package com.boilerplate.strains;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "strains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Strain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private int lbcc;

    @Column(nullable = false)
    private LocalDate strainDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Strain(String name, int lbcc, LocalDate strainDate) {
        this.name = name;
        this.lbcc = lbcc;
        this.strainDate = strainDate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    }