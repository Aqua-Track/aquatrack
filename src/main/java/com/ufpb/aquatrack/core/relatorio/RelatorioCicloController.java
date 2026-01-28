package com.ufpb.aquatrack.core.relatorio;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RelatorioCicloController {

    private final RelatorioCicloRepository repository;
    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final CicloService cicloService;

    public RelatorioCicloController(RelatorioCicloRepository repository,
            FazendaService fazendaService, ViveiroService viveiroService,
            CicloService cicloService
    ) {
        this.repository = repository;
        this.fazendaService = fazendaService;
        this.viveiroService = viveiroService;
        this.cicloService = cicloService;
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/relatorios")
    public String listarRelatorios(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Ciclo cicloAtivo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);

        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiro", viveiro);
        model.addAttribute("relatorios",
                repository.findByViveiroIdAndDeletadoFalseOrderByDataEncerramentoDesc(viveiroId)
        );

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", cicloAtivo);
        model.addAttribute("abaAtiva", "relatorio");


        return "relatorio/lista";
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/relatorios/{relatorioId}")
    public String visualizarRelatorio(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            @PathVariable Long relatorioId,
            HttpSession session,
            Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);

        RelatorioCiclo relatorio =
                repository.findByIdAndViveiroId(relatorioId, viveiroId)
                        .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));

        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiro", viveiro);
        model.addAttribute("relatorio", relatorio);

        return "relatorio/visualizar";
    }

}
