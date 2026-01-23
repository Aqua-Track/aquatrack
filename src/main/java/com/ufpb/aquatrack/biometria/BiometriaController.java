package com.ufpb.aquatrack.biometria;

import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}/biometria")
public class BiometriaController {

    private final BiometriaService biometriaService;

    public BiometriaController(BiometriaService biometriaService) {
        this.biometriaService = biometriaService;
    }

    @GetMapping("/nova")
    public String formulario(@PathVariable String codigo, @PathVariable Long viveiroId, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        return "biometria/formulario_biometria";
    }

    @PostMapping
    public String salvar(
            @PathVariable String codigo, @PathVariable Long viveiroId, @RequestParam LocalDate dataBiometria,
            @RequestParam Integer quantidadeAmostrada, @RequestParam BigDecimal pesoTotalAmostra,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            biometriaService.registrarBiometria(viveiroId, usuario, dataBiometria, quantidadeAmostrada, pesoTotalAmostra);
            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            return "biometria/formulario_biometria";
        }
    }

    @GetMapping("/historico")
    public String historico(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("biometrias", biometriaService.listarBiometrias(viveiroId, usuario));
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "biometria/historico_biometria";
    }
}
