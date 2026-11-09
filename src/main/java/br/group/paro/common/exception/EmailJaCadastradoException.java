package br.group.paro.common.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("Este e-mail já está cadastrado");
    }
}