package br.group.paro.recurso.repository;

import br.group.paro.recurso.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {
}