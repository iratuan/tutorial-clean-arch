package br.com.aygean.cleanarch.infra.dto;

import java.time.LocalDate;

public record HistoricoMedicoDTO(LocalDate dataConsulta,
                                 String diagnostico,
                                 String tratamento,
                                 String observacoes) {
}
