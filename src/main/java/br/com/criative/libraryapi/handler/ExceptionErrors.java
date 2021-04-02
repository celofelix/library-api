package br.com.criative.libraryapi.handler;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExceptionErrors {

    private List<String> errors;

    public ExceptionErrors(BindingResult bindingResult) {
        this.errors = new ArrayList();
        bindingResult
                .getAllErrors()
                .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ExceptionErrors(IsbnException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
