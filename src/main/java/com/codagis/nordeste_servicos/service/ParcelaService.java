package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.GerarParcelasRequestDTO;
import com.codagis.nordeste_servicos.dto.ParcelaRequestDTO;
import com.codagis.nordeste_servicos.dto.ParcelaResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.*;
import com.codagis.nordeste_servicos.repository.ContaRepository;
import com.codagis.nordeste_servicos.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParcelaService {

    @Autowired
    private ParcelaRepository parcelaRepository;
    @Autowired
    private ContaRepository contaRepository;

    @Transactional(readOnly = true)
    public List<ParcelaResponseDTO> findByContaId(Long contaId) {
        return parcelaRepository.findByContaIdOrderByNumeroParcela(contaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParcelaResponseDTO create(Long contaId, ParcelaRequestDTO dto) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + contaId));
        Parcela p = new Parcela();
        p.setConta(conta);
        p.setNumeroParcela(dto.getNumeroParcela());
        p.setValor(dto.getValor());
        p.setDataVencimento(dto.getDataVencimento());
        p.setStatus(StatusConta.PENDENTE);
        p = parcelaRepository.save(p);
        return toResponse(p);
    }

    @Transactional
    public List<ParcelaResponseDTO> gerarParcelas(Long contaId, GerarParcelasRequestDTO dto) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + contaId));
        List<Parcela> existentes = parcelaRepository.findByContaIdOrderByNumeroParcela(contaId);
        if (!existentes.isEmpty()) {
            throw new IllegalStateException("Esta conta já possui parcelas. Remova todas as parcelas antes de gerar novas.");
        }
        int qtd = dto.getQuantidade();
        if (qtd < 1) throw new IllegalArgumentException("Quantidade de parcelas deve ser pelo menos 1");
        BigDecimal valorTotal = conta.getValor();
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(qtd), 2, RoundingMode.HALF_UP);
        LocalDate data = dto.getPrimeiraDataVencimento();
        List<Parcela> criadas = new ArrayList<>();
        for (int i = 1; i <= qtd; i++) {
            Parcela p = new Parcela();
            p.setConta(conta);
            p.setNumeroParcela(i);
            p.setValor(valorParcela);
            p.setDataVencimento(data);
            p.setStatus(StatusConta.PENDENTE);
            p = parcelaRepository.save(p);
            criadas.add(p);
            data = data.plusMonths(1);
        }
        return criadas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ParcelaResponseDTO marcarComoPaga(Long contaId, Long parcelaId, LocalDate dataPagamento) {
        Parcela p = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada com ID: " + parcelaId));
        if (!p.getConta().getId().equals(contaId)) {
            throw new ResourceNotFoundException("Parcela não pertence à conta informada.");
        }
        p.setStatus(StatusConta.PAGO);
        p.setDataPagamento(dataPagamento != null ? dataPagamento : LocalDate.now());
        p.setValorPago(p.getValor());
        p = parcelaRepository.save(p);

        atualizarContaSeTodasParcelasPagas(p.getConta());
        return toResponse(p);
    }

    private void atualizarContaSeTodasParcelasPagas(Conta conta) {
        List<Parcela> parcelas = parcelaRepository.findByContaIdOrderByNumeroParcela(conta.getId());
        boolean todasPagas = parcelas.stream().allMatch(parc -> parc.getStatus() == StatusConta.PAGO);
        if (todasPagas && !parcelas.isEmpty()) {
            BigDecimal totalPago = parcelas.stream()
                    .map(Parcela::getValorPago)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            conta.setStatus(StatusConta.PAGO);
            conta.setValorPago(totalPago);
            LocalDate ultimaData = parcelas.stream()
                    .map(Parcela::getDataPagamento)
                    .filter(d -> d != null)
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now());
            conta.setDataPagamento(ultimaData);
            contaRepository.save(conta);
        }
    }

    @Transactional
    public void delete(Long contaId, Long parcelaId) {
        Parcela p = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada com ID: " + parcelaId));
        if (!p.getConta().getId().equals(contaId)) {
            throw new ResourceNotFoundException("Parcela não pertence à conta informada.");
        }
        parcelaRepository.delete(p);
    }

    @Transactional
    public void deleteAllByContaId(Long contaId) {
        parcelaRepository.deleteByConta_Id(contaId);
    }

    @Transactional
    public void marcarTodasComoPagas(Long contaId) {
        LocalDate hoje = LocalDate.now();
        List<Parcela> lista = parcelaRepository.findByContaIdOrderByNumeroParcela(contaId);
        for (Parcela p : lista) {
            if (p.getStatus() == StatusConta.PENDENTE) {
                p.setStatus(StatusConta.PAGO);
                p.setDataPagamento(hoje);
                p.setValorPago(p.getValor() != null ? p.getValor() : BigDecimal.ZERO);
                parcelaRepository.save(p);
            }
        }
    }

    @Transactional
    public ParcelaResponseDTO update(Long contaId, Long parcelaId, ParcelaRequestDTO dto) {
        Parcela p = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada com ID: " + parcelaId));
        if (!p.getConta().getId().equals(contaId)) {
            throw new ResourceNotFoundException("Parcela não pertence à conta informada.");
        }
        if (p.getStatus() != StatusConta.PENDENTE) {
            throw new IllegalStateException("Só é possível editar parcelas pendentes.");
        }
        p.setNumeroParcela(dto.getNumeroParcela());
        p.setValor(dto.getValor());
        p.setDataVencimento(dto.getDataVencimento());
        p = parcelaRepository.save(p);
        return toResponse(p);
    }

    private ParcelaResponseDTO toResponse(Parcela p) {
        ParcelaResponseDTO dto = new ParcelaResponseDTO();
        dto.setId(p.getId());
        dto.setContaId(p.getConta().getId());
        dto.setNumeroParcela(p.getNumeroParcela());
        dto.setValor(p.getValor());
        dto.setDataVencimento(p.getDataVencimento());
        dto.setValorPago(p.getValorPago() != null ? p.getValorPago() : BigDecimal.ZERO);
        dto.setDataPagamento(p.getDataPagamento());
        dto.setStatus(p.getStatus());
        return dto;
    }
}
