package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.RegistroDeslocamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.RegistroDeslocamentoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.RegistroDeslocamento;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.RegistroDeslocamentoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate; // Importar LocalDate
import java.util.stream.Collectors;

@Service
public class RegistroDeslocamentoService {

    @Autowired
    private RegistroDeslocamentoRepository registroDeslocamentoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDeslocamentoResponseDTO> findRegistrosDeslocamentoByOrdemServicoId(Long ordemServicoId) {
        List<RegistroDeslocamento> registros = registroDeslocamentoRepository.findByOrdemServicoId(ordemServicoId);
        return registros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RegistroDeslocamentoResponseDTO findRegistroDeslocamentoById(Long id) {
        RegistroDeslocamento registro = registroDeslocamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de Deslocamento não encontrado com ID: " + id));
        return convertToDTO(registro);
    }

    public RegistroDeslocamentoResponseDTO createRegistroDeslocamento(RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(registroDeslocamentoRequestDTO.getOrdemServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + registroDeslocamentoRequestDTO.getOrdemServicoId()));

        Usuario tecnico = usuarioRepository.findById(registroDeslocamentoRequestDTO.getTecnicoId()) // CORRIGIDO AQUI
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + registroDeslocamentoRequestDTO.getTecnicoId()));
        // TODO: Validar se o usuário é um TÉCNICO

        RegistroDeslocamento novoRegistro = convertToEntity(registroDeslocamentoRequestDTO);
        novoRegistro.setOrdemServico(ordemServico);
        novoRegistro.setTecnico(tecnico);
        novoRegistro.setTotalKm(calculateTotalKm(registroDeslocamentoRequestDTO.getKmInicial(), registroDeslocamentoRequestDTO.getKmFinal()));

        RegistroDeslocamento savedRegistro = registroDeslocamentoRepository.save(novoRegistro);
        return convertToDTO(savedRegistro);
    }

    public RegistroDeslocamentoResponseDTO updateRegistroDeslocamento(Long id, RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {
        RegistroDeslocamento existingRegistro = registroDeslocamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de Deslocamento não encontrado com ID: " + id));

        OrdemServico ordemServico = ordemServicoRepository.findById(registroDeslocamentoRequestDTO.getOrdemServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + registroDeslocamentoRequestDTO.getOrdemServicoId()));

        Usuario tecnico = usuarioRepository.findById(registroDeslocamentoRequestDTO.getTecnicoId()) // CORRIGIDO AQUI
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + registroDeslocamentoRequestDTO.getTecnicoId()));
        // TODO: Validar se o usuário é um TÉCNICO

        existingRegistro.setOrdemServico(ordemServico);
        existingRegistro.setTecnico(tecnico);
        existingRegistro.setData(registroDeslocamentoRequestDTO.getData());
        existingRegistro.setPlacaVeiculo(registroDeslocamentoRequestDTO.getPlacaVeiculo());
        existingRegistro.setKmInicial(registroDeslocamentoRequestDTO.getKmInicial());
        existingRegistro.setKmFinal(registroDeslocamentoRequestDTO.getKmFinal());
        existingRegistro.setSaidaDe(registroDeslocamentoRequestDTO.getSaidaDe());
        existingRegistro.setChegadaEm(registroDeslocamentoRequestDTO.getChegadaEm());
        existingRegistro.setTotalKm(calculateTotalKm(registroDeslocamentoRequestDTO.getKmInicial(), registroDeslocamentoRequestDTO.getKmFinal()));

        RegistroDeslocamento updatedRegistro = registroDeslocamentoRepository.save(existingRegistro);
        return convertToDTO(updatedRegistro);
    }

    private Double calculateTotalKm(Double kmInicial, Double kmFinal) {
        if (kmInicial == null || kmFinal == null) {
            return null;
        }
        if (kmFinal < kmInicial) {
            // TODO: Lançar BusinessException se KM Final for menor que Inicial
            return 0.0;
        }
        return kmFinal - kmInicial;
    }

    public void deleteRegistroDeslocamento(Long id) {
        if (!registroDeslocamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registro de Deslocamento não encontrado com ID: " + id);
        }
        registroDeslocamentoRepository.deleteById(id);
    }

    private RegistroDeslocamentoResponseDTO convertToDTO(RegistroDeslocamento registroDeslocamento) {
        RegistroDeslocamentoResponseDTO dto = new RegistroDeslocamentoResponseDTO();
        dto.setId(registroDeslocamento.getId());
        dto.setOrdemServicoId(registroDeslocamento.getOrdemServico().getId());
        dto.setTecnicoId(registroDeslocamento.getTecnico().getId());
        dto.setNomeTecnico(registroDeslocamento.getTecnico().getNome());
        dto.setData(registroDeslocamento.getData());
        dto.setPlacaVeiculo(registroDeslocamento.getPlacaVeiculo());
        dto.setKmInicial(registroDeslocamento.getKmInicial());
        dto.setKmFinal(registroDeslocamento.getKmFinal());
        dto.setTotalKm(registroDeslocamento.getTotalKm());
        dto.setSaidaDe(registroDeslocamento.getSaidaDe());
        dto.setChegadaEm(registroDeslocamento.getChegadaEm());
        return dto;
    }

    private RegistroDeslocamento convertToEntity(RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {
        RegistroDeslocamento registro = new RegistroDeslocamento();
        registro.setData(registroDeslocamentoRequestDTO.getData());
        registro.setPlacaVeiculo(registroDeslocamentoRequestDTO.getPlacaVeiculo());
        registro.setKmInicial(registroDeslocamentoRequestDTO.getKmInicial());
        registro.setKmFinal(registroDeslocamentoRequestDTO.getKmFinal());
        registro.setSaidaDe(registroDeslocamentoRequestDTO.getSaidaDe());
        registro.setChegadaEm(registroDeslocamentoRequestDTO.getChegadaEm());
        return registro;
    }
}