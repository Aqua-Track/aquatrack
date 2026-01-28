package com.ufpb.aquatrack.core.relatorio;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "relatorios_ciclo")
public class RelatorioCiclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long viveiroId;

    @Column(nullable = false)
    private String tagViveiro;

    @Column(nullable = false)
    private String codigoFazenda;

    @Column(nullable = false)
    private String nomeFazenda;

    @Column(nullable = false)
    private String laboratorio;

    @Column(nullable = false)
    private LocalDate dataPovoamento;

    @Column(nullable = false)
    private LocalDate dataEncerramento;

    @Column(nullable = false)
    private Long diasCultivo;

    @Column(nullable = false)
    private Integer quantidadePovoada;

    @Column(nullable = false)
    private BigDecimal biometriaFinal; // grama

    @Column(nullable = false)
    private BigDecimal biomassaFinal; // quilograma

    @Column(nullable = false)
    private BigDecimal consumoTotalRacao; // quilograma

    @Column(nullable = false)
    private BigDecimal sobrevivencia; // %

    @Column(nullable = false)
    private BigDecimal fca;

    // ===== Controle =====
    @Column(nullable = false)
    private boolean deletado = false;

    protected RelatorioCiclo() {}

    public RelatorioCiclo(
            Long viveiroId,
            String tagViveiro,
            String codigoFazenda,
            String nomeFazenda,
            String laboratorio,
            LocalDate dataPovoamento,
            LocalDate dataEncerramento,
            Long diasCultivo,
            Integer quantidadePovoada,
            BigDecimal biometriaFinal,
            BigDecimal biomassaFinal,
            BigDecimal consumoTotalRacao,
            BigDecimal sobrevivencia,
            BigDecimal fca
    ) {
        this.viveiroId = viveiroId;
        this.tagViveiro = tagViveiro;
        this.codigoFazenda = codigoFazenda;
        this.laboratorio = laboratorio;
        this.nomeFazenda = nomeFazenda;
        this.dataPovoamento = dataPovoamento;
        this.dataEncerramento = dataEncerramento;
        this.diasCultivo = diasCultivo;
        this.quantidadePovoada = quantidadePovoada;
        this.biometriaFinal = biometriaFinal;
        this.biomassaFinal = biomassaFinal;
        this.consumoTotalRacao = consumoTotalRacao;
        this.sobrevivencia = sobrevivencia;
        this.fca = fca;
    }

}