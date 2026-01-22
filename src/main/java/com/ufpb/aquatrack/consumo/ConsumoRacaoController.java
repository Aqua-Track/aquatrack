package com.ufpb.aquatrack.consumo;

import com.ufpb.aquatrack.estoqueRacao.EstoqueRacao;
import com.ufpb.aquatrack.estoqueRacao.EstoqueRacaoService;
import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}/consumo-racao")
public class ConsumoRacaoController {

    private final ConsumoRacaoService consumoRacaoService;
    private final EstoqueRacaoService estoqueRacaoService;

    public ConsumoRacaoController(ConsumoRacaoService consumoRacaoService, EstoqueRacaoService estoqueRacaoService) {
        this.consumoRacaoService = consumoRacaoService;
        this.estoqueRacaoService = estoqueRacaoService;
    }

    @GetMapping("/novo")
    public String formConsumo(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<EstoqueRacao> estoques = estoqueRacaoService.listarEstoqueDaFazendaPorViveiro(viveiroId);

        model.addAttribute("codigoFazenda", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("estoques", estoques);
        model.addAttribute("dataHoje", LocalDate.now());

        return "consumo/form_consumo_racao";
    }

    @PostMapping("/registrar")
    public String registrarConsumo(@PathVariable String codigo, @PathVariable Long viveiroId,
                                   @RequestParam Long tipoRacaoId, @RequestParam BigDecimal quantidadeKg,
                                   @RequestParam LocalDate dataConsumo, HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            consumoRacaoService.registrarConsumo(viveiroId, tipoRacaoId, quantidadeKg, dataConsumo, usuario);

            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";

        } catch (IllegalArgumentException | IllegalStateException e) {

            List<EstoqueRacao> estoques = estoqueRacaoService.listarEstoqueDaFazendaPorViveiro(viveiroId);

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigoFazenda", codigo);
            model.addAttribute("viveiroId", viveiroId);
            model.addAttribute("estoques", estoques);
            model.addAttribute("dataHoje", LocalDate.now());

            return "consumo/form_consumo_racao";
        }
    }
}
