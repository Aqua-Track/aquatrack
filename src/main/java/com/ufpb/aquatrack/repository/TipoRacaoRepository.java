package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.models.TipoRacao;
import com.ufpb.aquatrack.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoRacaoRepository extends JpaRepository<TipoRacao, Long> {

    //Lista todas as rações ativas de um usuário
    List<TipoRacao> findByUsuarioAndDeletadoFalse(Usuario usuario);

    //Busca um ração existente, não deletada e de um usuário
    Optional<TipoRacao> findByIdAndUsuarioAndDeletadoFalse(Long id, Usuario usuario);

    //Verifica se já existe uma ração com esse nome para o usuário
    boolean existsByUsuarioAndNomeAndDeletadoFalse(Usuario usuario, String nome);

    //Verifica se já existe outra ração, com o mesmo nome do mesmo usuário, ignorando a ração que está sendo editada
    boolean existsByUsuarioAndNomeAndIdNotAndDeletadoFalse(
            Usuario usuario,
            String nome,
            Long id
    );
}
