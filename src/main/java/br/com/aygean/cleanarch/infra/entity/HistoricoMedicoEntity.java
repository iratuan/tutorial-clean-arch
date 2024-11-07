package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "historicos_medicos_paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataConsulta;
    private String diagnostico;
    private String tratamento;
    private String observacoes;

    public HistoricoMedicoEntity(LocalDate dataConsulta, String diagnostico, String tratamento, String observacoes) {
        this.dataConsulta = dataConsulta;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }
}
