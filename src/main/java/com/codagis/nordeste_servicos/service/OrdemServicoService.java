package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.*;


import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.AssinaturaOS;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Equipamento;
import com.codagis.nordeste_servicos.model.FotoOS;
import com.codagis.nordeste_servicos.model.ItemOSUtilizado;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.RegistroDeslocamento;
import com.codagis.nordeste_servicos.model.RegistroTempo;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.model.Usuario; // A entidade Tecnico é Usuario

import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.EquipamentoRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegistroTempoService registroTempoService;

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> findAllOrdensServico(String searchTerm) {
        List<OrdemServico> ordens;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            ordens = ordemServicoRepository.searchByTerm(searchTerm);
        } else {
            ordens = ordemServicoRepository.findAll();
        }
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> findOrdensServicoByTecnicoId(Long tecnicoId) {
        List<OrdemServico> ordens = ordemServicoRepository.findByTecnicoAtribuidoId(tecnicoId);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> findOrdensServicoByClienteId(Long clienteId) {
        List<OrdemServico> ordens = ordemServicoRepository.findByClienteId(clienteId);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> findOrdensServicoByStatus(StatusOS status) {
        List<OrdemServico> ordens = ordemServicoRepository.findByStatus(status);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO findOrdemServicoById(Long id) {
        OrdemServico ordem = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
        return convertToDTO(ordem);
    }

    // Dentro da sua classe OrdemServicoService.java

    @Transactional
    public OrdemServicoResponseDTO createOrdemServico(OrdemServicoRequestDTO ordemServicoRequestDTO) {

        // --- 1. AJUSTE NO ACESSO AO ID DO CLIENTE ---
        // Adicionamos uma verificação para garantir que o objeto cliente e seu ID não são nulos.
        if (ordemServicoRequestDTO.getCliente() == null || ordemServicoRequestDTO.getCliente().getId() == null) {
            throw new BusinessException("O ID do Cliente é obrigatório."); // Lança uma exceção de negócio
        }
        Long clienteId = ordemServicoRequestDTO.getCliente().getId(); // Pega o ID de dentro do objeto
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        // --- 2. AJUSTE NO ACESSO AO ID DO EQUIPAMENTO ---
        if (ordemServicoRequestDTO.getEquipamento() == null || ordemServicoRequestDTO.getEquipamento().getId() == null) {
            throw new BusinessException("O ID do Equipamento é obrigatório.");
        }
        Long equipamentoId = ordemServicoRequestDTO.getEquipamento().getId(); // Pega o ID de dentro do objeto
        Equipamento equipamento = equipamentoRepository.findById(equipamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + equipamentoId));

        // A lógica para o técnico já estava correta, vamos mantê-la.
        Usuario tecnicoAtribuido = null;
        if (ordemServicoRequestDTO.getTecnicoAtribuido() != null && ordemServicoRequestDTO.getTecnicoAtribuido().getId() != null) {
            Long tecnicoId = ordemServicoRequestDTO.getTecnicoAtribuido().getId();
            tecnicoAtribuido = usuarioRepository.findById(tecnicoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + tecnicoId));
            // TODO: Validar se o usuário é realmente um TÉCNICO
        }

        // A criação da entidade OrdemServico continua a mesma, pois ela já espera os objetos completos.
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setNumeroOS(generateNewNumeroOS());
        ordemServico.setStatus(StatusOS.EM_ABERTO);
        ordemServico.setDataAbertura(LocalDateTime.now());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        ordemServico.setCliente(cliente);
        ordemServico.setPrioridade(ordemServicoRequestDTO.getPrioridade() != null ? ordemServicoRequestDTO.getPrioridade() : PrioridadeOS.MEDIA);
        ordemServico.setEquipamento(equipamento);
        ordemServico.setTecnicoAtribuido(tecnicoAtribuido);
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());

        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        return convertToDTO(savedOrdemServico);
    }

    @Transactional
    public OrdemServicoResponseDTO updateOrdemServico(Long id, OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico existingOrdemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));

        // Validações de Negócio (Ex: OS concluída não pode ser alterada)
        // if (existingOrdemServico.getStatus() == StatusOS.CONCLUIDA || existingOrdemServico.getStatus() == StatusOS.CANCELADA) {
        //    throw new BusinessException("OS não pode ser alterada com status " + existingOrdemServico.getStatus());
        // }

        // Atualiza o Cliente, se um novo ID for fornecido
        if (ordemServicoRequestDTO.getCliente() != null && ordemServicoRequestDTO.getCliente().getId() != null) {
            Long novoClienteId = ordemServicoRequestDTO.getCliente().getId();
            if (!novoClienteId.equals(existingOrdemServico.getCliente().getId())) {
                Cliente novoCliente = clienteRepository.findById(novoClienteId)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + novoClienteId));
                existingOrdemServico.setCliente(novoCliente);
            }
        }

        // Atualiza o Equipamento, se um novo ID for fornecido
        if (ordemServicoRequestDTO.getEquipamento() != null && ordemServicoRequestDTO.getEquipamento().getId() != null) {
            Long novoEquipamentoId = ordemServicoRequestDTO.getEquipamento().getId();
            if (!novoEquipamentoId.equals(existingOrdemServico.getEquipamento().getId())) {
                Equipamento novoEquipamento = equipamentoRepository.findById(novoEquipamentoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + novoEquipamentoId));
                existingOrdemServico.setEquipamento(novoEquipamento);
            }
        }

        // Atualiza o Técnico Atribuído
        if (ordemServicoRequestDTO.getTecnicoAtribuido() != null) {
            Long novoTecnicoId = ordemServicoRequestDTO.getTecnicoAtribuido().getId();
            // Caso queira desatribuir o técnico
            if (novoTecnicoId == null) {
                existingOrdemServico.setTecnicoAtribuido(null);
            } else {
                // Caso queira atribuir um novo técnico ou alterar o existente
                if (existingOrdemServico.getTecnicoAtribuido() == null || !novoTecnicoId.equals(existingOrdemServico.getTecnicoAtribuido().getId())) {
                    Usuario novoTecnico = usuarioRepository.findById(novoTecnicoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + novoTecnicoId));
                    existingOrdemServico.setTecnicoAtribuido(novoTecnico);
                }
            }
        }

        if (ordemServicoRequestDTO.getProblemaRelatado() != null) {
            existingOrdemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        }
        if (ordemServicoRequestDTO.getDataAgendamento() != null) {
            existingOrdemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        }
        if (ordemServicoRequestDTO.getPrioridade() != null) {
            existingOrdemServico.setPrioridade(ordemServicoRequestDTO.getPrioridade());
        }
        if (ordemServicoRequestDTO.getAnaliseFalha() != null) {
            existingOrdemServico.setAnaliseFalha(ordemServicoRequestDTO.getAnaliseFalha());
        }
        if (ordemServicoRequestDTO.getSolucaoAplicada() != null) {
            existingOrdemServico.setSolucaoAplicada(ordemServicoRequestDTO.getSolucaoAplicada());
        }
        if (ordemServicoRequestDTO.getStatus() != null && existingOrdemServico.getStatus() != ordemServicoRequestDTO.getStatus()) {
            existingOrdemServico.setStatus(ordemServicoRequestDTO.getStatus());
            boolean isFinalStatus = ordemServicoRequestDTO.getStatus() == StatusOS.CONCLUIDA || ordemServicoRequestDTO.getStatus() == StatusOS.ENCERRADA;
            if (isFinalStatus && existingOrdemServico.getDataFechamento() == null) {
                existingOrdemServico.setDataFechamento(LocalDateTime.now());
            } else if (!isFinalStatus) {
                existingOrdemServico.setDataFechamento(null);
            }
        }

        OrdemServico updatedOrdemServico = ordemServicoRepository.save(existingOrdemServico);
        return convertToDTO(updatedOrdemServico);
    }

    @Transactional
    public void deleteOrdemServico(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id);
        }
        try {
            ordemServicoRepository.deleteById(id);
            ordemServicoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir esta Ordem de Serviço, pois ela já está vinculada a um Orçamento existente.");
        }
    }

    @Transactional
    public void updateOrdemServicoStatus(Long id, StatusOS novoStatus) {
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));

        // TODO: Adicionar validações de segurança, se necessário.
        // Ex: um técnico só pode mudar de ABERTA para EM_ANDAMENTO.

        ordemServico.setStatus(novoStatus);
        ordemServicoRepository.save(ordemServico);
    }

    private String generateNewNumeroOS() {
        return "OS-" + getNextOsNumber();
    }

    @Transactional(readOnly = true)
    private OrdemServicoResponseDTO convertToDTO(OrdemServico ordemServico) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.setId(ordemServico.getId());
        dto.setNumeroOS(ordemServico.getNumeroOS());
        dto.setStatus(ordemServico.getStatus());
        dto.setDataAbertura(ordemServico.getDataAbertura());
        dto.setDataAgendamento(ordemServico.getDataAgendamento());
        dto.setDataFechamento(ordemServico.getDataFechamento());
        dto.setPrioridade(ordemServico.getPrioridade());

        // Mapeamento do Cliente (já estava correto)
        if (ordemServico.getCliente() != null) {
            Cliente clienteEntity = ordemServico.getCliente();
            dto.setCliente(new ClienteResponseDTO(
                    clienteEntity.getId(),
                    clienteEntity.getTipoCliente(),
                    clienteEntity.getNomeCompleto(),
                    clienteEntity.getCpfCnpj(),
                    clienteEntity.getEmail(),
                    clienteEntity.getTelefonePrincipal(),
                    clienteEntity.getTelefoneAdicional(),
                    clienteEntity.getCep(),
                    clienteEntity.getRua(),
                    clienteEntity.getNumero(),
                    clienteEntity.getComplemento(),
                    clienteEntity.getBairro(),
                    clienteEntity.getCidade(),
                    clienteEntity.getEstado()
            ));
        } else {
            dto.setCliente(null);
        }


        if (ordemServico.getEquipamento() != null) {
            Equipamento equipamentoEntity = ordemServico.getEquipamento();
            dto.setEquipamento(new EquipamentoResponseDTO(
                    equipamentoEntity.getId(),
                    equipamentoEntity.getTipo(),
                    equipamentoEntity.getMarcaModelo(),
                    equipamentoEntity.getNumeroSerieChassi(),
                    equipamentoEntity.getHorimetro(),
                    equipamentoEntity.getCliente().getId()
            ));
        } else {
            dto.setEquipamento(null);
        }


        if (ordemServico.getTecnicoAtribuido() != null) {
            Usuario tecnicoEntity = ordemServico.getTecnicoAtribuido();
            dto.setTecnicoAtribuido(new UsuarioResponseDTO(
                    tecnicoEntity.getId(),
                    tecnicoEntity.getNome(),
                    tecnicoEntity.getCracha(),
                    tecnicoEntity.getEmail(),
                    tecnicoEntity.getPerfil(),
                    tecnicoEntity.getFotoPerfil()
            ));
        } else {
            dto.setTecnicoAtribuido(null);
        }

        if (ordemServico.getRegistrosTempo() != null && !ordemServico.getRegistrosTempo().isEmpty()) {
            dto.setRegistrosTempo(
                    ordemServico.getRegistrosTempo().stream()
                            .map(this::convertRegistroTempoToDTO) // Usa um novo método auxiliar
                            .collect(Collectors.toList())
            );
        }


        dto.setProblemaRelatado(ordemServico.getProblemaRelatado());
        dto.setAnaliseFalha(ordemServico.getAnaliseFalha());
        dto.setSolucaoAplicada(ordemServico.getSolucaoAplicada());

        // As listas de registros, itens, fotos, etc., continuam iguais.
        // ... (resto do seu código)

        return dto;
    }

    private OrdemServico convertToEntity(OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        return ordemServico;
    }

    public String getNextOsNumber() {
        Optional<OrdemServico> ultimaOS = Optional.ofNullable(ordemServicoRepository.findTopByOrderByIdDesc());

        long nextNumber = 1;
        if (ultimaOS.isPresent() && ultimaOS.get().getNumeroOS() != null) {
            try {
                String lastNumStr = ultimaOS.get().getNumeroOS().replaceAll("OS-", "").replaceAll("[^0-9]", "");
                if (!lastNumStr.isEmpty()) {
                    long lastNum = Long.parseLong(lastNumStr);
                    nextNumber = lastNum + 1;
                }
            } catch (NumberFormatException e) {
                System.err.println("WARN: Não foi possível parsear o último numeroOS: " + ultimaOS.get().getNumeroOS() + ". Gerando novo número a partir de 1.");
            }
        }
        return String.valueOf(nextNumber);
    }

    private String formatarHorasDecimais(Double horasDecimais) {
        if (horasDecimais == null || horasDecimais < 0) {
            return "N/A";
        }
        int horas = horasDecimais.intValue();
        int minutos = (int) Math.round((horasDecimais - horas) * 60);
        return String.format("%dh %02dm", horas, minutos);
    }

    private RegistroTempoResponseDTO convertRegistroTempoToDTO(RegistroTempo registro) {
        if (registro == null) {
            return null;
        }

        RegistroTempoResponseDTO dto = new RegistroTempoResponseDTO();

        dto.setId(registro.getId());
        dto.setOrdemServicoId(registro.getOrdemServico().getId());
        dto.setTecnicoId(registro.getTecnico().getId());
        dto.setNomeTecnico(registro.getTecnico().getNome());
        dto.setHoraInicio(registro.getHoraInicio());
        dto.setHoraTermino(registro.getHoraTermino());
        dto.setHorasTrabalhadas(registro.getHorasTrabalhadas());

        dto.setTempoFormatado(formatarHorasDecimais(registro.getHorasTrabalhadas()));

        return dto;
    }
}