package br.com.fiap.carrinho.controller;

import br.com.fiap.carrinho.controller.dto.CarrinhoDto;
import br.com.fiap.carrinho.controller.dto.ItemCarrinhoDto;
import br.com.fiap.carrinho.exception.CustomException;
import br.com.fiap.carrinho.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrinho")
@Tag(name = "Carrinho", description = "APIs relacionadas à gestão de itens no carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @Autowired
    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @Operation(summary = "Listar itens do carrinho", description = "Recupera todos os itens no carrinho de um usuário específico e exibe o valor total do carrinho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens listados com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os itens do carrinho",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarrinhoDto> listarItensDoUsuario(@PathVariable String usuarioId) {
        CarrinhoDto resposta = carrinhoService.listarItensDoUsuarioFormatado(usuarioId);
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Adicionar item ao carrinho", description = "Adiciona um novo item ao carrinho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o item",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao adicionar o item ao carrinho",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<String> adicionarItem(@PathVariable String usuarioId,
                                                @Valid @RequestBody ItemCarrinhoDto itemDto) {
        carrinhoService.adicionarItem(itemDto, usuarioId);
        return ResponseEntity.ok("Item adicionado ao carrinho com sucesso.");
    }

    @Operation(summary = "Remover item do carrinho", description = "Remove um item do carrinho com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao remover o item do carrinho",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/usuario/{usuarioId}/item/{id}")
    public ResponseEntity<String> removerItem(@PathVariable String usuarioId,
                                              @PathVariable Long id) {
        try {
            carrinhoService.removerItem(usuarioId, id);
            return ResponseEntity.ok("Item removido com sucesso.");
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            throw new CustomException("Erro ao remover item do carrinho.", e);
        }
    }
}
