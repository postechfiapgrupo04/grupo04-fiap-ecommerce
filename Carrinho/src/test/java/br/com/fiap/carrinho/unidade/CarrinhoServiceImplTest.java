package br.com.fiap.carrinho.unidade;

import br.com.fiap.carrinho.controller.dto.CarrinhoDto;
import br.com.fiap.carrinho.controller.dto.ItemCarrinhoDto;
import br.com.fiap.carrinho.controller.dto.UsuarioDto;
import br.com.fiap.carrinho.exception.CustomException;
import br.com.fiap.carrinho.exception.ResourceNotFoundException;
import br.com.fiap.carrinho.model.ItemCarrinho;
import br.com.fiap.carrinho.repository.ItemCarrinhoRepository;
import br.com.fiap.carrinho.service.impl.CarrinhoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarrinhoServiceImplTest {

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarrinhoServiceImpl carrinhoService;

    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioDto = new UsuarioDto();
        usuarioDto.setIdUsuario("1");
    }

    @Test
    public void deveListarItensDoUsuarioFormatadoComSucesso() {
        ItemCarrinho item1 = new ItemCarrinho();
        item1.setValorTotal(BigDecimal.valueOf(100));

        ItemCarrinho item2 = new ItemCarrinho();
        item2.setValorTotal(BigDecimal.valueOf(200));

        List<ItemCarrinho> itens = Arrays.asList(item1, item2);


        when(itemCarrinhoRepository.findByIdUsuario("1")).thenReturn(itens);

        CarrinhoDto carrinho = carrinhoService.listarItensDoUsuarioFormatado("1");

        assertEquals(BigDecimal.valueOf(300), carrinho.getValorTotalCarrinho());
        assertEquals(2, carrinho.getItens().size());
        assertEquals("1", carrinho.getIdUsuario());
    }

    @Test
    public void deveLancarExcecaoQuandoCarrinhoEstiverVazio() {

        when(itemCarrinhoRepository.findByIdUsuario("1")).thenReturn(List.of());

        assertThrows(CustomException.class, () -> carrinhoService.listarItensDoUsuarioFormatado("1"));
    }

    @Test
    public void deveAdicionarItemComSucesso() {
        ItemCarrinhoDto itemDto = new ItemCarrinhoDto();
        itemDto.setIdProduto("1");
        itemDto.setQuantidade(2);
        itemDto.setPreco(BigDecimal.valueOf(50));



        ItemCarrinho item = new ItemCarrinho();
        item.setIdProduto("1");
        item.setQuantidade(2);
        item.setPreco(BigDecimal.valueOf(50));
        item.setIdUsuario("1");
        item.atualizarValorTotal();

        when(itemCarrinhoRepository.save(any(ItemCarrinho.class))).thenReturn(item);
        when(modelMapper.map(any(ItemCarrinho.class), eq(ItemCarrinhoDto.class))).thenReturn(itemDto);

        ItemCarrinhoDto result = carrinhoService.adicionarItem(itemDto, "1");

        assertNotNull(result);
        assertEquals("1", result.getIdProduto());
        assertEquals(2, result.getQuantidade());
        assertEquals(BigDecimal.valueOf(50), result.getPreco());
    }

    @Test
    public void deveRemoverItemComSucesso() {
        ItemCarrinho item = new ItemCarrinho();
        item.setId(1L);

        when(itemCarrinhoRepository.findByIdUsuarioAndId("1", 1L)).thenReturn(Optional.of(item));

        carrinhoService.removerItem("1", 1L);

        verify(itemCarrinhoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoItemNaoForEncontrado() {
        when(itemCarrinhoRepository.findByIdUsuarioAndId("1", 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carrinhoService.removerItem("1", 2L));
    }

}
