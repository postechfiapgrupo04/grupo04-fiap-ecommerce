package br.com.fiap.pagamento.controller;

import br.com.fiap.pagamento.dto.PagamentoDTO;
import br.com.fiap.pagamento.dto.PagamentoProcessamentoDTO;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamento", description = "APIs relacionadas ao processamento de pagamentos")
@Slf4j // Lombok cuida da inicialização do logger
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Autowired
    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @Operation(summary = "Processar um pagamento", description = "Processa o pagamento dos itens no carrinho de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento processado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar o pagamento", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/processar")
    public ResponseEntity<String> processarPagamento(@Validated @RequestBody PagamentoProcessamentoDTO pagamentoProcessamentoDTO) {
        try {
            String resultado = pagamentoService.processarPagamento(pagamentoProcessamentoDTO);
            return ResponseEntity.ok(resultado);
        } catch (CustomException e) {
            // Usa o logger fornecido pelo Lombok
            log.error("Erro de solicitação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao processar pagamento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado ao processar o pagamento: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar todos os pagamentos de um usuário", description = "Recupera todos os pagamentos já efetuados por um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamentos listados com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os pagamentos", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/usuario/{usuarioId}/pagamentos")
    public ResponseEntity<Object> listarPagamentosPorUsuario(
            @PathVariable String usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<PagamentoDTO> pagamentos = pagamentoService.listarPagamentosPorUsuario(usuarioId, pageable);
            return ResponseEntity.ok(pagamentos);
        } catch (CustomException e) {
            // Log da exceção com Lombok's @Slf4j
            log.error("Erro de solicitação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar os pagamentos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }
}
