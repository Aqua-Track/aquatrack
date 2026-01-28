package com.ufpb.aquatrack.core.usuario.conta;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.usuario.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UsuarioContaService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioContaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario atualizarDadosBasicos(Usuario usuario, String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido.");
        }

        usuario.setNome(nome.trim());
        return usuarioRepository.save(usuario);
    }

    public Usuario alterarEmail(Usuario usuario, String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) {
            throw new IllegalArgumentException("Email inválido.");
        }

        boolean emailEmUso = usuarioRepository
                .findByLoginAndDeletadoFalse(novoEmail)
                .filter(u -> !u.getId().equals(usuario.getId()))
                .isPresent();

        if (emailEmUso) {
            throw new IllegalArgumentException("Já existe um usuário com esse email.");
        }

        usuario.setLogin(novoEmail.trim());
        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(
            Usuario usuario,
            String senhaAtual,
            String novaSenha,
            String confirmarSenha
    ) {

        if (senhaAtual == null || novaSenha == null || confirmarSenha == null) {
            throw new IllegalArgumentException("Preencha todos os campos de senha.");
        }

        if (!BCrypt.checkpw(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        if (!novaSenha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (BCrypt.checkpw(novaSenha, usuario.getSenha())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
        }

        usuario.setSenha(BCrypt.hashpw(novaSenha, BCrypt.gensalt()));
        usuarioRepository.save(usuario);
    }

    public Usuario alterarFoto(Usuario usuario, String urlFoto) {
        if (urlFoto == null || urlFoto.isBlank()) {
            throw new IllegalArgumentException("URL da foto inválida.");
        }

        usuario.setUrlFoto(urlFoto);
        return usuarioRepository.save(usuario);
    }

    public Usuario removerFoto(Usuario usuario) {
        usuario.setUrlFoto("/images/default-user.png");
        return usuarioRepository.save(usuario);
    }
}
