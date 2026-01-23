package com.ufpb.aquatrack.ciclo;

import com.ufpb.aquatrack.viveiro.Viveiro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        indexes = { //define índices de banco de dados que serão criados automaticamente
                @Index(name = "idx_ciclo_viveiro_ativo",
                        columnList = "viveiro_id, ativo")
        }
)
public class Ciclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viveiro_id", nullable = false)
    private Viveiro viveiro;

    @Column(nullable = false)
    private LocalDate dataPovoamento;

    @Column(nullable = false)
    private int quantidadePovoada;

    @Column(nullable = false)
    private String laboratorio;


    @Column(nullable = false)
    private boolean ativo;

    @Column(nullable = true)
    private LocalDate dataEncerramento;

    @Column(nullable = false)
    private boolean deletado;

    protected Ciclo() {
    }

    public Ciclo(Viveiro viveiro, LocalDate dataPovoamento, int quantidadePovoada, String laboratorio) {
        this.viveiro = viveiro;
        this.dataPovoamento = dataPovoamento;
        this.quantidadePovoada = quantidadePovoada;
        this.laboratorio = laboratorio;
        this.ativo = true;
        this.deletado = false;
    }


    public void encerrar(LocalDate dataEncerramento) {
        this.ativo = false;
        this.dataEncerramento = dataEncerramento;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
