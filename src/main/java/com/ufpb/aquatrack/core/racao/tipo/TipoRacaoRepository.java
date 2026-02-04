package com.ufpb.aquatrack.core.racao.tipo;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TipoRacaoRepository extends JpaRepository<TipoRacao, Long> {

    //Lista todas as rações ativas de um usuário
    List<TipoRacao> findByFazendaAndDeletadoFalse(Fazenda fazenda);

    //Busca um ração existente, não deletada e de um usuário
    Optional<TipoRacao> findByIdAndFazendaAndDeletadoFalse(Long id, Fazenda fazenda);

    //Verifica se já existe uma ração com esse nome para o usuário
    boolean existsByFazendaAndNomeAndDeletadoFalse(Fazenda fazenda, String nome);

    //Verifica se já existe outra ração, com o mesmo nome do mesmo usuário, ignorando a ração que está sendo editada
    boolean existsByFazendaAndNomeAndIdNotAndDeletadoFalse(Fazenda fazenda, String nome, Long id);

    Optional<TipoRacao> findByIdAndDeletadoFalse(Long tipoRacaoId);
}

