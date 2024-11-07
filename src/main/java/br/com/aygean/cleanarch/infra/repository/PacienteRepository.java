package br.com.aygean.cleanarch.infra.repository;

import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends CrudRepository<PacienteEntity, Long> {
    @Query("select p from PacienteEntity p where upper(p.nome) like upper(?1)")
    List<PacienteEntity> findByNome(String nome);
}
