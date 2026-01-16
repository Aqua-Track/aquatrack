package com.ufpb.aquatrack.controlers;

import com.ufpb.aquatrack.models.Fazenda;
import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.services.FazendaService;
import com.ufpb.aquatrack.services.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViveiroController {

    private final ViveiroService viveiroService;
    private final FazendaService fazendaService;

    public ViveiroController(ViveiroService viveiroService, FazendaService fazendaService) {
        this.viveiroService = viveiroService;
        this.fazendaService = fazendaService;
    }


    @GetMapping("/fazenda/{id}/cadastrar-viveiro")
    public String formularioViveiro(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorId(id);

        // segurança
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        model.addAttribute("idFazenda", id);

        return "viveiros/formulario_viveiro";
    }


    @PostMapping("/fazenda/{id}/cadastrar-viveiro")
    public String cadastrarViveiro(
            @PathVariable Long id, @RequestParam double area,
            @RequestParam int idPersonalizado, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            String tag = "V-" + idPersonalizado;
            viveiroService.criarViveiro(id, tag, area, usuario);

            return "redirect:/fazenda/" + id;

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("idFazenda", id);

            return "viveiros/formulario_viveiro";
        }
    }
}
