package com.ufpb.aquatrack.parametroQualidadeAgua;

import com.ufpb.aquatrack.qualidadeAgua.QualidadeAguaService;
import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}/qualidade-agua/parametros")
public class ParametroQualidadeAguaController {

    private final QualidadeAguaService qualidadeAguaService;

    public ParametroQualidadeAguaController(QualidadeAguaService qualidadeAguaService) {
        this.qualidadeAguaService = qualidadeAguaService;
    }

    @GetMapping
    public String listar(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("parametros", qualidadeAguaService.listarParametrosDoUsuario(usuario));
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "qualidadeAgua/parametros/lista_parametros";
    }

    @GetMapping("/novo")
    public String formularioNovo(@PathVariable String codigo, @PathVariable Long viveiroId, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        return "qualidadeAgua/parametros/formulario_parametros";
    }

    @PostMapping
    public String salvar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @RequestParam String nome, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            qualidadeAguaService.cadastrarParametro(usuario, nome);
            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/qualidade-agua/parametros";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            return "qualidadeAgua/parametros/formulario_parametros";
        }
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable Long id, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("parametro", qualidadeAguaService.buscarParametroPorId(id, usuario));
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "qualidadeAgua/parametros/formulario_parametros";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable Long id, @RequestParam String nome,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            qualidadeAguaService.editarParametro(id, usuario, nome);
            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/qualidade-agua/parametros";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("parametro", qualidadeAguaService.buscarParametroPorId(id, usuario));
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            return "qualidadeAgua/parametros/formulario_parametros";
        }
    }

    @PostMapping("/{id}/remover")
    public String remover(
            @PathVariable String codigo,
            @PathVariable Long viveiroId, @PathVariable Long id, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        qualidadeAguaService.removerParametro(id, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/qualidade-agua/parametros";
    }
}
