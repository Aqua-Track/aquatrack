package com.ufpb.aquatrack.fazenda;

import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.viveiro.Viveiro;
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

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String localizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(
            mappedBy = "fazenda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Viveiro> viveiros = new ArrayList<>();

    @Column(nullable = false)
    private boolean deletado;

    protected Fazenda() { }

    public Fazenda(Usuario usuario) {
        this.usuario = usuario;
        this.deletado = false;
    }

    public Fazenda(String nome, String localizacao, Usuario usuario) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.usuario = usuario;
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
