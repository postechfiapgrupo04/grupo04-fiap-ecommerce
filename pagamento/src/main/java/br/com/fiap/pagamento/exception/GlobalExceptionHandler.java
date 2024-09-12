package br.com.fiap.pagamento.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Exceção de validação
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        logger.warn("Erro de validação: {}", ex.getMessage());

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceção customizada
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomExceptions(CustomException ex) {
        Map<String, String> error = new HashMap<>();
        logger.error("Erro customizado: {}", ex.getMessage());

        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Captura erros de HTTP, como erros de comunicação ou acesso de recursos (exemplo: Connection Refused)
    @ExceptionHandler({HttpClientErrorException.class, ResourceAccessException.class})
    public ResponseEntity<Map<String, String>> handleHttpClientError(Exception ex) {
        Map<String, String> error = new HashMap<>();

        // Logando a exceção detalhada
        logger.error("Erro de comunicação HTTP: {}", ex.getMessage());

        // Inclui a mensagem detalhada da exceção na resposta
        error.put("error", "Erro de comunicação: " + ex.getMessage());

        // Aqui, você pode ajustar o status conforme necessário. Para 'Connection Refused', podemos usar 503 (Service Unavailable)
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Exceção global
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        logger.error("Erro inesperado: {}", ex.getMessage(), ex);

        error.put("error", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
