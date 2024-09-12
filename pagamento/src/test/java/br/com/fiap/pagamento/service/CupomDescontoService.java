package br.com.fiap.pagamento.service;

import br.com.fiap.pagamento.dto.CupomDescontoCreateDTO;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.model.CupomDesconto;
import br.com.fiap.pagamento.repository.CupomDescontoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CupomDescontoServiceTest {

    @Mock
    private CupomDescontoRepository cupomDescontoRepository;

    @InjectMocks
    private CupomDescontoService cupomDescontoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void aplicarCupom_Sucesso() {
        // Arrange
        String codigo = "DESCONTO10";
        CupomDesconto cupom = new CupomDesconto();
        cupom.setCodigo(codigo);
        cupom.setPorcentagemDesconto(10);
        cupom.setNumeroUsos(0);
        cupom.setAtivo(true);

        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));

        // Act
        String resultado = cupomDescontoService.aplicarCupom(codigo);

        // Assert
        assertEquals("Cupom aplicado com sucesso! Desconto de 10%.", resultado);
        verify(cupomDescontoRepository, times(1)).findByCodigo(codigo);
        verify(cupomDescontoRepository, times(1)).save(cupom);
    }

    @Test
    void aplicarCupom_CupomNaoEncontrado() {
        // Arrange
        String codigo = "INVALIDO";
        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.aplicarCupom(codigo);
        });

        assertEquals("Cupom de desconto inválido ou não encontrado.", exception.getMessage());
    }

    @Test
    void aplicarCupom_CupomInativo() {
        // Arrange
        String codigo = "DESCONTO10";
        CupomDesconto cupom = new CupomDesconto();
        cupom.setCodigo(codigo);
        cupom.setAtivo(false);

        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.aplicarCupom(codigo);
        });

        assertEquals("Cupom expirado: Este cupom não é mais válido.", exception.getMessage());
    }

    @Test
    void aplicarCupom_CupomAtingiuLimiteDeUsos() {
        // Arrange
        String codigo = "DESCONTO10";
        CupomDesconto cupom = new CupomDesconto();
        cupom.setCodigo(codigo);
        cupom.setAtivo(true);
        cupom.setNumeroUsos(10);  // Atingiu o limite de usos

        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.aplicarCupom(codigo);
        });

        assertEquals("Este cupom de desconto já atingiu o limite máximo de utilizações e não pode mais ser usado.", exception.getMessage());
    }

    @Test
    void listarTodosCupons_Sucesso() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<CupomDesconto> page = new PageImpl<>(List.of(new CupomDesconto()));
        when(cupomDescontoRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<CupomDesconto> resultado = cupomDescontoService.listarTodosCupons(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(cupomDescontoRepository, times(1)).findAll(pageable);
    }

    @Test
    void listarTodosCupons_ErroNoBancoDeDados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(cupomDescontoRepository.findAll(pageable)).thenThrow(new DataAccessException("Erro ao acessar banco de dados") {});

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.listarTodosCupons(pageable);
        });

        assertEquals("Erro ao acessar o banco de dados ao listar os cupons de desconto.", exception.getMessage());
    }

    @Test
    void criarCupom_Sucesso() {
        // Arrange
        CupomDescontoCreateDTO dto = new CupomDescontoCreateDTO();
        dto.setCodigo("DESCONTO20");
        dto.setPorcentagemDesconto(20.0);

        CupomDesconto cupom = new CupomDesconto();
        cupom.setCodigo(dto.getCodigo());
        cupom.setPorcentagemDesconto(dto.getPorcentagemDesconto());

        // Act
        cupomDescontoService.criarCupom(dto);

        // Assert
        verify(cupomDescontoRepository, times(1)).save(any(CupomDesconto.class));
    }

    @Test
    void criarCupom_CodigoVazio() {
        // Arrange
        CupomDescontoCreateDTO dto = new CupomDescontoCreateDTO();
        dto.setCodigo("");
        dto.setPorcentagemDesconto(20.0);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.criarCupom(dto);
        });

        assertEquals("O código do cupom não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    void buscarPorcentagemDesconto_Sucesso() {
        // Arrange
        String codigo = "DESCONTO15";
        CupomDesconto cupom = new CupomDesconto();
        cupom.setCodigo(codigo);
        cupom.setPorcentagemDesconto(15);

        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));

        // Act
        double porcentagem = cupomDescontoService.buscarPorcentagemDesconto(codigo);

        // Assert
        assertEquals(15, porcentagem);
        verify(cupomDescontoRepository, times(1)).findByCodigo(codigo);
    }

    @Test
    void buscarPorcentagemDesconto_CupomNaoEncontrado() {
        // Arrange
        String codigo = "INVALIDO";
        when(cupomDescontoRepository.findByCodigo(codigo)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            cupomDescontoService.buscarPorcentagemDesconto(codigo);
        });

        assertEquals("Cupom de desconto não encontrado.", exception.getMessage());
    }
}
