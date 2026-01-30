package com.ufpb.aquatrack.core.usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/master")
public class MasterController {

    private final UsuarioService usuarioService;

    public MasterController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping()
    public String mostrarPaginaMaster(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inválido.");
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarioService.listarUsuarios());

        return "master/pagina_master";
    }

    @PostMapping("/removerUsuario")
    public String removerUsuario(@RequestParam Long id) {
        usuarioService.removerUsuario(id);
        return "redirect:/master";
    }

    @GetMapping("/cadastrar")
    public String abrirCadastroUsuario() {
        return "master/formulario_usuario_novo";
    }

    @PostMapping("/cadastrar")
    public String cadastrarUsuario(
            @RequestParam String nome, @RequestParam String login, @RequestParam String senha,
            @RequestParam String confirmarSenha, @RequestParam UsuarioRole role, Model model
    ) {

        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return "master/formulario_usuario_novo";
        }

        try {
            usuarioService.cadastrarUsuario(nome, login, senha, role);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "master/formulario_usuario_novo";
        }

        return "redirect:/master";
    }

    @GetMapping("/editar/{id}")
    public String abrirEditarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        return "master/formulario_usuario_editar";
    }

    @PostMapping("/editar")
    public String editarUsuario(
            @RequestParam Long id, @RequestParam String nome, @RequestParam String login,
            @RequestParam(required = false) String senha, @RequestParam String senhaMaster,
            HttpSession session, Model model
    ) {
        Usuario master = (Usuario) session.getAttribute("usuario");
        try {
            usuarioService.editarUsuarioPeloMaster(master, id, nome, login, senha, senhaMaster);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuarioService.buscarUsuarioPorId(id));
            return "master/formulario_usuario_editar";
        }
        return "redirect:/master";
    }
}