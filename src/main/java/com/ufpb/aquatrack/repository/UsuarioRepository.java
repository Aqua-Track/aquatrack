package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
