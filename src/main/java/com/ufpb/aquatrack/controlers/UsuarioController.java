package com.ufpb.aquatrack.controlers;

import com.ufpb.aquatrack.models.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    @GetMapping("/login")
    public String AbrirPaginaDelogin() {
        return "login";
    }
}
