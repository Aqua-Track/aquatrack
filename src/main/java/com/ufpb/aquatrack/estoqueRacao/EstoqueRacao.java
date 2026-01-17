package com.ufpb.aquatrack.estoqueRacao;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.tipoRacao.TipoRacaoService;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "estoque_racao",
        uniqueConstraints = { //Uma fazenda não pode ter dois estoques da mesma ração
                @UniqueConstraint(
                        columnNames = {"fazenda_id", "tipo_racao_id"}
                )
        }
)
public class EstoqueRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quantidade de sacos disponíveis na fazenda.
     * Unidade inteira, sem decimais.
     */
    @Min(0)
    @Column(nullable = false)
    private int quantidadeSacos;

    @Column(nullable = false)
    private boolean deletado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_racao_id", nullable = false)
    private TipoRacao tipoRacao;

    protected EstoqueRacao() {
        this.deletado = false;
    }

    public EstoqueRacao(Fazenda fazenda, TipoRacao tipoRacao, int quantidadeSacos) {
        this.fazenda = fazenda;
        this.tipoRacao = tipoRacao;
        this.quantidadeSacos = quantidadeSacos;
        this.deletado = false;
    }
}
