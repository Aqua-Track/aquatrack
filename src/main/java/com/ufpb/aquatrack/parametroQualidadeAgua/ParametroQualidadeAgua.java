package com.ufpb.aquatrack.parametroQualidadeAgua;

import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_parametro_usuario_nome",
                        columnNames = {"usuario_id", "nome"}
                )
        }
)
public class ParametroQualidadeAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean deletado;

    protected ParametroQualidadeAgua() {
        this.deletado = false;
    }

    public ParametroQualidadeAgua(Usuario usuario, String nome ) {
        this.usuario = usuario;
        this.nome = nome;
        this.deletado = false;
    }

    public void marcarComoDeletado() {
        this.deletado = true;
    }
}
