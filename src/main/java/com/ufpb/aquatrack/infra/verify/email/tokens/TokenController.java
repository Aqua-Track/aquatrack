package com.ufpb.aquatrack.infra.verify.email.tokens;

import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TokenController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public TokenController(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/ativar-conta")
    public String verificarConta(@RequestParam String token, Model model) {
        if (!tokenService.validaToken(token, TokenType.ATIVACAO_CONTA)) {
            model.addAttribute("erro", "Token invalido ou expirado");
            return "login";
        }

        // Recupera o usuário associado ao token e armazena o usuário na sessão
        //Usuario usuario = tokenService.getUsuario(token);
        //session.setAttribute("usuario", usuario);

        // Passa o token para o modelo para ser usado no formulário
        System.out.println(token);
        model.addAttribute("token", token);

        return "usuario/ativacao/ativar-conta";
    }

    @PostMapping("/ativar-conta")
    public String confirmarAtivacao(@RequestParam String token, @RequestParam String senha, Model model, HttpSession session) {
        if (!tokenService.validaToken(token, TokenType.ATIVACAO_CONTA)) {
            model.addAttribute("erro", "Token invalido ou expirado");
            return "login";
        }
        Usuario usuario = tokenService.getUsuario(token, TokenType.ATIVACAO_CONTA);
        session.setAttribute("usuario", usuario);

        // Se o usuário não estiver na sessão, algo deu errado
        if (usuario == null) {
            model.addAttribute("erro", "Sessão expirada ou inválida");
            return "login";
        }

        usuarioService.definirSenha(usuario , senha);

        usuario.setContaVerificada(Boolean.TRUE);
        tokenService.consumirToken(token);
        System.out.println("Token: " + token);
        System.out.println("Senha: " + senha);
        System.out.println("Token consumido");
        usuarioService.atualizarUsuario(usuario);

        model.addAttribute("info", "Conta ativada! Faça seu primeiro login:");
        return "login";
    }

    @GetMapping("/conta-inativa")
    public String contaInativa(Model model, HttpSession session) {
        return "usuario/ativacao/conta-nao-ativada";
    }
}
