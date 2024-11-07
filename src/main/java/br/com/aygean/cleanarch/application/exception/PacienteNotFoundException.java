package br.com.aygean.cleanarch.application.exception;

public class PacienteNotFoundException extends RuntimeException {

    public PacienteNotFoundException(Long id) {
        super("Paciente com ID " + id + " não foi encontrado.");
    }

    public PacienteNotFoundException(String message) {
        super(message);
    }
}
