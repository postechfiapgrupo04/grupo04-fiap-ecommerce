package br.com.fiap.pagamento.service;

import br.com.fiap.pagamento.dto.PagamentoDTO;
import br.com.fiap.pagamento.dto.UsuarioDTO;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.model.Pagamento;
import br.com.fiap.pagamento.repository.HistoricoCompraRepository;
import br.com.fiap.pagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HistoricoCompraRepository historicoCompraRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPagamentosPorUsuario_Sucesso() {
        // Arrange
        String usuarioId = "12345";
        Pageable pageable = PageRequest.of(0, 10);

        UsuarioDTO usuarioMock = new UsuarioDTO();
        usuarioMock.setUserId(usuarioId);

        Pagamento pagamentoMock = new Pagamento();
        pagamentoMock.setId(1L);

        Page<Pagamento> pagamentoPage = new PageImpl<>(List.of(pagamentoMock));

        when(restTemplate.exchange(eq("http://localhost:8002/api/user/me"),
                eq(HttpMethod.GET), isNull(), eq(UsuarioDTO.class)))
                .thenReturn(ResponseEntity.ok(usuarioMock));

        when(pagamentoRepository.findByUsuarioId(usuarioId, pageable)).thenReturn(pagamentoPage);

        when(historicoCompraRepository.findByPagamento(pagamentoMock))
                .thenReturn(Collections.emptyList());

        // Act
        Page<PagamentoDTO> resultado = pagamentoService.listarPagamentosPorUsuario(usuarioId, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(pagamentoRepository, times(1)).findByUsuarioId(usuarioId, pageable);
    }

    @Test
    void listarPagamentosPorUsuario_UsuarioNaoEncontrado() {
        // Arrange
        String usuarioId = "12345";
        Pageable pageable = PageRequest.of(0, 10);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(UsuarioDTO.class)))
                .thenThrow(new CustomException("Usuário não encontrado"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            pagamentoService.listarPagamentosPorUsuario(usuarioId, pageable);
        });

        assertEquals("Erro ao obter os dados do usuário. ID do usuário: 12345. Detalhes: Usuário não encontrado", exception.getMessage());
    }

    @Test
    void listarPagamentosPorUsuario_ErroAoBuscarPagamentos() {
        // Arrange
        String usuarioId = "12345";
        Pageable pageable = PageRequest.of(0, 10);

        UsuarioDTO usuarioMock = new UsuarioDTO();
        usuarioMock.setUserId(usuarioId);

        when(restTemplate.exchange(eq("http://localhost:8002/api/user/me"),
                eq(HttpMethod.GET), isNull(), eq(UsuarioDTO.class)))
                .thenReturn(ResponseEntity.ok(usuarioMock));

        when(pagamentoRepository.findByUsuarioId(usuarioId, pageable)).thenThrow(new RuntimeException("Erro ao buscar pagamentos"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            pagamentoService.listarPagamentosPorUsuario(usuarioId, pageable);
        });

        assertEquals("Erro ao buscar pagamentos no repositório para o usuário com ID: 12345. Detalhes: Erro ao buscar pagamentos", exception.getMessage());
    }
}
