package br.com.fiap.pagamento.service;

import br.com.fiap.pagamento.dto.*;
import br.com.fiap.pagamento.exception.CustomException;
import br.com.fiap.pagamento.model.HistoricoCompra;
import br.com.fiap.pagamento.model.Pagamento;
import br.com.fiap.pagamento.repository.HistoricoCompraRepository;
import br.com.fiap.pagamento.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository pagamentoRepository;
    private final RestTemplate restTemplate;
    private final HistoricoCompraRepository historicoCompraRepository;
    private final CupomDescontoService cupomDescontoService;

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository, RestTemplate restTemplate,
                            HistoricoCompraRepository historicoCompraRepository, CupomDescontoService cupomDescontoService) {
        this.pagamentoRepository = pagamentoRepository;
        this.restTemplate = restTemplate;
        this.historicoCompraRepository = historicoCompraRepository;
        this.cupomDescontoService = cupomDescontoService;
    }

    @Transactional
    public String processarPagamento(PagamentoProcessamentoDTO pagamentoProcessamentoDTO) {
        // Obter dados do usuário via API
        UsuarioDTO usuario = obterDadosUsuario(pagamentoProcessamentoDTO.getUsuarioId());

        // Obter itens do carrinho do usuário via API de Carrinho
        List<ItemCarrinhoDTO> itensCarrinho = obterItensCarrinho(usuario.getUserId());

        if (itensCarrinho.isEmpty()) {
            throw new CustomException("O carrinho do usuário está vazio.");
        }

        // Calcular o valor total do carrinho
        double valorTotal = calcularValorTotalCarrinho(itensCarrinho);

        // Aplicar desconto se houver cupom
        String cupomDescontoAplicado = null;
        double valorDesconto = 0;
        if (pagamentoProcessamentoDTO.getCupomDesconto() != null && !pagamentoProcessamentoDTO.getCupomDesconto().isEmpty()) {
            valorDesconto = aplicarCupomDesconto(pagamentoProcessamentoDTO.getCupomDesconto(), valorTotal);
            valorTotal -= valorDesconto;
            cupomDescontoAplicado = pagamentoProcessamentoDTO.getCupomDesconto();
        }

        // Criar pagamento e salvar
        Pagamento pagamento = criarPagamento(pagamentoProcessamentoDTO, valorTotal, valorDesconto, cupomDescontoAplicado, usuario.getUserId(), itensCarrinho);
        salvarPagamento(pagamento, itensCarrinho, usuario);

        return gerarMensagemConclusao(valorTotal, valorDesconto, cupomDescontoAplicado);
    }

    private UsuarioDTO obterDadosUsuario(String id) {
        try {
            // Atualize a URL para incluir o ID dinâmico
            String url = String.format("http://ms-login:8002/api/auth/user/%s", id);
            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, UsuarioDTO.class);

            // Verifique se o corpo da resposta não é nulo e retorne os dados do usuário
            UsuarioDTO usuario = response.getBody();

            // Se o corpo for nulo, ou o serviço externo retornar um usuário inválido, lance uma exceção
            if (usuario == null || usuario.getUserId() == null || usuario.getUserId().isEmpty()) {
                throw new CustomException("Usuário inválido ou não encontrado.");
            }

            return usuario;
        } catch (HttpClientErrorException e) {
            throw new CustomException("Erro ao obter dados do usuário: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CustomException("Erro inesperado ao obter dados do usuário.", e);
        }
    }

    private List<ItemCarrinhoDTO> obterItensCarrinho(String usuarioId) {
        try {
            String url = "http://ms-carrinho:8000/carrinho/usuario/" + usuarioId;
            ResponseEntity<List<ItemCarrinhoDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemCarrinhoDTO>>() {});
            return Optional.ofNullable(response.getBody()).orElseThrow(() -> new CustomException("Carrinho não encontrado para o usuário."));
        } catch (HttpClientErrorException e) {
            throw new CustomException("Erro ao obter itens do carrinho: " + e.getMessage(), e);
        }
    }

    private double calcularValorTotalCarrinho(List<ItemCarrinhoDTO> itensCarrinho) {
        return itensCarrinho.stream().mapToDouble(ItemCarrinhoDTO::getValorTotal).sum();
    }

    private double aplicarCupomDesconto(String cupom, double valorTotal) {
        try {
            CupomDescontoApplyDTO cupomDescontoApplyDTO = new CupomDescontoApplyDTO(cupom);
            ResponseEntity<String> response = restTemplate.postForEntity("http://ms-login:8080/cupom/aplicar", new HttpEntity<>(cupomDescontoApplyDTO), String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                double percentualDesconto = cupomDescontoService.buscarPorcentagemDesconto(cupom);
                return valorTotal * (percentualDesconto / 100);
            } else {
                throw new CustomException("Falha ao aplicar o cupom: " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new CustomException("Erro ao aplicar o cupom: " + e.getResponseBodyAsString(), e);
        }
    }

    private Pagamento criarPagamento(PagamentoProcessamentoDTO dto, double valorTotal, double valorDesconto, String cupomDesconto, String usuarioId, List<ItemCarrinhoDTO> itensCarrinho) {
        Pagamento pagamento = new Pagamento();
        pagamento.setNumeroCartao(dto.getNumeroCartao());
        pagamento.setValidadeCartao(dto.getValidadeCartao());
        pagamento.setValorPago(valorTotal);
        pagamento.setValorDescontado(valorDesconto);
        pagamento.setCupomDescontoAplicado(cupomDesconto);
        pagamento.setUsuarioId(usuarioId);
        pagamento.setItensCarrinho(itensCarrinho);
        return pagamento;
    }

    private void salvarPagamento(Pagamento pagamento, List<ItemCarrinhoDTO> itensCarrinho, UsuarioDTO usuario) {
        try {
            pagamentoRepository.save(pagamento);
            salvarHistoricoCompra(itensCarrinho, pagamento, usuario);
            limparCarrinho(usuario.getUserId());
        } catch (DataAccessException e) {
            throw new CustomException("Erro ao salvar o pagamento no banco de dados.", e);
        }
    }

    private void salvarHistoricoCompra(List<ItemCarrinhoDTO> itensCarrinho, Pagamento pagamento, UsuarioDTO usuario) {
        itensCarrinho.forEach(item -> {
            HistoricoCompra historicoCompra = new HistoricoCompra();
            historicoCompra.setQuantidade(item.getQuantidade());
            historicoCompra.setPreco(item.getPreco());
            historicoCompra.setValorTotal(item.getValorTotal());
            historicoCompra.setDataCompra(LocalDateTime.now());
            historicoCompra.setUsuarioId(usuario.getUserId()); // Armazenamos apenas o ID do usuário
            historicoCompra.setPagamento(pagamento);
            historicoCompraRepository.save(historicoCompra);
        });
    }

    public Page<PagamentoDTO> listarPagamentosPorUsuario(String usuarioId, Pageable pageable) {
        logger.info("Iniciando listagem de pagamentos para o usuário com ID: {}", usuarioId);

        // Obter dados do usuário via API externa
        UsuarioDTO usuario;
        try {
            usuario = obterDadosUsuario(usuarioId);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao obter os dados do usuário. ID do usuário: %s. Detalhes: %s", usuarioId, e.getMessage());
            logger.error(errorMessage);
            throw new CustomException(errorMessage, e);
        }

        // Verifica se o usuário foi encontrado
        if (usuario == null) {
            String errorMessage = String.format("Usuário com ID %s não foi encontrado!", usuarioId);
            logger.error(errorMessage);
            throw new CustomException(errorMessage);
        }

        logger.info("Usuário encontrado: {} - Email: {}", usuario.getUserId(), usuario.getEmail());

        // Buscar os pagamentos no repositório filtrando pelo ID do usuário (usuarioId)
        Page<Pagamento> pagamentos;
        try {
            logger.info("Buscando pagamentos no repositório para o usuário com ID: {}", usuario.getUserId());
            pagamentos = pagamentoRepository.findByUsuarioId(usuario.getUserId(), pageable);
            if (pagamentos.isEmpty()) {
                logger.warn("Nenhum pagamento encontrado para o usuário com ID: {}", usuario.getUserId());
            } else {
                logger.info("Pagamentos encontrados: {}", pagamentos.getTotalElements());
            }
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao buscar pagamentos no repositório para o usuário com ID: %s. Detalhes: %s", usuarioId, e.getMessage());
            logger.error(errorMessage);
            throw new CustomException(errorMessage, e);
        }

        // Mapear os pagamentos para PagamentoDTO e incluir o histórico de compras
        return pagamentos.map(pagamento -> {
            List<HistoricoCompra> historicoCompras;
            try {
                historicoCompras = historicoCompraRepository.findByPagamento(pagamento);
                logger.info("Histórico de compras encontrado para o pagamento ID: {}. Itens: {}", pagamento.getId(), historicoCompras.size());
            } catch (Exception e) {
                String errorMessage = String.format("Erro ao buscar histórico de compras para o pagamento ID: %s. Detalhes: %s", pagamento.getId(), e.getMessage());
                logger.error(errorMessage);
                throw new CustomException(errorMessage, e);
            }

            PagamentoDTO pagamentoDTO = PagamentoDTO.fromEntity(pagamento, historicoCompras);
            logger.info("Pagamento mapeado com sucesso: {}", pagamentoDTO.getNumeroCartao());

            return pagamentoDTO;
        });
    }

    private void limparCarrinho(String usuarioId) {
        restTemplate.delete("http://ms-carrinho:8000/carrinho/usuario/" + usuarioId);
    }

    private String gerarMensagemConclusao(double valorTotal, double valorDesconto, String cupomDescontoAplicado) {
        return String.format("Compra concluída com sucesso! Valor total após desconto: R$ %.2f. Cupom: %s. Valor descontado: R$ %.2f",
                valorTotal, cupomDescontoAplicado, valorDesconto);
    }
}
