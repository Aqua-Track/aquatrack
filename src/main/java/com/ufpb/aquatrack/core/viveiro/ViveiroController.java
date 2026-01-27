package com.ufpb.aquatrack.core.viveiro;

import com.ufpb.aquatrack.core.biometria.Biometria;
import com.ufpb.aquatrack.core.biometria.BiometriaService;
import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.racao.consumo.ConsumoRacao;
import com.ufpb.aquatrack.core.racao.consumo.ConsumoRacaoService;
import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.instrucao.Instrucao;
import com.ufpb.aquatrack.core.instrucao.InstrucaoService;
import com.ufpb.aquatrack.core.qualidadeAgua.QualidadeAgua;
import com.ufpb.aquatrack.core.qualidadeAgua.QualidadeAguaService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
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
    private final BiometriaService biometriaService;
    private final QualidadeAguaService qualidadeAguaService;
    private final InstrucaoService instrucaoService;


    public ViveiroController(ViveiroService viveiroService, FazendaService fazendaService,
                             CicloService cicloService, ConsumoRacaoService consumoRacaoService,
                             BiometriaService biometriaService, QualidadeAguaService qualidadeAguaService,
                             InstrucaoService instrucaoService
    ) {
        this.viveiroService = viveiroService;
        this.fazendaService = fazendaService;
        this.cicloService = cicloService;
        this.consumoRacaoService = consumoRacaoService;
        this.biometriaService = biometriaService;
        this.qualidadeAguaService = qualidadeAguaService;
        this.instrucaoService = instrucaoService;
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
    public String abrirViveiro(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);
        Ciclo cicloAtivo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        BigDecimal biomassa = null;
        BigDecimal sobrevivencia = null;
        List<ConsumoRacao> consumos = List.of();
        List<Biometria> biometrias = List.of();

        // ===== BIOMETRIA =====
        if (cicloAtivo != null) {
            biometrias = biometriaService.listarBiometrias(viveiroId, usuario);
            int total = biometrias.size();
            if (total > 0) {
                model.addAttribute("ultimaBiometria", biometrias.get(total - 1));
            }
            if (total > 1) {
                model.addAttribute("penultimaBiometria", biometrias.get(total - 2));
            }
        }

        // ===== QUALIDADE DA ÁGUA =====
        if (cicloAtivo != null) {
            QualidadeAgua ultimaAgua = qualidadeAguaService.buscarUltima(cicloAtivo);
            QualidadeAgua penultimaAgua = qualidadeAguaService.buscarPenultima(cicloAtivo);

            model.addAttribute("ultimaAgua", ultimaAgua);
            model.addAttribute("penultimaAgua", penultimaAgua);
        }

        // ===== CONSUMO / BIOMASSA / SOBREVIVÊNCIA =====
        if (cicloAtivo != null) {
            consumos = consumoRacaoService.listarConsumosDoCiclo(viveiroId, usuario);
            if (!biometrias.isEmpty()) {
                Biometria ultimaBiometria = biometrias.getLast();

                biomassa = cicloService.calcularBiomassaKg(ultimaBiometria, cicloAtivo);

                if (!consumos.isEmpty()) {
                    sobrevivencia = cicloService.calcularSobrevivencia(
                            ultimaBiometria, cicloAtivo, consumos.getFirst()
                    );
                }
            }
        }

        BigDecimal consumoTotal = consumoRacaoService.calcularConsumoTotal(consumos);
        Map<String, BigDecimal> consumoPorTipo =
                consumoRacaoService.calcularConsumoPorTipo(consumos);

        // ===== INSTRUÇÕES (NÃO DEPENDE DE CICLO) =====
        List<Instrucao> instrucoesRecentes =
                instrucaoService.listar3Ultimas(codigo, viveiroId, usuario);

        // ===== MODEL =====
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", cicloAtivo);
        model.addAttribute("abaAtiva", "detalhes");
        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiro", viveiro);

        model.addAttribute("biomassa", biomassa);
        model.addAttribute("sobrevivencia", sobrevivencia);
        model.addAttribute("consumos", consumos);
        model.addAttribute("consumoTotal", consumoTotal);
        model.addAttribute("consumoPorTipo", consumoPorTipo);

        model.addAttribute("instrucoes", instrucoesRecentes);

        return "viveiros/pagina_viveiro";
    }



    @PostMapping("/fazenda/{codigo}/viveiro/{idViveiro}/remover")
    public String removerViveiro(@PathVariable String codigo, @PathVariable Long idViveiro, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        viveiroService.removerViveiro(idViveiro, usuario);

        return "redirect:/fazenda/" + codigo;
    }


}
