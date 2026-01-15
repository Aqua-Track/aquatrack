package com.ufpb.aquatrack.models;

import com.ufpb.aquatrack.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    private String urlFoto;

    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @NotNull
    private List<Fazenda> fazendas;

    @Column(nullable = false)
    private boolean deletado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    public Usuario(String login, String nome, String senha, TipoUsuario tipo) {
        this.login = login;
        this.nome = nome;
        this.senha = senha;
        this.tipo = tipo;
        this.deletado = false;
        this.fazendas = new ArrayList<>();
        // this.urlFoto = "/images/default-user.png";
    }
}