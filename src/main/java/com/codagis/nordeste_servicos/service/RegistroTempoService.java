package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.RegistroTempoRequestDTO;
import com.codagis.nordeste_servicos.dto.RegistroTempoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException; // Precisaremos de uma exceção de negócio
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.RegistroTempo;
import com.codagis.nordeste_servicos.model.TipoServico;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.RegistroTempoRepository;
import com.codagis.nordeste_servicos.repository.TipoServicoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistroTempoService {

    @Autowired
    private RegistroTempoRepository registroTempoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    // Método para listar registros de tempo de uma OS
    public List<RegistroTempoResponseDTO> findRegistrosTempoByOrdemServicoId(Long ordemServicoId) {
        List<RegistroTempo> registros = registroTempoRepository.findByOrdemServicoId(ordemServicoId);
        return registros.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
    }

    public RegistroTempoResponseDTO findRegistroTempoById(Long id) {
         RegistroTempo registro = registroTempoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de Tempo não encontrado com ID: " + id));
         return convertToDTO(registro);
    }


    // Método para iniciar um novo registro de tempo (iniciar timer)
    public RegistroTempoResponseDTO iniciarRegistroTempo(RegistroTempoRequestDTO registroTempoRequestDTO) {
        // Verifica se já existe um timer ativo para este técnico nesta OS
        Optional<RegistroTempo> registroAtivo = registroTempoRepository.findByOrdemServicoIdAndTecnicoIdAndHoraTerminoIsNull(
                registroTempoRequestDTO.getOrdemServicoId(), registroTempoRequestDTO.getTecnicoId());

        if (registroAtivo.isPresent()) {
            throw new BusinessException("Já existe um registro de tempo ativo para este técnico nesta OS.");
        }

        OrdemServico ordemServico = ordemServicoRepository.findById(registroTempoRequestDTO.getOrdemServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + registroTempoRequestDTO.getOrdemServicoId()));

        Usuario tecnico = usuarioRepository.findById(registroTempoRequestDTO.getTecnicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + registroTempoRequestDTO.getTecnicoId()));
         // TODO: Validar se o usuário é um TÉCNICO

        TipoServico tipoServico = tipoServicoRepository.findById(registroTempoRequestDTO.getTipoServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + registroTempoRequestDTO.getTipoServicoId()));


        RegistroTempo novoRegistro = new RegistroTempo();
        novoRegistro.setOrdemServico(ordemServico);
        novoRegistro.setTecnico(tecnico);
        novoRegistro.setTipoServico(tipoServico);
        novoRegistro.setHoraInicio(LocalDateTime.now()); // Hora de início é AGORA

        RegistroTempo savedRegistro = registroTempoRepository.save(novoRegistro);
        return convertToDTO(savedRegistro);
    }

     // Método para finalizar um registro de tempo (parar/pausar timer)
    public RegistroTempoResponseDTO finalizarRegistroTempo(Long id) {
         RegistroTempo registro = registroTempoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de Tempo não encontrado com ID: " + id));

         if (registro.getHoraTermino() != null) {
             throw new BusinessException("Este registro de tempo já foi finalizado.");
         }

         registro.setHoraTermino(LocalDateTime.now()); // Hora de término é AGORA
         registro.setHorasTrabalhadas(calculateHours(registro.getHoraInicio(), registro.getHoraTermino()));

         RegistroTempo updatedRegistro = registroTempoRepository.save(registro);
         return convertToDTO(updatedRegistro);
    }

    // Método para calcular as horas trabalhadas
    private Double calculateHours(LocalDateTime inicio, LocalDateTime termino) {
        if (inicio == null || termino == null) {
            return 0.0;
        }
        Duration duration = Duration.between(inicio, termino);
        return (double) duration.toMinutes() / 60.0; // Retorna em horas
    }


    // Método para deletar um registro de tempo
    public void deleteRegistroTempo(Long id) {
        if (!registroTempoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Registro de Tempo não encontrado com ID: " + id);
        }
        registroTempoRepository.deleteById(id);
    }


    private RegistroTempoResponseDTO convertToDTO(RegistroTempo registroTempo) {
        RegistroTempoResponseDTO dto = new RegistroTempoResponseDTO();
        dto.setId(registroTempo.getId());
        dto.setOrdemServicoId(registroTempo.getOrdemServico().getId());
        dto.setTecnicoId(registroTempo.getTecnico().getId());
        dto.setNomeTecnico(registroTempo.getTecnico().getNome()); // Popula nome do técnico
        dto.setTipoServicoId(registroTempo.getTipoServico().getId());
        dto.setDescricaoTipoServico(registroTempo.getTipoServico().getDescricao()); // Popula descrição do tipo de serviço
        dto.setHoraInicio(registroTempo.getHoraInicio());
        dto.setHoraTermino(registroTempo.getHoraTermino());
        dto.setHorasTrabalhadas(registroTempo.getHorasTrabalhadas());
        return dto;
    }

    // Método para converter DTO para Entidade (usado principalmente na criação/início)
    private RegistroTempo convertToEntity(RegistroTempoRequestDTO registroTempoRequestDTO) {
        RegistroTempo registro = new RegistroTempo();
        // Relacionamentos (OrdemServico, Tecnico, TipoServico) e HoraInicio são definidos no serviço
        // HoraTermino e HorasTrabalhadas definidos ao finalizar
        return registro;
    }
}