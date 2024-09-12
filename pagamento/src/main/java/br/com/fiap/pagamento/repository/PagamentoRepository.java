package br.com.fiap.pagamento.repository;

import br.com.fiap.pagamento.model.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // Busca os pagamentos pelo campo usuarioId (String)
    List<Pagamento> findByUsuarioId(String usuarioId);

    // Busca paginada de pagamentos pelo campo usuarioId (String)
    Page<Pagamento> findByUsuarioId(String usuarioId, Pageable pageable);
}
