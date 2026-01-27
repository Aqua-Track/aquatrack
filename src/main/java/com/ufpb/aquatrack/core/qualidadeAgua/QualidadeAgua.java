package com.ufpb.aquatrack.core.qualidadeAgua;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "qualidade_agua",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ciclo_id", "data_coleta"})
        }
)
public class QualidadeAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ciclo_id")
    private Ciclo ciclo;

    @Column(name = "data_coleta", nullable = false)
    private LocalDate dataColeta;

    private Double amonia;
    private Double nitrito;
    private Double ph;
    private Double alcalinidade;
    private Double salinidade;
    private Double oxigenio;

    private boolean deletado = false;

    protected QualidadeAgua() {}

    public QualidadeAgua(
            Ciclo ciclo, LocalDate dataColeta,
            Double amonia, Double nitrito, Double ph,
            Double alcalinidade, Double salinidade, Double oxigenio
    ) {
        this.ciclo = ciclo;
        this.dataColeta = dataColeta;
        this.amonia = amonia;
        this.nitrito = nitrito;
        this.ph = ph;
        this.alcalinidade = alcalinidade;
        this.salinidade = salinidade;
        this.oxigenio = oxigenio;
    }

    public void atualizar(
            LocalDate dataColeta,
            Double amonia, Double nitrito, Double ph,
            Double alcalinidade, Double salinidade, Double oxigenio
    ) {
        this.dataColeta = dataColeta;
        this.amonia = amonia;
        this.nitrito = nitrito;
        this.ph = ph;
        this.alcalinidade = alcalinidade;
        this.salinidade = salinidade;
        this.oxigenio = oxigenio;
    }

    public void deletar() {
        this.deletado = true;
    }

}
