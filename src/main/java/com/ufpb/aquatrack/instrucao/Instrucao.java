package com.ufpb.aquatrack.instrucao;

import com.ufpb.aquatrack.viveiro.Viveiro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Instrucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstrucaoStatus status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @Column(nullable = false)
    private boolean deletado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viveiro_id", nullable = false)
    private Viveiro viveiro;

    protected Instrucao() {
    }

    public Instrucao(String tag, String titulo, String descricao, Viveiro viveiro) {
        this.tag = tag;
        this.titulo = titulo;
        this.descricao = descricao;
        this.viveiro = viveiro;
        this.status = InstrucaoStatus.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
        this.deletado = false;
    }

    public void marcarComoConcluida() {
        this.status = InstrucaoStatus.CONCLUIDO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void marcarComoPendente() {
        this.status = InstrucaoStatus.PENDENTE;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void atualizar(String titulo, String descricao, InstrucaoStatus status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void marcarComoDeletada() {
        this.deletado = true;
    }
}
