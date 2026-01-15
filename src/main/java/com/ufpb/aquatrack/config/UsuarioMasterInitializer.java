package com.ufpb.aquatrack.config;

import com.ufpb.aquatrack.enums.UsuarioRole;
import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.repository.UsuarioRepository;
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
            master.setLogin("master@gmail.com");
            master.setSenha(BCrypt.hashpw("track0123", BCrypt.gensalt()));
            master.setNome("Administrador");
            master.setRole(UsuarioRole.MASTER);

            usuarioRepository.save(master);

        }
    }
}
