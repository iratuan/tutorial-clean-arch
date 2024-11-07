package br.com.aygean.cleanarch.infra.dto;

import java.time.LocalDate;
import java.util.List;

public record PacienteDTO(Long id,
                          String nome,
                          LocalDate dataNascimento,
                          String genero,
                          ContatoDTO contato,
                          EnderecoDTO endereco,
                          List<HistoricoMedicoDTO> historicoMedico) {
}
