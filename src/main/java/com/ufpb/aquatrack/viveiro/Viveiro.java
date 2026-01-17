package com.ufpb.aquatrack.viveiro;

import com.ufpb.aquatrack.fazenda.Fazenda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fazenda_id", "tag"})
        }
)
public class Viveiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private double area;

    @Column(nullable = false)
    private boolean deletado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;


    @Column(nullable = false)
    private boolean cicloAtivo; //Isso é só para testar por agora, sairá no futuro

    protected Viveiro() {

    }

    public Viveiro(String tag, double area) {
        this.tag = tag;
        this.area = area;
        this.deletado = false;
        this.cicloAtivo = false;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
