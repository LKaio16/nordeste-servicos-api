package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.OrdemServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Equipamento;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.EquipamentoRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para atribuir técnicos

    public List<OrdemServicoResponseDTO> findAllOrdensServico() {
        List<OrdemServico> ordens = ordemServicoRepository.findAll();
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdemServicoResponseDTO> findOrdensServicoByTecnicoId(Long tecnicoId) {
        List<OrdemServico> ordens = ordemServicoRepository.findByTecnicoAtribuidoId(tecnicoId);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdemServicoResponseDTO> findOrdensServicoByClienteId(Long clienteId) {
        List<OrdemServico> ordens = ordemServicoRepository.findByClienteId(clienteId);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdemServicoResponseDTO> findOrdensServicoByStatus(StatusOS status) {
        List<OrdemServico> ordens = ordemServicoRepository.findByStatus(status);
        return ordens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public OrdemServicoResponseDTO findOrdemServicoById(Long id) {
        OrdemServico ordem = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
        return convertToDTO(ordem);
    }

    // Método para criar uma nova OS (Geralmente por Admin)
    public OrdemServicoResponseDTO createOrdemServico(OrdemServicoRequestDTO ordemServicoRequestDTO) {
        // Verifica se Cliente e Equipamento existem
        Cliente cliente = clienteRepository.findById(ordemServicoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + ordemServicoRequestDTO.getClienteId()));

        Equipamento equipamento = equipamentoRepository.findById(ordemServicoRequestDTO.getEquipamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + ordemServicoRequestDTO.getEquipamentoId()));

        // Verifica se Técnico existe, se foi atribuído na criação
        Usuario tecnicoAtribuido = null;
        if (ordemServicoRequestDTO.getTecnicoAtribuidoId() != null) {
            tecnicoAtribuido = usuarioRepository.findById(ordemServicoRequestDTO.getTecnicoAtribuidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + ordemServicoRequestDTO.getTecnicoAtribuidoId()));
            // TODO: Adicionar validação para garantir que o usuário atribuído seja de fato um TÉCNICO
        }


        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setNumeroOS(generateNumeroOS()); // TODO: Implementar lógica de geração de número único
        ordemServico.setStatus(StatusOS.EM_ABERTO); // Status inicial
        ordemServico.setDataAbertura(LocalDateTime.now());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        ordemServico.setCliente(cliente);
        ordemServico.setEquipamento(equipamento);
        ordemServico.setTecnicoAtribuido(tecnicoAtribuido);
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        // analiseFalha e solucaoAplicada são preenchidos na execução

        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        return convertToDTO(savedOrdemServico);
    }

    // Método para atualizar uma OS (Pode ser usado por Admin ou Técnico para partes específicas)
    public OrdemServicoResponseDTO updateOrdemServico(Long id, OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico existingOrdemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));

        // Exemplo de como lidar com diferentes atualizações:
        // Se o status está sendo atualizado:
        if (ordemServicoRequestDTO.getStatus() != null && existingOrdemServico.getStatus() != ordemServicoRequestDTO.getStatus()) {
            existingOrdemServico.setStatus(ordemServicoRequestDTO.getStatus());
            if (ordemServicoRequestDTO.getStatus() == StatusOS.ENCERRADA) {
                existingOrdemServico.setDataFechamento(LocalDateTime.now());
                // TODO: Lógica adicional ao encerrar (ex: calcular total, marcar OS como finalizada para relatórios)
            }
            if (ordemServicoRequestDTO.getStatus() == StatusOS.CONCLUIDA) {
                // TODO: Lógica ao concluir (ex: permitir gerar orçamento)
            }
        }

        // Se os campos de execução estão sendo atualizados (geralmente pelo técnico)
        if (ordemServicoRequestDTO.getAnaliseFalha() != null) {
            existingOrdemServico.setAnaliseFalha(ordemServicoRequestDTO.getAnaliseFalha());
        }
        if (ordemServicoRequestDTO.getSolucaoAplicada() != null) {
            existingOrdemServico.setSolucaoAplicada(ordemServicoRequestDTO.getSolucaoAplicada());
        }

        // Lógica para atualizar outros campos da criação (se o Admin editar)
        // TODO: Implementar lógica para permitir Admin atualizar cliente, equipamento, tecnico, problema etc.
        // exigindo verificações de existência como na criação.

        OrdemServico updatedOrdemServico = ordemServicoRepository.save(existingOrdemServico);
        return convertToDTO(updatedOrdemServico);
    }

    public void deleteOrdemServico(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id);
        }
        // TODO: Implementar lógica para remover entidades relacionadas (registros de tempo, peças, fotos, etc.)
        //  se não estiverem configuradas para remoção em cascata (CascadeType.ALL, orphanRemoval = true)
        ordemServicoRepository.deleteById(id);
    }

    // TODO: Implementar lógica para geração de número de OS único
    private String generateNumeroOS() {
        return "OS-" + System.currentTimeMillis(); // Exemplo simples baseado em timestamp
    }


    // Método utilitário para converter Entidade para DTO
    private OrdemServicoResponseDTO convertToDTO(OrdemServico ordemServico) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.setId(ordemServico.getId());
        dto.setNumeroOS(ordemServico.getNumeroOS());
        dto.setStatus(ordemServico.getStatus());
        dto.setDataAbertura(ordemServico.getDataAbertura());
        dto.setDataAgendamento(ordemServico.getDataAgendamento());
        dto.setDataFechamento(ordemServico.getDataFechamento());
        // dto.setDataHoraEmissao = não é um campo da entidade, é gerado na emissão do PDF

        dto.setClienteId(ordemServico.getCliente().getId());
        dto.setNomeCliente(ordemServico.getCliente().getNomeCompleto()); // Popula campo opcional
        dto.setEquipamentoId(ordemServico.getEquipamento().getId());
        dto.setDescricaoEquipamento(ordemServico.getEquipamento().getMarcaModelo() + " - " + ordemServico.getEquipamento().getNumeroSerieChassi()); // Popula campo opcional
        if (ordemServico.getTecnicoAtribuido() != null) {
            dto.setTecnicoAtribuidoId(ordemServico.getTecnicoAtribuido().getId());
            dto.setNomeTecnicoAtribuido(ordemServico.getTecnicoAtribuido().getNome()); // Popula campo opcional
        }

        dto.setProblemaRelatado(ordemServico.getProblemaRelatado());
        dto.setAnaliseFalha(ordemServico.getAnaliseFalha());
        dto.setSolucaoAplicada(ordemServico.getSolucaoAplicada());

        return dto;
    }

    // Método utilitário para converter DTO para Entidade (usado principalmente na criação)
    // Atualizações são feitas diretamente na entidade existente no updateService
    private OrdemServico convertToEntity(OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico ordemServico = new OrdemServico();
        // Campos preenchidos aqui são os da criação inicial.
        // Relacionamentos (Cliente, Equipamento, Tecnico) são definidos no serviço após busca pelos IDs
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        // status, numeroOS, datasAbertura/Fechamento/Emissao, analiseFalha, solucaoAplicada definidos no serviço ou execução

        return ordemServico;
    }


    // Método público para obter o próximo número de OS formatado
    public String getNextOsNumber() {
        // Busca a última OS criada (baseado no ID, assumindo que IDs são sequenciais)
        // Uma alternativa seria buscar pelo maior numeroOS se ele for numérico ou tiver um padrão ordenável.
        OrdemServico lastOs = ordemServicoRepository.findTopByOrderByIdDesc();

        long nextNumber = 1; // Número inicial se não houver OS anterior
        if (lastOs != null && lastOs.getNumeroOS() != null) {
            try {
                // Tenta extrair a parte numérica do último númeroOS
                // Exemplo: Se numeroOS for "OS-2549" ou "#2549"
                String lastNumStr = lastOs.getNumeroOS().replaceAll("[^0-9]", "");
                if (!lastNumStr.isEmpty()) {
                    long lastNum = Long.parseLong(lastNumStr);
                    nextNumber = lastNum + 1;
                }
            } catch (NumberFormatException e) {
                // Se não conseguir parsear, loga um aviso e usa o default 1
                // Ou implementa uma lógica mais robusta baseada no seu padrão de numeroOS
                System.err.println("WARN: Não foi possível parsear o último numeroOS: " + lastOs.getNumeroOS());
                // Poderia tentar buscar o ID máximo como fallback?
                // nextNumber = lastOs.getId() + 1; // Alternativa se numeroOS não for confiável
            }
        }

        // Formata o próximo número (Ex: #2550)
        // Ajuste a formatação conforme o padrão desejado no app Flutter
        // return String.format("#%d", nextNumber);
        return String.format(String.valueOf(nextNumber));
    }
}