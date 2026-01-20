package com.ufpb.aquatrack.viveiro;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.consumo.ConsumoRacao;
import com.ufpb.aquatrack.consumo.ConsumoRacaoService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class ViveiroController {

    private final ViveiroService viveiroService;
    private final FazendaService fazendaService;
    private final CicloService cicloService;
    private final ConsumoRacaoService consumoRacaoService;

    public ViveiroController(ViveiroService viveiroService, FazendaService fazendaService,
                             CicloService cicloService, ConsumoRacaoService consumoRacaoService) {
        this.viveiroService = viveiroService;
        this.fazendaService = fazendaService;
        this.cicloService = cicloService;
        this.consumoRacaoService = consumoRacaoService;
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

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/abrirViveiro")
    public String abrirViveiro(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");


        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);

        Ciclo cicloAtivo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        List<ConsumoRacao> consumos = null;

        if (cicloAtivo != null) {
            consumos = consumoRacaoService.listarConsumosDoCiclo(viveiroId, usuario);
        }
        BigDecimal consumoTotal = consumoRacaoService.calcularConsumoTotal(consumos);
        Map<String, BigDecimal> consumoPorTipo = consumoRacaoService.calcularConsumoPorTipo(consumos);

        model.addAttribute("consumos", consumos);
        model.addAttribute("consumoTotal", consumoTotal);
        model.addAttribute("consumoPorTipo", consumoPorTipo);
        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiro", viveiro);
        model.addAttribute("ciclo", cicloAtivo);

        return "viveiros/pagina_viveiro";
    }

    @PostMapping("/fazenda/{codigo}/viveiro/{idViveiro}/remover")
    public String removerViveiro(@PathVariable String codigo, @PathVariable Long idViveiro, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        viveiroService.removerViveiro(idViveiro, usuario);

        return "redirect:/fazenda/" + codigo;
    }


}
