package com.ufpb.aquatrack.tipoRacao;

import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "tipo_racao",
        uniqueConstraints = {
                @UniqueConstraint( //não permite que um úsuario cadastre 2 rações com o mesmo nome
                        columnNames = {"usuario_id", "nome"}
                )
        }
)
public class TipoRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String fabricante;

    @NotNull
    @Column(nullable = false)
    private BigDecimal kgPorSaco;

    @Column(nullable = false)
    private BigDecimal valorPorSaco;

    @Column(nullable = false)
    private boolean deletado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected TipoRacao() {
        this.deletado = false;
    }

    public TipoRacao(String nome, String fabricante, BigDecimal kgPorSaco, BigDecimal valorPorSaco, Usuario usuario) {
        this.nome = nome;
        this.fabricante = fabricante;
        this.kgPorSaco = kgPorSaco;
        this.valorPorSaco = valorPorSaco;
        this.usuario = usuario;
        this.deletado = false;
    }
}

