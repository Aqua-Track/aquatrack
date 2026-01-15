package com.ufpb.aquatrack.config;

import com.ufpb.aquatrack.interceptors.AuthInterceptor;
import com.ufpb.aquatrack.interceptors.MasterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final MasterInterceptor masterInterceptor;

    public WebConfig(AuthInterceptor authInterceptor, MasterInterceptor masterInterceptor) {
        this.authInterceptor = authInterceptor;
        this.masterInterceptor = masterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Registra o AuthInterceptor no Spring
        // A partir daqui o Spring sabe que esse interceptor existe e deve ser executado antes dos controllers

        // Interceptor geral: exige usuário logado
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");

        // Interceptor específico: exige role MASTER
        registry.addInterceptor(masterInterceptor)
                .addPathPatterns("/master/**");

        // Esses 2 blocos a cima define que o interceptor será aplicado a TODAS as rotas
        // "/**" significa: qualquer URL da aplicação
    }
}
