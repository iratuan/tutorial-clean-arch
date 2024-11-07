package br.com.aygean.cleanarch.infra.converter;

import br.com.aygean.cleanarch.domain.Contato;
import br.com.aygean.cleanarch.domain.Endereco;
import br.com.aygean.cleanarch.domain.HistoricoMedico;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.infra.dto.ContatoDTO;
import br.com.aygean.cleanarch.infra.dto.EnderecoDTO;
import br.com.aygean.cleanarch.infra.dto.HistoricoMedicoDTO;
import br.com.aygean.cleanarch.infra.dto.PacienteDTO;
import br.com.aygean.cleanarch.infra.entity.ContatoEntity;
import br.com.aygean.cleanarch.infra.entity.EnderecoEntity;
import br.com.aygean.cleanarch.infra.entity.HistoricoMedicoEntity;
import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class PacienteConverter {

    public static Paciente fromDtoToDomain(PacienteDTO dto) {

        var contato = new Contato();
        BeanUtils.copyProperties(dto.contato(), contato);
        var endereco = new Endereco();
        BeanUtils.copyProperties(dto.endereco(), endereco);
        var historicoMedico = dto.historicoMedico().stream().map((h) -> {
            var historicoMedicoDomain = new HistoricoMedico();
            BeanUtils.copyProperties(h, historicoMedicoDomain);
            return historicoMedicoDomain;
        }).toList();
        return new Paciente(dto.id(), dto.nome(), dto.dataNascimento(), dto.genero(), contato, endereco, historicoMedico);
    }

    public static PacienteEntity fromDtoToEntity(PacienteDTO dto) {

        var contatoEntity = new ContatoEntity();
        BeanUtils.copyProperties(dto.contato(), contatoEntity);
        var enderecoEntity = new EnderecoEntity();
        BeanUtils.copyProperties(dto.endereco(), enderecoEntity);
        var historicoMedicoEntity = dto.historicoMedico().stream().map((h) -> {
            var historicoMedicoInnerEntity = new HistoricoMedicoEntity();
            BeanUtils.copyProperties(h, historicoMedicoInnerEntity);
            return historicoMedicoInnerEntity;
        }).toList();
        return new PacienteEntity(
            dto.nome(),
            dto.dataNascimento(),
            dto.genero(),
            contatoEntity,
            enderecoEntity,
            historicoMedicoEntity);
    }

    public static PacienteDTO fromDomainToDTO(Paciente domain) {
        var contatoDTO = new ContatoDTO(domain.getContato().getTelefone(),
            domain.getContato().getEmail());
        var enderecoDTO = new EnderecoDTO(domain.getEndereco().getRua(),
            domain.getEndereco().getNumero(),
            domain.getEndereco().getCidade(),
            domain.getEndereco().getEstado(),
            domain.getEndereco().getCep());
        var historicoMedicoDTO = domain.getHistoricoMedico().stream().map((h) -> new HistoricoMedicoDTO(
                h.getDataConsulta(),
                h.getDiagnostico(),
                h.getTratamento(),
                h.getObservacoes()))
            .toList();

        return new br.com.aygean.cleanarch.infra.dto.PacienteDTO(
            domain.getId(),
            domain.getNome(),
            domain.getDataNascimento(),
            domain.getGenero(),
            contatoDTO,
            enderecoDTO,
            historicoMedicoDTO);
    }


    public static Paciente fromEntityToDomain(PacienteEntity entity) {
        var contato = new Contato();
        BeanUtils.copyProperties(entity.getContato(), contato);
        var endereco = new Endereco();
        BeanUtils.copyProperties(entity.getEndereco(), endereco);

        var historicoMedico = entity.getHistoricoMedico().stream().map((h) -> new HistoricoMedico(
                h.getDataConsulta(),
                h.getDiagnostico(),
                h.getTratamento(),
                h.getObservacoes()))
            .toList();

        return new Paciente(
            entity.getId(),
            entity.getNome(),
            entity.getDataNascimento(),
            entity.getGenero(),
            contato,
            endereco,
            historicoMedico);
    }

    public static PacienteEntity fromDomainToEntity(Paciente domain) {
        var contatoEntity = new ContatoEntity();
        BeanUtils.copyProperties(domain.getContato(), contatoEntity);
        var enderecoEntity = new EnderecoEntity();
        BeanUtils.copyProperties(domain.getEndereco(), enderecoEntity);
        List<HistoricoMedicoEntity> historicoMedicoEntity = new ArrayList<>();
        List<HistoricoMedico> listaHistoricoMedico = domain.getHistoricoMedico();
        for (HistoricoMedico h : listaHistoricoMedico) {
            var historicoMedico = new HistoricoMedicoEntity(h.getDataConsulta(), h.getDiagnostico(), h.getTratamento(), h.getObservacoes());
            historicoMedicoEntity.add(historicoMedico);
        }
        return new PacienteEntity(
            domain.getId(),
            domain.getNome(),
            domain.getDataNascimento(),
            domain.getGenero(),
            contatoEntity,
            enderecoEntity,
            historicoMedicoEntity);
    }
}
