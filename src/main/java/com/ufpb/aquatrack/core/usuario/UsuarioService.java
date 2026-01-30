package com.ufpb.aquatrack.core.usuario;

import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import com.ufpb.aquatrack.infra.email.EmailService;
import com.ufpb.aquatrack.infra.auth.tokens.TokenService;
import com.ufpb.aquatrack.infra.auth.tokens.TokenType;
import com.ufpb.aquatrack.infra.auth.tokens.TokenUsuario;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, TokenService tokenService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    public void cadastrarUsuario(String nome, String login, String senha, UsuarioRole role) {

        if (usuarioRepository.findByLoginAndDeletadoFalse(login).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com esse login.");
        }

        validarSenha(senha);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        usuario.setRole(role);
        usuario.setContaVerificada(Boolean.FALSE); //Define o novo usuário como "Não ativado"
        usuarioRepository.save(usuario);

        TokenUsuario tokenUsuario = tokenService.gerarToken(usuario, TokenType.ATIVACAO_CONTA);

        // Enviar o e-mail de ativação
        emailService.enviarEmailAtivacao(usuario.getLogin(), tokenUsuario.getToken());
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    public Usuario buscarUsuarioPorLogin(String login) {
        return usuarioRepository.findByLoginAndDeletadoFalse(login)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    public void editarNomeUsuario(Long idUsuarioParaEditar, String nome) {
        Usuario usuario = buscarUsuarioPorId(idUsuarioParaEditar);
        if (nome != null) {
            usuario.setNome(nome);
        }
        usuarioRepository.save(usuario);
    }

    public void editarLoginUsuario(Long idUsuarioParaEditar, String login) {
        Usuario usuario = buscarUsuarioPorId(idUsuarioParaEditar);
        if (login != null) {
            usuario.setLogin(login);
        }
        usuarioRepository.save(usuario);
    }

    public Usuario editarSenhaUsuario(Usuario usuario, String senha) {
        validarSenha(senha);
        usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        return usuarioRepository.save(usuario);
    }

    public Usuario editarSenhaUsuario(Usuario usuario, String senhaAtual, String novaSenha, String confirmarSenha) {

        validarSenha(novaSenha);

        if (senhaAtual == null || novaSenha == null || confirmarSenha == null) throw new IllegalArgumentException("Todos os campos de senha devem ser preenchidos.");
        if (!BCrypt.checkpw(senhaAtual, usuario.getSenha())) throw new IllegalArgumentException("Senha atual incorreta.");
        if (!novaSenha.equals(confirmarSenha)) throw new IllegalArgumentException("As senhas não coincidem.");
        if (BCrypt.checkpw(novaSenha, usuario.getSenha())) throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");

        usuario.setSenha(BCrypt.hashpw(novaSenha, BCrypt.gensalt()));
        return usuarioRepository.save(usuario);
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

    public void editarUsuarioPeloMaster(Usuario master, Long idUsuarioEditado, String nome, String login, String novaSenhaUsuario, String senhaMaster) {

        verificaMaster(master, senhaMaster);

        Usuario usuario = buscarUsuarioPorId(idUsuarioEditado);
        usuario.setNome(nome);
        usuario.setLogin(login);

        if (novaSenhaUsuario != null && !novaSenhaUsuario.isBlank()) {
            validarSenha(novaSenhaUsuario);
            usuario.setSenha(BCrypt.hashpw(novaSenhaUsuario, BCrypt.gensalt()));
        }

        usuarioRepository.save(usuario);
    }

    public void verificaMaster(Usuario master, String senhaMaster) {
        // Segurança básica
        if (master == null || master.getRole() != UsuarioRole.MASTER) {
            throw new IllegalArgumentException("Permissão negada.");
        }

        // Valida senha do MASTER
        if (!BCrypt.checkpw(senhaMaster, master.getSenha())) {
            throw new IllegalArgumentException("Senha do administrador inválida.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || !senha.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new IllegalArgumentException(
                    "A senha deve ter no mínimo 8 caracteres, contendo letras maiúsculas, minúsculas e números."
            );
        }
    }
}

