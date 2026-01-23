package com.ufpb.aquatrack.consumo;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ConsumoRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_racao_id", nullable = false)
    private TipoRacao tipoRacao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantidadeKg;

    @Column(nullable = false)
    private LocalDate dataConsumo;

    @Column(nullable = false)
    private boolean deletado;

    protected ConsumoRacao() {
        this.deletado = false;
    }

    public ConsumoRacao(Ciclo ciclo, TipoRacao tipoRacao, BigDecimal quantidadeKg, LocalDate dataConsumo) {
        this.ciclo = ciclo;
        this.tipoRacao = tipoRacao;
        this.quantidadeKg = quantidadeKg;
        this.dataConsumo = dataConsumo;
        this.deletado = false;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
