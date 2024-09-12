package br.com.fiap.pagamento.controller;
import br.com.fiap.pagamento.dto.CupomDescontoApplyDTO;
import br.com.fiap.pagamento.dto.CupomDescontoCreateDTO;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.service.CupomDescontoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(CupomDescontoController.class) // Testando apenas o controller
public class CupomDescontoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CupomDescontoService cupomDescontoService; // Mock do service

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void aplicarCupom_Sucesso() throws Exception {
        // Arrange
        String codigoCupom = "DESCONTO20";
        when(cupomDescontoService.aplicarCupom(codigoCupom)).thenReturn("Cupom aplicado com sucesso! Desconto de 20%.");

        CupomDescontoApplyDTO applyDTO = new CupomDescontoApplyDTO(codigoCupom);

        // Act & Assert
        mockMvc.perform(post("/cupom/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cupom aplicado com sucesso! Desconto de 20%."));

        verify(cupomDescontoService, times(1)).aplicarCupom(codigoCupom);
    }

    @Test
    void criarCupom_Erro_CustomException() throws Exception {
        // Arrange
        CupomDescontoCreateDTO cupomCreateDTO = new CupomDescontoCreateDTO();
        cupomCreateDTO.setCodigo("DESCONTOINVALIDO");
        cupomCreateDTO.setPorcentagemDesconto(150.0); // Percentual inválido

        // Act & Assert
        mockMvc.perform(post("/cupom/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cupomCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.porcentagemDesconto").value("A porcentagem de desconto deve ser menor ou igual a 100."));
    }
}
