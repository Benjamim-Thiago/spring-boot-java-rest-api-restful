package br.com.course.Validation;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
