package com.ufpb.aquatrack.core.usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/usuario")
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String paginaConta(HttpSession session, Model model) {
        Usuario usuarioSessao = (Usuario) session.getAttribute("usuario");
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioSessao.getId());

        String erro = (String) session.getAttribute("erro");
        if (erro != null) {
            model.addAttribute("erro", erro);
            session.removeAttribute("erro");
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("fazenda", usuario.getFazenda());

        return "usuario/pagina-conta";
    }

    @PostMapping("/editar-nome")
    public String editarNome(
            @RequestParam String nome,
            HttpSession session
    ) {
        Usuario usuario = usuarioLogado(session);

        usuarioService.editarNomeUsuario(usuario.getId(), nome);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario";
    }

    @PostMapping("/alterar-email")
    public String alterarEmail(@RequestParam String email, HttpSession session) {
        Usuario usuario = usuarioLogado(session);

        usuarioService.editarLoginUsuario(usuario.getId(), email);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam String senhaAtual, @RequestParam String novaSenha,
            @RequestParam String confirmarSenha, HttpSession session,
            Model model) {
        Usuario usuario = usuarioLogado(session);

        session.setAttribute("usuario", usuario);
        try {
            usuarioService.editarSenhaUsuario(usuario, senhaAtual, novaSenha, confirmarSenha);
        } catch (Exception e) {
            session.setAttribute("erro", e.getMessage());
            return "redirect:/usuario";
        }


        return "redirect:/usuario";
    }

    @PostMapping("/remover-foto")
    public String removerFoto(HttpSession session) {
        Usuario usuario = usuarioLogado(session);

        usuarioService.removerFoto(usuario);
        session.setAttribute("usuario", usuario);

        return "redirect:/usuario";
    }

    private Usuario usuarioLogado(HttpSession session) {
        Usuario usuarioSessao = (Usuario) session.getAttribute("usuario");
        return usuarioService.buscarUsuarioPorId(usuarioSessao.getId());
    }
}
