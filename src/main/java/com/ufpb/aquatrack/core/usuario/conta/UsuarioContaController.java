package com.ufpb.aquatrack.core.usuario.conta;

import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/usuario")
@Controller
public class UsuarioContaController {
    private final UsuarioContaService contaService;
    private final UsuarioService usuarioService;

    public UsuarioContaController(UsuarioContaService contaService, UsuarioService usuarioService) {
        this.contaService = contaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/conta")
    public String paginaConta(HttpSession session, Model model) {
        Usuario usuarioSessao = (Usuario) session.getAttribute("usuario");
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioSessao.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("fazenda", usuario.getFazenda());

        return "usuario/conta";
    }

    @PostMapping("/conta/editar-nome")
    public String editarNome(
            @RequestParam String nome,
            HttpSession session
    ) {
        Usuario usuario = usuarioLogado(session);

        contaService.atualizarDadosBasicos(usuario, nome);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario/conta";
    }

    @PostMapping("/conta/alterar-email")
    public String alterarEmail(@RequestParam String email, HttpSession session) {
        Usuario usuario = usuarioLogado(session);

        contaService.alterarEmail(usuario, email);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario/conta";
    }

    @PostMapping("/conta/alterar-senha")
    public String alterarSenha(
            @RequestParam String senhaAtual, @RequestParam String novaSenha,
            @RequestParam String confirmarSenha, HttpSession session
    ) {
        Usuario usuario = usuarioLogado(session);

        contaService.alterarSenha(usuario, senhaAtual, novaSenha, confirmarSenha);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario/conta";
    }

    @PostMapping("/conta/remover-foto")
    public String removerFoto(HttpSession session) {
        Usuario usuario = usuarioLogado(session);

        contaService.removerFoto(usuario);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario/conta";
    }

    private Usuario usuarioLogado(HttpSession session) {
        Usuario usuarioSessao = (Usuario) session.getAttribute("usuario");
        return usuarioService.buscarUsuarioPorId(usuarioSessao.getId());
    }
}
