package br.com.fiap.pagamento.dto;

import br.com.fiap.pagamento.model.Pagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemCarrinhoDTO {
    private Long id;
    private String idProduto;
    private Integer quantidade;
    private Double preco;
    private Double valorTotal;

}

