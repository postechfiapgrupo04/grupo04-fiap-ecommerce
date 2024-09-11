package br.com.fiap.pagamento.controller;

import br.com.fiap.pagamento.dto.PagamentoDTO;
import br.com.fiap.pagamento.dto.PagamentoProcessamentoDTO;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @Test
    public void processarPagamento_Sucesso() throws Exception {
        // Arrange
        PagamentoProcessamentoDTO pagamentoDTO = new PagamentoProcessamentoDTO();
        pagamentoDTO.setNumeroCartao("1234567812345678");
        pagamentoDTO.setValidadeCartao("12/25");
        pagamentoDTO.setCupomDesconto("DESC10");

        when(pagamentoService.processarPagamento(any(PagamentoProcessamentoDTO.class)))
                .thenReturn("Pagamento processado com sucesso!");

        // Act & Assert
        mockMvc.perform(post("/pagamento/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(pagamentoDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Pagamento processado com sucesso!"));
    }

    @Test
    public void processarPagamento_ErroCustomException() throws Exception {
        // Arrange
        PagamentoProcessamentoDTO pagamentoDTO = new PagamentoProcessamentoDTO();
        pagamentoDTO.setNumeroCartao("1234567812345678");
        pagamentoDTO.setValidadeCartao("12/25");

        when(pagamentoService.processarPagamento(any(PagamentoProcessamentoDTO.class)))
                .thenThrow(new CustomException("Erro ao processar o pagamento"));

        // Act & Assert
        mockMvc.perform(post("/pagamento/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(pagamentoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao processar o pagamento"));
    }

    @Test
    public void processarPagamento_ErroValidacao() throws Exception {
        // Arrange
        PagamentoProcessamentoDTO pagamentoDTO = new PagamentoProcessamentoDTO(); // Campos em branco

        // Act & Assert
        mockMvc.perform(post("/pagamento/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(pagamentoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numeroCartao").value("O número do cartão é obrigatório."))
                .andExpect(jsonPath("$.validadeCartao").value("A validade do cartão é obrigatória."));
    }

    @Test
    public void listarPagamentosPorUsuario_Sucesso() throws Exception {
        // Arrange
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setNumeroCartao("1234567812345678");
        pagamentoDTO.setValidadeCartao("12/25");
        pagamentoDTO.setValorPago(100.0);

        Page<PagamentoDTO> pagamentos = new PageImpl<>(Collections.singletonList(pagamentoDTO));

        when(pagamentoService.listarPagamentosPorUsuario(anyString(), any(Pageable.class)))
                .thenReturn(pagamentos);

        // Act & Assert
        mockMvc.perform(get("/pagamento/usuario/{usuarioId}/pagamentos", "user123")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numeroCartao").value("1234567812345678"))
                .andExpect(jsonPath("$.content[0].validadeCartao").value("12/25"))
                .andExpect(jsonPath("$.content[0].valorPago").value(100.0));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

