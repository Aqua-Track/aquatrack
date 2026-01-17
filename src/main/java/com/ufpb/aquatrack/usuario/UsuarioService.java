package com.ufpb.aquatrack.usuario;

import com.ufpb.aquatrack.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void cadastrarUsuario(String nome, String login, String senha, UsuarioRole role) {

        if (usuarioRepository.findByLoginAndDeletadoFalse(login).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com esse login.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        usuario.setRole(role);
        usuarioRepository.save(usuario);
    }


    public Usuario autenticar(String login, String senha){
        Optional<Usuario> usuario = usuarioRepository.findByLoginAndDeletadoFalse(login);

        if (usuario.isEmpty()) {return null;}
        if (usuario.get().isDeletado()){return null;}
        if (!BCrypt.checkpw(senha, usuario.get().getSenha())) {return null;}

        return usuario.orElse(null);
    }

    public void removerUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);

        usuario.setDeletado(true);
        usuarioRepository.save(usuario);
        //lançar uma exceptuon especifica pra quando ele nn conseguir deletar
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findByDeletadoFalse();
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findByIdAndDeletadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public void editarUsuario(
            Usuario master, Long idUsuarioEditado, String nome,
            String login, String novaSenhaUsuario, String senhaMaster
    ) {

        // Segurança básica
        if (master == null || master.getRole() != UsuarioRole.MASTER) {
            throw new IllegalArgumentException("Permissão negada.");
        }

        // Valida senha do MASTER
        if (!BCrypt.checkpw(senhaMaster, master.getSenha())) {
            throw new IllegalArgumentException("Senha do administrador inválida.");
        }

        Usuario usuario = buscarUsuarioPorId(idUsuarioEditado);
        usuario.setNome(nome);
        usuario.setLogin(login);

        if (novaSenhaUsuario != null && !novaSenhaUsuario.isBlank()) {
            usuario.setSenha(BCrypt.hashpw(novaSenhaUsuario, BCrypt.gensalt()));
        }

        usuarioRepository.save(usuario);
    }

}

