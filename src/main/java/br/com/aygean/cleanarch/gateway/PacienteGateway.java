package br.com.aygean.cleanarch.gateway;

import br.com.aygean.cleanarch.domain.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteGateway {
    List<Paciente> listarPacientes();
    Paciente adicionarPaciente(Paciente paciente);
    Optional<Paciente> buscarPacientePorId(Long id);
    Optional<Paciente> atualizarPaciente(Paciente paciente);
    Optional<List<Paciente>> buscarPacientes(String nome);
    void deletarPaciente(Paciente paciente);
}
