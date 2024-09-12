package br.com.fiap.pagamento.model;

import br.com.fiap.pagamento.dto.ItemCarrinhoDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroCartao;
    private String validadeCartao;
    private double valorPago;

    private String cupomDescontoAplicado;
    private double valorDescontado;

    private double percentualDesconto;

    private String usuarioId;

    // Marcado como @Transient porque não queremos persistir ItemCarrinhoDTO no banco de dados
    @Transient
    private List<ItemCarrinhoDTO> itensCarrinho;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getValidadeCartao() {
        return validadeCartao;
    }

    public void setValidadeCartao(String validadeCartao) {
        this.validadeCartao = validadeCartao;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public String getCupomDescontoAplicado() {
        return cupomDescontoAplicado;
    }

    public void setCupomDescontoAplicado(String cupomDescontoAplicado) {
        this.cupomDescontoAplicado = cupomDescontoAplicado;
    }

    public double getValorDescontado() {
        return valorDescontado;
    }

    public void setValorDescontado(double valorDescontado) {
        this.valorDescontado = valorDescontado;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<ItemCarrinhoDTO> getItensCarrinho() {
        return itensCarrinho;
    }

    public void setItensCarrinho(List<ItemCarrinhoDTO> itensCarrinho) {
        this.itensCarrinho = itensCarrinho;
    }
}
