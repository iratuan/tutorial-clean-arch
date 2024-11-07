package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataNascimento;
    private String genero;

    // Relacionamento um-para-um com Contato
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contato_id", referencedColumnName = "id")
    private ContatoEntity contato;

    // Relacionamento um-para-um com Endereco
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecoEntity endereco;

    // Relacionamento um-para-muitos com HistoricoMedico
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<HistoricoMedicoEntity> historicoMedico = new ArrayList<>();

    // Construtor sem ID para facilitar a criação de instâncias sem definir ID
    public PacienteEntity(String nome, LocalDate dataNascimento, String genero, ContatoEntity contato, EnderecoEntity endereco, List<HistoricoMedicoEntity> historicoMedico) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico != null ? historicoMedico : new ArrayList<>();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PacienteEntity that = (PacienteEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
