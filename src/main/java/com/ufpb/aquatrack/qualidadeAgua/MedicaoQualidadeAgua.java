package com.ufpb.aquatrack.qualidadeAgua;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.parametroQualidadeAgua.ParametroQualidadeAgua;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        indexes = {
                @Index(
                        name = "idx_medicao_ciclo_data_parametro",
                        columnList = "ciclo_id, data_medicao, parametro_id"
                )
        }
)
public class MedicaoQualidadeAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parametro_id", nullable = false)
    private ParametroQualidadeAgua parametro;

    @Column(name = "data_medicao", nullable = false)
    private LocalDate dataMedicao;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal valor;

    @Column(nullable = false)
    private boolean deletado;

    protected MedicaoQualidadeAgua() {
        this.deletado = false;
    }

    public MedicaoQualidadeAgua(Ciclo ciclo, ParametroQualidadeAgua parametro, LocalDate dataMedicao, BigDecimal valor) {
        this.ciclo = ciclo;
        this.parametro = parametro;
        this.dataMedicao = dataMedicao;
        this.valor = valor;
        this.deletado = false;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
