package com.ufpb.aquatrack.infra.config;

import com.ufpb.aquatrack.core.usuario.UsuarioRole;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMasterInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;

    public UsuarioMasterInitializer(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {

        boolean existeMaster = usuarioRepository.existsByRole(UsuarioRole.MASTER);

        if (!existeMaster) {
            Usuario master = new Usuario();
            master.setLogin("aquatrackpb@gmail.com");
            master.setSenha(BCrypt.hashpw("track0123", BCrypt.gensalt()));
            master.setNome("Administrador");
            master.setRole(UsuarioRole.MASTER);
            master.setContaVerificada(Boolean.TRUE);

            usuarioRepository.save(master);
        }
    }
}
