package com.ufpb.aquatrack.infra.auth.activation;

import com.ufpb.aquatrack.infra.auth.tokens.TokenService;
import com.ufpb.aquatrack.infra.auth.tokens.TokenType;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccontVerifyController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AccontVerifyController(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/ativar-conta")
    public String verificarConta(@RequestParam String token, Model model) {
        if (!tokenService.validaToken(token, TokenType.ATIVACAO_CONTA)) {
            model.addAttribute("erro", "Token invalido ou expirado");
            return "login";
        }

        // Passa o token para o modelo para ser usado no formulário
        System.out.println(token);
        model.addAttribute("token", token);

        return "usuario/ativacao/ativar-conta";
    }

    @PostMapping("/ativar-conta")
    public String confirmarAtivacao(@RequestParam String token, @RequestParam String senha, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
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

        try {
            usuarioService.editarSenhaUsuario(usuario , senha);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/ativar-conta?token=" + token;
        }

        usuario.setContaVerificada(Boolean.TRUE);
        tokenService.consumirToken(token);
        usuarioService.atualizarUsuario(usuario);

        redirectAttributes.addFlashAttribute("info", "Conta ativada! Faça seu login:");
        return "redirect:/login";
    }

    @GetMapping("/conta-inativa")
    public String contaInativa() {
        return "usuario/ativacao/conta-nao-ativada";
    }
}
