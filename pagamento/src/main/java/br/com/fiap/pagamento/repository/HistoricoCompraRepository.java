package br.com.fiap.pagamento.repository;


import br.com.fiap.pagamento.model.HistoricoCompra;
import br.com.fiap.pagamento.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoCompraRepository extends JpaRepository<HistoricoCompra, Long> {
    List<HistoricoCompra> findByPagamento(Pagamento pagamento);
}
