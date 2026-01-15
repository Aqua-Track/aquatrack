package com.ufpb.aquatrack.models;

import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fazenda_id", "tag"})
        }
)
public class Viveiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID técnico

    @Column(nullable = false)
    private String tag; // ID de negócio (usuário define)

    @Column(nullable = false)
    private double area;

    @Column(nullable = false)
    private boolean deletado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;
}
