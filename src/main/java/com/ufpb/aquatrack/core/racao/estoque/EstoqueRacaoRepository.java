package com.ufpb.aquatrack.core.racao.estoque;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.racao.tipo.TipoRacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstoqueRacaoRepository extends JpaRepository<EstoqueRacao, Long> {

    // Busca um estoque de ração por fazenda e tipo de ração, retornando apenas se não estiver deletado
    Optional<EstoqueRacao> findByFazendaAndTipoRacaoAndDeletadoFalse(
            Fazenda fazenda,
            TipoRacao tipoRacao
    );

    // Lista todos os estoques de ração de uma fazenda que não estejam marcados como deletados
    List<EstoqueRacao> findByFazendaAndDeletadoFalse(Fazenda fazenda);

}

