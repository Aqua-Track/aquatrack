package com.ufpb.aquatrack.qualidadeAgua;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.fazenda.FazendaService;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.viveiro.Viveiro;
import com.ufpb.aquatrack.viveiro.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}")
public class QualidadeAguaController {

    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final CicloService cicloService;
    private final QualidadeAguaService service;

    public QualidadeAguaController(
            FazendaService fazendaService,
            ViveiroService viveiroService,
            CicloService cicloService,
            QualidadeAguaService service
    ) {
        this.fazendaService = fazendaService;
        this.viveiroService = viveiroService;
        this.cicloService = cicloService;
        this.service = service;
    }

    @GetMapping("/qualidade-agua/novo")
    public String formulario(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            HttpSession session,
            Model model
    ) {
        validarAcesso(codigo, viveiroId, session);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        return "qualidadeAgua/formulario_qualidade_agua";
    }

    @PostMapping("/qualidade-agua")
    public String salvar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @RequestParam LocalDate dataColeta, @RequestParam double amonia,
            @RequestParam double nitrito, @RequestParam double ph, @RequestParam double alcalinidade,
            @RequestParam double salinidade, @RequestParam double oxigenio, HttpSession session
    ) {
        validarAcesso(codigo, viveiroId, session);

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, (Usuario) session.getAttribute("usuario"));

        service.cadastrar(ciclo, dataColeta, amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";
    }


    @GetMapping("/qualidade-agua/historico")
    public String historico(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            HttpSession session,
            Model model
    ) {
        validarAcesso(codigo, viveiroId, session);

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        List<QualidadeAgua> historico = service.listarHistorico(ciclo);

        model.addAttribute("historicoQualidade", historico);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "qualidadeAgua/historico_qualidade_agua";
    }
    private void validarAcesso(String codigo, Long viveiroId, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }
        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);
    }
}
