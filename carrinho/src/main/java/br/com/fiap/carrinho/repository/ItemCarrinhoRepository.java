package br.com.fiap.carrinho.repository;

import br.com.fiap.carrinho.model.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    public List<ItemCarrinho> findByIdUsuario(String idUsuario);
    Optional<ItemCarrinho> findByIdUsuarioAndId(String idUsuario, Long id);
}
