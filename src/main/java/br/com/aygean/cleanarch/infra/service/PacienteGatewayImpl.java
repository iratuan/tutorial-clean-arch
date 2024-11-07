package br.com.aygean.cleanarch.infra.service;

import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.gateway.PacienteGateway;
import br.com.aygean.cleanarch.infra.converter.PacienteConverter;
import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import br.com.aygean.cleanarch.infra.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteGatewayImpl implements PacienteGateway {

    private final PacienteRepository pacienteRepository;

    public PacienteGatewayImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public List<Paciente> listarPacientes() {
        var entityList = pacienteRepository.findAll();
        var pacienteList = new ArrayList<Paciente>();
        entityList.forEach(e -> {
            pacienteList.add(PacienteConverter.fromEntityToDomain(e));
        });
        return pacienteList;
    }

    @Override
    public Paciente adicionarPaciente(Paciente paciente) {
        var pacienteEntity = PacienteConverter.fromDomainToEntity(paciente);
        var pacienteEntityPersisted = pacienteRepository.save(pacienteEntity);
        return PacienteConverter.fromEntityToDomain(pacienteEntityPersisted);
    }

    @Override
    public Optional<Paciente> buscarPacientePorId(Long id) {
        Optional<PacienteEntity> optionalPacienteEntity = pacienteRepository.findById(id);
        return optionalPacienteEntity.map(PacienteConverter::fromEntityToDomain);
    }

    @Override
    public Optional<Paciente> atualizarPaciente(Paciente paciente) {
        var pacienteEntity = pacienteRepository.save(PacienteConverter.fromDomainToEntity(paciente));
        return Optional.of(PacienteConverter.fromEntityToDomain(pacienteEntity));
    }

    @Override
    public Optional<List<Paciente>> buscarPacientes(String nome) {
        List<PacienteEntity> pacienteEntityList = pacienteRepository.findByNome(nome);

        // Retorna Optional.empty() se a lista estiver vazia
        if (pacienteEntityList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pacienteEntityList.stream()
            .map(PacienteConverter::fromEntityToDomain)
            .toList());
    }

    @Override
    public void deletarPaciente(Paciente paciente) {
        // Verifica se o paciente existe antes de deletar
        if (paciente.getId() != null && pacienteRepository.existsById(paciente.getId())) {
            pacienteRepository.deleteById(paciente.getId());
        } else {
            throw new IllegalArgumentException("Paciente não encontrado ou ID inválido.");
        }
    }
}
