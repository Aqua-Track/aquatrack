package com.ufpb.aquatrack.services;

import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleta(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getId()) && !usuario.isDeletado()) {
            usuario.setDeletado(true);
        }
        //lançar uma exceptuon especifica pra quando ele nn conseguir deletar
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorId(Usuario usuario) {
        return usuarioRepository.findById(usuario.getId()).orElse(null);
    }

    public Usuario editarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


}

