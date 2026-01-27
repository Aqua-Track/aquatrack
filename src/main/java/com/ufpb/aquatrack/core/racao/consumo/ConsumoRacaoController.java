package com.ufpb.aquatrack.core.racao.consumo;

import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.racao.estoque.EstoqueRacao;
import com.ufpb.aquatrack.core.racao.estoque.EstoqueRacaoService;
import com.ufpb.aquatrack.core.usuario.Usuario;
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
    private final CicloService cicloService;

    public ConsumoRacaoController(ConsumoRacaoService consumoRacaoService,
                 EstoqueRacaoService estoqueRacaoService, CicloService cicloService
    ) {
        this.consumoRacaoService = consumoRacaoService;
        this.estoqueRacaoService = estoqueRacaoService;
        this.cicloService = cicloService;
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

    @GetMapping("/historico")
    public String historico(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", cicloService.buscarCicloAtivo(viveiroId, usuario));
        model.addAttribute("abaAtiva", "consumo");
        model.addAttribute("consumos", consumoRacaoService.listarConsumosDoCiclo(viveiroId, usuario));

        return "consumo/historico_consumo_racao";
    }


    @PostMapping("/{id}/excluir")
    public String excluirConsumo(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable Long id, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        consumoRacaoService.excluirConsumo(id, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/consumo-racao/historico";
    }

}
