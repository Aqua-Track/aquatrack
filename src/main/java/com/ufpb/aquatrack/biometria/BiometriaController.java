package com.ufpb.aquatrack.biometria;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
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
    private final CicloService cicloService;

    public BiometriaController(BiometriaService biometriaService, CicloService cicloService) {
        this.biometriaService = biometriaService;
        this.cicloService = cicloService;
    }

    @GetMapping("/nova")
    public String formulario(@PathVariable String codigo, @PathVariable Long viveiroId, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        return "biometria/formulario_biometria";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable String codigo, @PathVariable Long viveiroId, @PathVariable Long id,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Biometria biometria = biometriaService.buscarPorId(id, usuario);

        model.addAttribute("biometria", biometria);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "biometria/formulario_biometria";
    }

    @PostMapping("/salvar")
    public String salvar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @RequestParam LocalDate dataBiometria, @RequestParam Integer quantidadeAmostrada,
            @RequestParam BigDecimal pesoTotalAmostra, @RequestParam(required = false) Long id,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            if (id == null) { //Cadastrar
                biometriaService.registrarBiometria(viveiroId, usuario, dataBiometria, quantidadeAmostrada, pesoTotalAmostra);
            } else { //Editar
                biometriaService.atualizarBiometria(id, usuario, dataBiometria, quantidadeAmostrada, pesoTotalAmostra);
            }

            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            if (id != null) {
                model.addAttribute("biometria", biometriaService.buscarPorId(id, usuario));
            }
            return "biometria/formulario_biometria";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String codigo, @PathVariable Long viveiroId,
                          @PathVariable Long id, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        biometriaService.excluirBiometria(id, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/biometria/historico";
    }

    @GetMapping("/historico")
    public String historico(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("abaAtiva", "biometria");
        model.addAttribute("biometrias", biometriaService.listarBiometrias(viveiroId, usuario));

        return "biometria/historico_biometria";
    }
}
