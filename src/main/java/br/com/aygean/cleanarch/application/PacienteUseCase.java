package br.com.aygean.cleanarch.application;

import br.com.aygean.cleanarch.application.exception.PacienteNotFoundException;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.gateway.PacienteGateway;

import java.util.List;
import java.util.Optional;

public class PacienteUseCase {
    // Dependência para acessar o repositório de pacientes
    private final PacienteGateway gateway;

    // Construtor que recebe uma implementação de PacienteGateway
    public PacienteUseCase(PacienteGateway gateway) {
        this.gateway = gateway;
    }

    // Lista todos os pacientes
    public List<Paciente> listarPacientes() {
        return gateway.listarPacientes();
    }

    // Adiciona um novo paciente e o retorna após o salvamento
    public Paciente adicionarPaciente(Paciente paciente) {
        return gateway.adicionarPaciente(paciente);
    }

    // Busca um paciente por ID e lança uma exceção se não for encontrado
    public Paciente buscarPacientePorId(Long id) {
        return gateway.buscarPacientePorId(id)
            .orElseThrow(() -> new PacienteNotFoundException("Paciente não encontrado"));
    }

    // Atualiza os dados de um paciente existente e lança exceção se o ID não existir
    public Paciente atualizarPaciente(Long id, Paciente paciente) {
        // Verifica se o paciente existe, lançando exceção se não for encontrado
        buscarPacientePorId(id);

        // Define o ID do paciente a ser atualizado e salva
        paciente.setId(id);

        // Redefine o paciente para sua versão atualizada
        paciente = gateway.atualizarPaciente(paciente).get();
        return paciente;
    }

    // Busca pacientes pelo nome e lança exceção se não houver resultados
    public List<Paciente> buscarPacientes(String nome) {
        Optional<List<Paciente>> pacientes = gateway.buscarPacientes(nome);

        // Lança exceção se a lista estiver vazia
        if (pacientes.isEmpty()) {
            throw new PacienteNotFoundException("Não encontramos resultados que satisfaçam os critérios de sua busca");
        }
        // Retorna a lista de pacientes
        return pacientes.get();
    }

    // Deleta um paciente pelo ID, após verificar sua existência
    public void deletarPaciente(Long id) {
        // Verifica se o paciente existe, lançando exceção se não for encontrado
        Paciente paciente = buscarPacientePorId(id);

        // Realiza a exclusão do paciente
        gateway.deletarPaciente(paciente);
    }
}
