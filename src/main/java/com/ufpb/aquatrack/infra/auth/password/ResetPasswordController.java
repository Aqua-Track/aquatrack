package com.ufpb.aquatrack.infra.auth.password;

import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import com.ufpb.aquatrack.infra.email.EmailService;
import com.ufpb.aquatrack.infra.auth.tokens.TokenService;
import com.ufpb.aquatrack.infra.auth.tokens.TokenType;
import com.ufpb.aquatrack.infra.auth.tokens.TokenUsuario;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final  EmailService emailService;

    public ResetPasswordController(TokenService tokenService, UsuarioService usuarioService, EmailService emailService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    @GetMapping("/redefinir-senha")
    public String redfinirSenha() {
        return "usuario/senha/redefinir-senha";
    }

    @GetMapping("/redefinir-senha/nova-senha")
    public String novaSenha(@RequestParam String token, Model model) {

        if (!tokenService.validaToken(token, TokenType.RESET_SENHA)) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "usuario/senha/redefinir-senha";
        }

        model.addAttribute("token", token);
        return "usuario/senha/nova-senha";
    }

    @PostMapping("/redefinir-senha")
    public String enviarEmailReset(@RequestParam String login, Model model) {

        Usuario usuario;

        try {
            usuario = usuarioService.buscarUsuarioPorLogin(login);
        } catch (RecursoNaoEncontradoException e) {
            model.addAttribute("erro", "Não existe um usuário com esse login");
            return "usuario/senha/redefinir-senha";
        }

        if (usuario == null) {
            model.addAttribute("erro", "Não existe um usuário com esse login");
            return "usuario/senha/redefinir-senha";
        }

        TokenUsuario tokenUsuario = tokenService.gerarToken(usuario, TokenType.RESET_SENHA);

        emailService.enviarEmailResetSenha(usuario.getLogin(), tokenUsuario.getToken());

        model.addAttribute("info", "Enviamos um email com o link para redefinir sua senha!");

        return "usuario/senha/email-enviado";
    }

    @PostMapping("/redefinir-senha/nova-senha")
    public String salvarNovaSenha(@RequestParam String token, @RequestParam String novaSenha, Model model, HttpSession session) {

        if (!tokenService.validaToken(token, TokenType.RESET_SENHA)) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "usuario/senha/redefinir-senha";
        }

        Usuario usuario = tokenService.getUsuario(token, TokenType.RESET_SENHA);

        usuarioService.editarSenhaUsuario(usuario, novaSenha);

        tokenService.consumirToken(token);

        model.addAttribute("info", "Senha redefinida com sucesso! Faça login.");
        return "login";
    }
}
