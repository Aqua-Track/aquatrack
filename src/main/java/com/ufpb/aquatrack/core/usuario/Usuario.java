package com.ufpb.aquatrack.core.usuario;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String login;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    private String urlFoto;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Fazenda fazenda;

    @Column(nullable = false)
    private boolean deletado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsuarioRole role;

    @Column(nullable = false)
    private boolean contaVerificada;

    public Usuario(String login, String nome, String senha, UsuarioRole role) {
        this.login = login;
        this.nome = nome;
        this.senha = senha;
        this.role = role;
        this.deletado = false;
        this.urlFoto = "/images/default-user.png";
        this.contaVerificada = false;
    }

    public Usuario() {
        this.deletado = false;
        this.urlFoto = "/images/default-user.png";
    }
}