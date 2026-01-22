package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.parametroQualidadeAgua.ParametroQualidadeAgua;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParametroQualidadeAguaRepository extends JpaRepository<ParametroQualidadeAgua, Long> {

    //Busca todos os parâmetros de qualidade da água de um usuário não deletados, por nome em ordem crescente
    List<ParametroQualidadeAgua> findByUsuarioAndDeletadoFalseOrderByNomeAsc(Usuario usuario);

    //Verifica se já existe um parâmetro com o mesmo nome para o usuário que não esteja marcado como deletado
    boolean existsByUsuarioAndNomeAndDeletadoFalse(Usuario usuario, String nome);
}
