package com.boilerplate.scope;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scopes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scope {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}
