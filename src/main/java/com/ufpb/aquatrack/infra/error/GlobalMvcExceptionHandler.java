package com.ufpb.aquatrack.infra.error;

import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalMvcExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class) //Cobre todas as url inexistentes
    public String handleNoResource(NoResourceFoundException ex, Model model, HttpServletRequest request) {

        model.addAttribute("mensagem", "Página não encontrada");
        model.addAttribute("path", request.getRequestURI());

        return "error/404";
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public String handle404(RecursoNaoEncontradoException ex, Model model, HttpServletRequest request) {

        model.addAttribute("mensagem", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handle500(Exception ex, Model model, HttpServletRequest request) {

        model.addAttribute("mensagem", "Erro interno. Tente novamente.");
        model.addAttribute("path", request.getRequestURI());

        return "error/500";
    }
}
