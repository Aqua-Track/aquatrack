package com.ufpb.aquatrack.core.fazenda;

import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Fazenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String codigo;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String localizacao;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(
            mappedBy = "fazenda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Viveiro> viveiros = new ArrayList<>();

    @Column(nullable = false)
    private boolean deletado;

    protected Fazenda() {
    }

    public Fazenda(String nome, String localizacao, Usuario usuario, String codigo) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.usuario = usuario;
        this.codigo = codigo;
        this.deletado = false;
    }

    public void adicionarViveiro(Viveiro viveiro) {
        viveiro.setFazenda(this);
        this.viveiros.add(viveiro);
    }

    public void removerViveiro(Viveiro viveiro) {
        if (!this.viveiros.contains(viveiro)) {
            throw new IllegalArgumentException("Viveiro não pertence a esta fazenda");
        }
        viveiro.marcarComoDeletado();
    }

}
