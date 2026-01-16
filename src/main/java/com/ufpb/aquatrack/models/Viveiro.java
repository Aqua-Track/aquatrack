package com.ufpb.aquatrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public Viveiro() {
        this.deletado = false;
    }

    public Viveiro(String tag, double area, Fazenda fazenda) {
        this.tag = tag;
        this.area = area;
        this.deletado = false;
        this.fazenda = fazenda;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
