package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ReciboRequestDTO;
import com.codagis.nordeste_servicos.dto.ReciboResponseDTO;
import com.codagis.nordeste_servicos.model.Recibo;
import com.codagis.nordeste_servicos.repository.ReciboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReciboService {

    @Autowired
    private ReciboRepository reciboRepository;

    @Transactional(readOnly = true)
    public List<ReciboResponseDTO> findAllRecibos() {
        List<Recibo> recibos = reciboRepository.findAllByOrderByDataCriacaoDesc();
        return recibos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReciboResponseDTO findReciboById(Long id) {
        Recibo recibo = reciboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recibo não encontrado com ID: " + id));
        return convertToDTO(recibo);
    }

    @Transactional
    public ReciboResponseDTO createRecibo(ReciboRequestDTO reciboRequestDTO) {
        Recibo recibo = new Recibo();
        recibo.setValor(reciboRequestDTO.getValor());
        recibo.setCliente(reciboRequestDTO.getCliente());
        recibo.setReferenteA(reciboRequestDTO.getReferenteA());
        recibo.setDataCriacao(LocalDateTime.now());
        recibo.setNumeroRecibo(generateNumeroRecibo());

        Recibo savedRecibo = reciboRepository.save(recibo);
        return convertToDTO(savedRecibo);
    }

    @Transactional
    public void deleteRecibo(Long id) {
        if (!reciboRepository.existsById(id)) {
            throw new RuntimeException("Recibo não encontrado com ID: " + id);
        }
        reciboRepository.deleteById(id);
    }

    private String generateNumeroRecibo() {
        // Busca o último recibo para pegar o número mais alto
        List<Recibo> allRecibos = reciboRepository.findAll();
        long maxNumber = 0;
        
        for (Recibo recibo : allRecibos) {
            if (recibo.getNumeroRecibo() != null && recibo.getNumeroRecibo().startsWith("REC-")) {
                try {
                    String numberPart = recibo.getNumeroRecibo().replace("REC-", "");
                    long number = Long.parseLong(numberPart);
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignora números inválidos
                }
            }
        }
        
        return "REC-" + String.format("%06d", maxNumber + 1);
    }

    private ReciboResponseDTO convertToDTO(Recibo recibo) {
        ReciboResponseDTO dto = new ReciboResponseDTO();
        dto.setId(recibo.getId());
        dto.setValor(recibo.getValor());
        dto.setCliente(recibo.getCliente());
        dto.setReferenteA(recibo.getReferenteA());
        dto.setDataCriacao(recibo.getDataCriacao());
        dto.setNumeroRecibo(recibo.getNumeroRecibo());
        return dto;
    }
}

