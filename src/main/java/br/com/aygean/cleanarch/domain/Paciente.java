package br.com.aygean.cleanarch.domain;

import java.time.LocalDate;
import java.util.List;

public class Paciente {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private Contato contato;
    private Endereco endereco;
    private List<HistoricoMedico> historicoMedico;

    public Paciente() {
        // no args constructor
    }

    public Paciente(Long id, String nome, LocalDate dataNascimento, String genero, Contato contato, Endereco endereco, List<HistoricoMedico> historicoMedico) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<HistoricoMedico> getHistoricoMedico() {
        return historicoMedico;
    }

    public void setHistoricoMedico(List<HistoricoMedico> historicoMedico) {
        this.historicoMedico = historicoMedico;
    }
}
