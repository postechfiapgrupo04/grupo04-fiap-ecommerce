package br.com.fiap.item.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Class<?> entity, Object identifier) {
        super(entity.getSimpleName() + " not found: " + identifier);
    }

}