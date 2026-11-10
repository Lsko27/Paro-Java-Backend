package br.group.paro.recurso.service;

import br.group.paro.recurso.dto.RecursoResponse;
import br.group.paro.recurso.entity.Recurso;
import br.group.paro.recurso.repository.RecursoRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoService(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Transactional(readOnly = true)
    public List<RecursoResponse> listarTodos() {
        return recursoRepository.findAll(
                        Sort.by(Sort.Direction.ASC, "nome")
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RecursoResponse toResponse(Recurso recurso) {
        return new RecursoResponse(
                recurso.getId(),
                recurso.getNome(),
                recurso.getDescricao()
        );
    }
}