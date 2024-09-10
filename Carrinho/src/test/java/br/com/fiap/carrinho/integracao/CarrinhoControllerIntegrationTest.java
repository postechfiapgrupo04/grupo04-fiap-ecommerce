package br.com.fiap.carrinho.integracao;

import br.com.fiap.carrinho.controller.dto.ItemCarrinhoDto;
import br.com.fiap.carrinho.model.ItemCarrinho;
import br.com.fiap.carrinho.repository.ItemCarrinhoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarrinhoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @BeforeEach
    public void setup() {
        // Insere um item de carrinho no banco de dados H2 para o usuário "1"
        ItemCarrinho item = new ItemCarrinho();
        item.setIdProduto("prod123");
        item.setQuantidade(2);
        item.setPreco(new BigDecimal("10.00"));
        item.setIdUsuario("1");
        item.atualizarValorTotal();

        // Salvar no banco de dados H2
        itemCarrinhoRepository.save(item);
    }
    @Test
    public void deveAdicionarItemAoCarrinhoComSucesso() throws Exception {
        ItemCarrinhoDto item = new ItemCarrinhoDto();
        item.setIdProduto("1");
        item.setQuantidade(2);
        item.setPreco(BigDecimal.valueOf(100));

        mockMvc.perform(post("/carrinho/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item adicionado ao carrinho com sucesso."));
    }
    @Test
    public void deveListarItensDoCarrinhoComSucesso() throws Exception {
        mockMvc.perform(get("/carrinho/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value("1"))
                .andExpect(jsonPath("$.totalItens").isNumber())
                .andExpect(jsonPath("$.valorTotalCarrinho").isNumber());
    }
    @Test
    public void deveRetornar400QuandoItemInvalidoForEnviado() throws Exception {
        ItemCarrinhoDto item = new ItemCarrinhoDto(); // Sem dados obrigatórios

        mockMvc.perform(post("/carrinho/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }
}
