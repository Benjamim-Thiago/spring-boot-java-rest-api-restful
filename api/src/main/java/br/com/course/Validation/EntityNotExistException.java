package br.com.course.Validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotExistException extends BusinessException {
    private static final long serialVersionUID = 1L;


    public EntityNotExistException(String message) {
        super(message);
    }
}
