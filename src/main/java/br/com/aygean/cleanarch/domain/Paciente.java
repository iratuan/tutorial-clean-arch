package br.com.aygean.cleanarch.domain;

import java.time.LocalDate;
import java.util.List;

public class Paciente {

    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private Contato contato;
    private Endereco endereco;
    private List<HistoricoMedico> historicoMedico;

    public Paciente(String nome, LocalDate dataNascimento, String genero, Contato contato, Endereco endereco, List<HistoricoMedico> historicoMedico) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico;
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
