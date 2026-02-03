    package com.ufpb.aquatrack.infra.auth.interceptors;

    import com.ufpb.aquatrack.core.usuario.Usuario;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.stereotype.Component;
    import org.springframework.web.servlet.HandlerInterceptor;

    @Component
    public class AuthInterceptor implements HandlerInterceptor {

        //Esse metodo vai rodar antes de qualquer controller, para proteger as rotas, dizendo se é bloqueada ou não
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    String uri = request.getServletPath(); //Aqui ele vai pegar a requisição (url) que está send

            // Rotas públicas
            if (uri.equals("/login")
                    || uri.startsWith("/redefinir-senha")
                    || uri.startsWith("/ativar-conta")
                    || uri.equals("/logout")
                    || uri.startsWith("/css")
                    || uri.startsWith("/js")
                    || uri.startsWith("/images")) {
                return true;
            }

            HttpSession session = request.getSession(false); //Pega a sessão que está ativa, o false é para
            //não criar uma sessão nova, só a existente, caso exista

            Usuario usuario = (session != null) //Pegar o objeto usuário
                    ? (Usuario) session.getAttribute("usuario")
                    : null;

            if (usuario == null) { //Caso não tenha usuário é direcionado para login
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

            // Se a conta não estiver verificada, redireciona para a página de verificação
            if (!usuario.isContaVerificada() && !uri.contains("/conta-inativa")) {  // Impede o redirecionamento dentro da página de verificação
                response.sendRedirect(request.getContextPath() + "/conta-inativa");
                return false;
            }

            return true; //Caso chegue aqui o usuário existe e pode acessar a rota
        }
    }
