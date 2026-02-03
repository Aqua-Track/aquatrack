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
            String uri = request.getRequestURI(); //Aqui ele vai pegar a requisição (url) que está send
            String contextPath = request.getContextPath(); //Aqui ele pega o path da aplicação
            // localhost → ""
            // produção → "/aquatrack"

            // Se existir contextPath, remove ele da URI
            // "/aquatrack/redefinir-senha" vira "/redefinir-senha"
            if (contextPath != null && !contextPath.isEmpty()) {
                uri = uri.substring(contextPath.length());
            }

            //A partir daqui a url fica padronizada tanto para localhost tanto para aquatrackpb

            // Rotas públicas
            if (uri.equals("/login") || uri.equals("/logout")
                    || uri.startsWith("/css")
                    || uri.startsWith("/js")
                    || uri.startsWith("/images") || uri.startsWith("/ativar-conta") || uri.startsWith("/redefinir-senha"))  {
                return true;
            }

            HttpSession session = request.getSession(false); //Pega a sessão que está ativa, o false é para
            //não criar uma sessão nova, só a existente, caso exista

            Usuario usuario = (session != null) //Pegar o objeto usuário
                    ? (Usuario) session.getAttribute("usuario")
                    : null;

            if (usuario == null) { //Caso não tenha usuário é direcionado para login
                response.sendRedirect("/login");
                return false;
            }

            // Se a conta não estiver verificada, redireciona para a página de verificação
            if (!usuario.isContaVerificada() && !uri.contains("/conta-inativa")) {  // Impede o redirecionamento dentro da página de verificação
                response.sendRedirect("/conta-inativa");
                return false;
            }

            return true; //Caso chegue aqui o usuário existe e pode acessar a rota
        }
    }
