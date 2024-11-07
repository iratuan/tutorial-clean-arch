package br.com.aygean.cleanarch.infra.controller;

import br.com.aygean.cleanarch.application.PacienteUseCase;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.infra.dto.PacienteDTO;
import br.com.aygean.cleanarch.infra.converter.PacienteConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteUseCase pacienteUseCase;

    public PacienteController(PacienteUseCase pacienteUseCase) {
        this.pacienteUseCase = pacienteUseCase;
    }

    // Método para listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarPacientes() {
        List<PacienteDTO> pacientes = pacienteUseCase.listarPacientes()
            .stream()
            .map(PacienteConverter::fromDomainToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pacientes);
    }

    // Método para buscar um paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> buscarPacientePorId(@PathVariable Long id) {
        Paciente paciente = pacienteUseCase.buscarPacientePorId(id);
        var pacienteDto = PacienteConverter.fromDomainToDTO(paciente);
        return ResponseEntity.ok(pacienteDto);
    }

    // Método para adicionar um novo paciente
    @PostMapping
    public ResponseEntity<PacienteDTO> adicionarPaciente(@RequestBody PacienteDTO pacienteDTO) {
        var paciente = PacienteConverter.fromDtoToDomain(pacienteDTO);
        var pacienteSalvo = pacienteUseCase.adicionarPaciente(paciente);
        return ResponseEntity.ok(PacienteConverter.fromDomainToDTO(pacienteSalvo));
    }

    // Método para atualizar um paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> atualizarPaciente(@PathVariable Long id, @RequestBody PacienteDTO pacienteDTO) {
        var paciente = PacienteConverter.fromDtoToDomain(pacienteDTO);
        Paciente pacienteAtualizado = pacienteUseCase.atualizarPaciente(id, paciente);
        var pacienteDto = PacienteConverter.fromDomainToDTO(pacienteAtualizado);
        return ResponseEntity.ok(pacienteDto);
    }

    // Método para deletar um paciente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        try {
            pacienteUseCase.deletarPaciente(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
