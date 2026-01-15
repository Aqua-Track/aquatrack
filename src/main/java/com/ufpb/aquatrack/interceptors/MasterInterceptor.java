package com.ufpb.aquatrack.interceptors;

import com.ufpb.aquatrack.enums.UsuarioRole;
import com.ufpb.aquatrack.models.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MasterInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false); //Pega a sessão que está ativa, o false é para
        //não criar uma sessão nova, só a existente, caso exista

        Usuario usuario = (session != null) //Pegar o objeto usuário
                ? (Usuario) session.getAttribute("usuario")
                : null;

        // Segurança extra: se não houver usuário, bloqueia
        if (usuario == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Se o usuário NÃO for MASTER, bloqueia
        if (usuario.getRole() != UsuarioRole.MASTER) {
            response.sendRedirect("/fazendas"); // ou /403, se quiser depois
            return false;
        }

        // Usuário é MASTER → pode acessar
        return true;
    }
}
