package com.ufpb.aquatrack.usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String abrirPaginaDelogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(
        @RequestParam String login, @RequestParam String senha,
        HttpSession session, Model model
    ){
        Usuario usuario = usuarioService.autenticar(login, senha);

        if (usuario == null) {
            model.addAttribute("erro", "Usuário ou senha inválidos");
            return "login";
        }

        session.setAttribute("usuario", usuario);
        if (usuario.getRole() == UsuarioRole.MASTER) { //Se o usuário não for Master ele é padrão
            return "redirect:/master"; // Uso de redirect para finalizar o POST e iniciar um novo GET com URL correta
        }

        if (usuario.getFazenda() != null) {
            return "redirect:/fazenda/" + usuario.getFazenda().getCodigo();
        } else {
            return "redirect:/inicio"; // tela "sem fazenda"
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
