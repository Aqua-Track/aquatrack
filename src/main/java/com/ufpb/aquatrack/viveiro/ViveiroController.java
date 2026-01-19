package com.ufpb.aquatrack.viveiro;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.fazenda.FazendaService;
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


    @GetMapping("/fazenda/{codigo}/cadastrar-viveiro")
    public String formularioViveiro(@PathVariable String codigo, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        // segurança
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        model.addAttribute("codigo", codigo);

        return "viveiros/formulario_viveiro";
    }


    @PostMapping("/fazenda/{codigo}/cadastrar-viveiro")
    public String cadastrarViveiro(
            @PathVariable String codigo, @RequestParam double area,
            @RequestParam int idPersonalizado, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            String tag = "V-" + idPersonalizado;
            viveiroService.criarViveiro(codigo, tag, area, usuario);

            return "redirect:/fazenda/" + codigo;

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);

            return "viveiros/formulario_viveiro";
        }
    }
}
