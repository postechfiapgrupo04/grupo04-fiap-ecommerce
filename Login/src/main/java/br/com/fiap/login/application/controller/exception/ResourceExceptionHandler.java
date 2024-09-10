package br.com.fiap.login.application.controller.exception;

import br.com.fiap.login.application.dto.ErrorDTO;
import br.com.fiap.login.application.exceptions.AppException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDTO> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        ErrorDTO err = new ErrorDTO();
        err.setTimestamp(Instant.now());
        err.setStatus(HttpStatus.NOT_FOUND.value());
        err.setError("Resource not found");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(err.getStatus()).body(err);
    }
}
