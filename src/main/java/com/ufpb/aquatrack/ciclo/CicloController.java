package com.ufpb.aquatrack.ciclo;

import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}/ciclo")
public class CicloController {

    private final CicloService cicloService;

    public CicloController(CicloService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping("/novo")
    public String formNovoCiclo(@PathVariable String codigo, @PathVariable Long viveiroId,
                                Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        boolean existeCicloAtivo = cicloService.existeCicloAtivo(viveiroId, usuario);

        if (existeCicloAtivo) {
            throw new IllegalStateException("Já existe ciclo ativo neste viveiro.");
        }

        model.addAttribute("codigoFazenda", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "ciclo/formulario_ciclo";
    }


    @PostMapping("/iniciar")
    public String iniciarCiclo(@PathVariable String codigo, @PathVariable Long viveiroId,
                               @RequestParam LocalDate dataPovoamento, @RequestParam int quantidadePovoada,
                               @RequestParam String laboratorio, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        cicloService.iniciarCiclo(viveiroId, dataPovoamento, quantidadePovoada, laboratorio, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";
    }


    @PostMapping("/encerrar")
    public String encerrarCiclo(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        cicloService.encerrarCiclo(viveiroId, LocalDate.now(), usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";
    }
}
