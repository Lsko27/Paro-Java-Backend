
package br.group.paro.estabelecimento.service;

import br.group.paro.common.exception.RecursoNaoEncontradoException;
import br.group.paro.estabelecimento.dto.HorarioFuncionamentoRequest;
import br.group.paro.estabelecimento.dto.HorarioFuncionamentoResponse;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.entity.HorarioFuncionamento;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;
import br.group.paro.estabelecimento.repository.HorarioFuncionamentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HorarioFuncionamentoService {

    private final HorarioFuncionamentoRepository horarioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public HorarioFuncionamentoService(
            HorarioFuncionamentoRepository horarioRepository,
            EstabelecimentoRepository estabelecimentoRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioFuncionamentoResponse> listar(
            Long estabelecimentoId
    ) {
        buscarEstabelecimentoAtivo(estabelecimentoId);

        return horarioRepository
                .findByEstabelecimentoIdOrderByDiaSemanaAsc(estabelecimentoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<HorarioFuncionamentoResponse> atualizar(
            Long estabelecimentoId,
            List<HorarioFuncionamentoRequest> requests,
            Long usuarioId
    ) {
        Estabelecimento estabelecimento =
                buscarEstabelecimentoDoProprietario(
                        estabelecimentoId,
                        usuarioId
                );

        validarHorarios(requests);

        List<HorarioFuncionamento> horariosAnteriores =
                horarioRepository
                        .findByEstabelecimentoIdOrderByDiaSemanaAsc(
                                estabelecimentoId
                        );

        horarioRepository.deleteAll(horariosAnteriores);
        horarioRepository.flush();

        List<HorarioFuncionamento> novosHorarios = requests.stream()
                .map(request -> new HorarioFuncionamento(
                        estabelecimento,
                        request.diaSemana(),
                        request.fechado()
                                ? null
                                : LocalTime.parse(request.horaAbertura()),
                        request.fechado()
                                ? null
                                : LocalTime.parse(request.horaFechamento()),
                        request.fechado()
                ))
                .toList();

        return horarioRepository.saveAll(novosHorarios)
                .stream()
                .sorted(Comparator.comparing(
                        HorarioFuncionamento::getDiaSemana
                ))
                .map(this::toResponse)
                .toList();
    }

    private Estabelecimento buscarEstabelecimentoAtivo(
            Long estabelecimentoId
    ) {
        return estabelecimentoRepository.findById(estabelecimentoId)
                .filter(Estabelecimento::getAtivo)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estabelecimento não encontrado"
                        )
                );
    }

    private Estabelecimento buscarEstabelecimentoDoProprietario(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        return estabelecimentoRepository
                .findByIdAndProprietarioId(
                        estabelecimentoId,
                        usuarioId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estabelecimento não encontrado"
                        )
                );
    }

    private void validarHorarios(
            List<HorarioFuncionamentoRequest> requests
    ) {
        if (requests == null || requests.size() != 7) {
            throw badRequest(
                    "É necessário informar os 7 dias da semana"
            );
        }

        Set<Integer> diasInformados = new HashSet<>();

        for (HorarioFuncionamentoRequest request : requests) {

            if (request == null
                    || request.diaSemana() == null
                    || request.fechado() == null) {
                throw badRequest(
                        "Todos os dias devem possuir informações válidas"
                );
            }

            Integer diaSemana = request.diaSemana();

            if (diaSemana < 1 || diaSemana > 7) {
                throw badRequest(
                        "O dia da semana deve estar entre 1 e 7"
                );
            }

            if (!diasInformados.add(diaSemana)) {
                throw badRequest(
                        "Existem dias da semana duplicados"
                );
            }

            validarHorarioDoDia(request);
        }
    }


    private void validarHorarioDoDia(
            HorarioFuncionamentoRequest request
    ) {
        if (request.fechado()) {
            if (request.horaAbertura() != null
                    || request.horaFechamento() != null) {
                throw badRequest(
                        "Dias fechados não devem possuir horários"
                );
            }

            return;
        }

        if (request.horaAbertura() == null
                || request.horaFechamento() == null) {
            throw badRequest(
                    "Dias abertos devem possuir horários de abertura e fechamento"
            );
        }

        String formato = "([01][0-9]|2[0-3]):[0-5][0-9]";

        if (!request.horaAbertura().matches(formato)
                || !request.horaFechamento().matches(formato)) {
            throw badRequest(
                    "Os horários devem estar no formato HH:mm"
            );
        }

        try {
            LocalTime abertura = LocalTime.parse(
                    request.horaAbertura()
            );

            LocalTime fechamento = LocalTime.parse(
                    request.horaFechamento()
            );

            // 00:00 até 00:00 representa funcionamento 24 horas.
            boolean aberto24Horas =
                    abertura.equals(LocalTime.MIDNIGHT)
                            && fechamento.equals(LocalTime.MIDNIGHT);

            if (aberto24Horas) {
                return;
            }

            // Demais horários devem ter abertura anterior ao fechamento.
            if (!abertura.isBefore(fechamento)) {
                throw badRequest(
                        "A abertura deve ser anterior ao fechamento"
                );
            }

        } catch (DateTimeParseException exception) {
            throw badRequest(
                    "Horário de funcionamento inválido"
            );
        }
    }


    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    private HorarioFuncionamentoResponse toResponse(
            HorarioFuncionamento horario
    ) {
        return new HorarioFuncionamentoResponse(
                horario.getId(),
                horario.getEstabelecimento().getId(),
                horario.getDiaSemana(),
                horario.getHoraAbertura() == null
                        ? null
                        : horario.getHoraAbertura().toString(),
                horario.getHoraFechamento() == null
                        ? null
                        : horario.getHoraFechamento().toString(),
                horario.isFechado()
        );
    }
}
