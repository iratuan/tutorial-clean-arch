package br.com.aygean.cleanarch.infra.dto;

public record EnderecoDTO(String rua,
                          Integer numero,
                          String cidade,
                          String estado,
                          String cep) {
}
