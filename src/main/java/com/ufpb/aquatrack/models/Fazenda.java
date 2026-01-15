package com.ufpb.aquatrack.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Fazenda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @OneToMany(
            mappedBy = "fazenda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Viveiro> viveiros;
}
