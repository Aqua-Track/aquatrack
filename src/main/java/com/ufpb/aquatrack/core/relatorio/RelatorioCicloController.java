package com.ufpb.aquatrack.core.relatorio;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RelatorioCicloController {

    private final RelatorioCicloRepository repository;
    private final RelatorioDocumentoService documentoService;
    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final CicloService cicloService;

    public RelatorioCicloController(RelatorioCicloRepository repository,
            FazendaService fazendaService, ViveiroService viveiroService,
            RelatorioDocumentoService documentoService, CicloService cicloService
    ) {
        this.repository = repository;
        this.documentoService = documentoService;
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
        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiro", viveiro);

        return "relatorio/lista";
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/relatorios/{relatorioId}")
    public String previewRelatorio(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable Long relatorioId, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        validarAcesso(usuario, fazenda, viveiroId);

        RelatorioCiclo relatorio = repository.findById(relatorioId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));

        model.addAttribute("relatorio", relatorio);
        return "relatorio/preview";
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/relatorios/{relatorioId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable Long relatorioId, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        validarAcesso(usuario, fazenda, viveiroId);

        RelatorioCiclo relatorio = repository.findById(relatorioId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));

        byte[] pdf = documentoService.gerarPdf(relatorio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=relatorio-" + relatorio.getTagViveiro() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private void validarAcesso(Usuario usuario, Fazenda fazenda, Long viveiroId) {
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);
        if (!viveiro.getFazenda().getId().equals(fazenda.getId())) {
            throw new IllegalArgumentException("Viveiro não pertence à fazenda");
        }
    }
}
