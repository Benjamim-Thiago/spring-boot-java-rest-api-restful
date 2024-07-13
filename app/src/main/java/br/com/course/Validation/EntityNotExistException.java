package br.com.course.Validation;

public class EntityNotExistException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EntityNotExistException(String message) {
        super(message);
    }
}
