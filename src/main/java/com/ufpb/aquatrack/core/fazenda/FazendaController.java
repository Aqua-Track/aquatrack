package com.ufpb.aquatrack.core.fazenda;

import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.racao.estoque.EstoqueRacaoService;
import com.ufpb.aquatrack.core.usuario.UsuarioService;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroService;
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
public class FazendaController {

    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final EstoqueRacaoService estoqueRacaoService;
    private final CicloService cicloService;
    private final UsuarioService usuarioService;

    public FazendaController(FazendaService fazendaService, ViveiroService viveiroService,
                             EstoqueRacaoService estoqueRacaoService, CicloService cicloService, UsuarioService usuarioService) {
        this.fazendaService = fazendaService;
        this.viveiroService = viveiroService;
        this.estoqueRacaoService = estoqueRacaoService;
        this.cicloService = cicloService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/inicio")
    public String semFazenda(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Usuario usuarioAtualizado = usuarioService.buscarUsuarioPorId(usuario.getId());

        if (usuarioAtualizado.getFazenda() != null) {
            return "redirect:/fazenda/" + usuario.getFazenda().getCodigo();
        }

        return "fazendas/sem_fazenda"; // tela da imagem
    }

    @GetMapping("/fazendas/cadastrar")
    public String formularioFazenda() {
        return "fazendas/formulario_fazendas";
    }

    @PostMapping("/fazendas/cadastrar")
    public String criarFazenda(
            @RequestParam String nome, @RequestParam
            String localizacao, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Usuario usuarioAtualizado = usuarioService.buscarUsuarioPorId(usuario.getId());
        try {
            Fazenda fazenda = fazendaService.criarFazenda(nome, localizacao, usuarioAtualizado  );
            return "redirect:/fazenda/" + fazenda.getCodigo();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "fazendas/formulario_fazendas";
        }
    }

    @GetMapping("/fazenda/{codigo}")
    public String abrirFazenda(@PathVariable String codigo, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Usuario usuarioAtualizado = usuarioService.buscarUsuarioPorId(usuario.getId());
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuarioAtualizado.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        model.addAttribute("fazenda", fazenda);
        List<Viveiro> viveiros = viveiroService.listarViveiros(fazenda.getId(), usuarioAtualizado);

        model.addAttribute("viveiros", viveiros);
        model.addAttribute("estoques", estoqueRacaoService.listarEstoqueDaFazenda(fazenda.getId(), usuarioAtualizado));

        BigDecimal valorTotalEstoque = estoqueRacaoService.totalEstoque(fazenda.getId(), usuario);
        model.addAttribute("valorTotalEstoque", valorTotalEstoque);

        Map<Long, Boolean> statusCicloPorViveiro = cicloService.obterStatusCicloPorViveiro(viveiros, usuarioAtualizado);
        model.addAttribute("statusCiclo", statusCicloPorViveiro);

        return "fazendas/pagina_fazenda";
    }


    @PostMapping("/fazenda/{codigo}/remover")
    public String removerFazenda(@PathVariable String codigo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        fazendaService.deletarFazenda(fazenda.getId());
        return "redirect:/inicio";
    }
}
