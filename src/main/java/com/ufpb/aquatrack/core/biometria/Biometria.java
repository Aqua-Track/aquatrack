package com.ufpb.aquatrack.core.biometria;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_biometria_ciclo_data", columnList = "ciclo_id, data_biometria")
        }
)
@Getter
@Setter
public class Biometria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private Ciclo ciclo;

    @Column(name = "data_biometria", nullable = false)
    private LocalDate dataBiometria;

    @Column(nullable = false)
    private Integer quantidadeAmostrada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoTotalAmostra;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoMedio;

    @Column(nullable = false)
    private boolean deletado;

    protected Biometria() {
        this.deletado = false;
    }

    public Biometria(Ciclo ciclo, LocalDate dataBiometria, Integer quantidadeAmostrada, BigDecimal pesoTotalAmostra) {
        this.ciclo = ciclo;
        this.dataBiometria = dataBiometria;
        this.quantidadeAmostrada = quantidadeAmostrada;
        this.pesoTotalAmostra = pesoTotalAmostra;
        this.pesoMedio = pesoTotalAmostra
                .divide(BigDecimal.valueOf(quantidadeAmostrada), 2, RoundingMode.HALF_UP);
        this.deletado = false;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
