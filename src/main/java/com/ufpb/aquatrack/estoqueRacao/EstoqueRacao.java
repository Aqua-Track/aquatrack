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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "estoque_racao",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fazenda_id", "tipo_racao_id"})
        }
)
public class EstoqueRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantidadeKg;

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
        this.quantidadeKg = BigDecimal.ZERO;
    }

    public EstoqueRacao(Fazenda fazenda, TipoRacao tipoRacao, BigDecimal quantidadeKg) {
        this.fazenda = fazenda;
        this.tipoRacao = tipoRacao;
        this.quantidadeKg = quantidadeKg;
        this.deletado = false;
    }

    public void adicionarKg(BigDecimal kg) {
        if (kg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        this.quantidadeKg = this.quantidadeKg.add(kg);
    }

    public void consumirKg(BigDecimal kg) {
        if (kg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        if (this.quantidadeKg.compareTo(kg) < 0) {
            throw new IllegalStateException("Estoque insuficiente");
        }

        this.quantidadeKg = this.quantidadeKg.subtract(kg);
    }


    public BigDecimal getQuantidadeSacos() {
        return quantidadeKg.divide(tipoRacao.getKgPorSaco(), 2, RoundingMode.HALF_UP);
    }
}

