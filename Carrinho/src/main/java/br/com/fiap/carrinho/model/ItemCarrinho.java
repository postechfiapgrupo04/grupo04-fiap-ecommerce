package br.com.fiap.carrinho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idProduto;
    private Integer quantidade;
    private BigDecimal preco;

    private String idUsuario;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @PrePersist
    @PreUpdate
    public void atualizarValorTotal() {
        this.valorTotal = BigDecimal.valueOf(getQuantidade()).multiply(getPreco());
    }
}
