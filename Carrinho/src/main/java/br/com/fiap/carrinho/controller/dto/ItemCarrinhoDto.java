package br.com.fiap.carrinho.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemCarrinhoDto {
    @NotBlank(message = "O codigo do produto é obrigatório.")
    private String idProduto;
    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade deve ser maior que zero.")
    private int quantidade;
    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero.")
    private BigDecimal preco;
    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long idUsuario;
    private double valorTotal;
}
