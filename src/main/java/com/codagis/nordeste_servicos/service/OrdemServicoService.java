package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.OrdemServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException; // Importado para validações de negócio
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Equipamento;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.PrioridadeOS; // Importe PrioridadeOS
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
            // TODO: Adicionar validação para garantir que o usuário atribuído seja de fato um TÉCNICO (ex: verificando um campo de perfil no Usuario)
        }

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setNumeroOS(generateNewNumeroOS()); // Usa o novo método para gerar o número
        ordemServico.setStatus(StatusOS.EM_ABERTO); // Status inicial
        ordemServico.setDataAbertura(LocalDateTime.now());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        ordemServico.setCliente(cliente);
        ordemServico.setPrioridade(ordemServicoRequestDTO.getPrioridade() != null ? ordemServicoRequestDTO.getPrioridade() : PrioridadeOS.MEDIA); // Define prioridade ou padrão
        ordemServico.setEquipamento(equipamento);
        ordemServico.setTecnicoAtribuido(tecnicoAtribuido);
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        // analiseFalha e solucaoAplicada são preenchidos na execução, então não definimos aqui.

        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        return convertToDTO(savedOrdemServico);
    }

    // Método para atualizar uma OS (Pode ser usado por Admin ou Técnico para partes específicas)
    public OrdemServicoResponseDTO updateOrdemServico(Long id, OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico existingOrdemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id));

        // **1. Validações de Negócio (Implementar a lógica completa de segurança e permissões):**
        // TODO: Implementar validação de segurança baseada no perfil (Admin vs Técnico) e status da OS.
        // Exemplo de regra: Uma OS "CONCLUIDA" ou "CANCELADA" não deve ser editável.
        //if (existingOrdemServico.getStatus() == StatusOS.CONCLUIDA || existingOrdemServico.getStatus() == StatusOS.CANCELADA) {
        //    throw new BusinessException("Não é possível atualizar uma Ordem de Serviço com status " + existingOrdemServico.getStatus().name() + ".");
        //}
        // TODO: Outras regras como: Técnico só pode alterar status para EM_ANDAMENTO, CONCLUIDA. Admin pode tudo.
        // Ou campos como clienteId/equipamentoId só podem ser alterados por Admin.

        // **2. Atualização dos Campos da OS:**

        // Atualiza Cliente, se fornecido e diferente do atual (geralmente permitido por Admin)
        if (ordemServicoRequestDTO.getClienteId() != null && !ordemServicoRequestDTO.getClienteId().equals(existingOrdemServico.getCliente().getId())) {
            Cliente cliente = clienteRepository.findById(ordemServicoRequestDTO.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + ordemServicoRequestDTO.getClienteId()));
            existingOrdemServico.setCliente(cliente);
        }

        // Atualiza Equipamento, se fornecido e diferente do atual (geralmente permitido por Admin)
        if (ordemServicoRequestDTO.getEquipamentoId() != null && !ordemServicoRequestDTO.getEquipamentoId().equals(existingOrdemServico.getEquipamento().getId())) {
            Equipamento equipamento = equipamentoRepository.findById(ordemServicoRequestDTO.getEquipamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + ordemServicoRequestDTO.getEquipamentoId()));
            existingOrdemServico.setEquipamento(equipamento);
        }

        // Atualiza Técnico Atribuído, se fornecido e diferente do atual (geralmente permitido por Admin, ou técnico pode se reatribuir)
        if (ordemServicoRequestDTO.getTecnicoAtribuidoId() != null) {
            // Se o ID do técnico na requisição é diferente do atual
            if (existingOrdemServico.getTecnicoAtribuido() == null || !ordemServicoRequestDTO.getTecnicoAtribuidoId().equals(existingOrdemServico.getTecnicoAtribuido().getId())) {
                Usuario tecnico = usuarioRepository.findById(ordemServicoRequestDTO.getTecnicoAtribuidoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Técnico atribuído não encontrado com ID: " + ordemServicoRequestDTO.getTecnicoAtribuidoId()));
                existingOrdemServico.setTecnicoAtribuido(tecnico);
            }
        } else {
            // Permite desatribuir o técnico se o ID for nulo no DTO
            existingOrdemServico.setTecnicoAtribuido(null);
        }

        // Atualiza Problema Relatado
        if (ordemServicoRequestDTO.getProblemaRelatado() != null) {
            existingOrdemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        }

        // Atualiza Data de Agendamento
        if (ordemServicoRequestDTO.getDataAgendamento() != null) {
            existingOrdemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        }

        // Atualiza Prioridade
        if (ordemServicoRequestDTO.getPrioridade() != null) {
            existingOrdemServico.setPrioridade(ordemServicoRequestDTO.getPrioridade());
        }

        // Atualiza Análise de Falha
        if (ordemServicoRequestDTO.getAnaliseFalha() != null) {
            existingOrdemServico.setAnaliseFalha(ordemServicoRequestDTO.getAnaliseFalha());
        }

        // Atualiza Solução Aplicada
        if (ordemServicoRequestDTO.getSolucaoAplicada() != null) {
            existingOrdemServico.setSolucaoAplicada(ordemServicoRequestDTO.getSolucaoAplicada());
        }

        // Lógica de Status e dataFechamento
        if (ordemServicoRequestDTO.getStatus() != null && existingOrdemServico.getStatus() != ordemServicoRequestDTO.getStatus()) {
            existingOrdemServico.setStatus(ordemServicoRequestDTO.getStatus());
            if (ordemServicoRequestDTO.getStatus() == StatusOS.CONCLUIDA || ordemServicoRequestDTO.getStatus() == StatusOS.ENCERRADA) {
                // Define dataFechamento apenas se estiver mudando para um status de finalização e ainda não tiver sido definida
                if (existingOrdemServico.getDataFechamento() == null) {
                    existingOrdemServico.setDataFechamento(LocalDateTime.now());
                }
            } else {
                // Se o status for alterado para um estado não finalizado, limpa a dataFechamento
                existingOrdemServico.setDataFechamento(null);
            }
        }

        // **3. Salvar e Retornar:**
        OrdemServico updatedOrdemServico = ordemServicoRepository.save(existingOrdemServico);
        return convertToDTO(updatedOrdemServico);
    }

    public void deleteOrdemServico(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id);
        }
        // TODO: Verifique se a remoção em cascata (CascadeType.ALL, orphanRemoval = true) está funcionando corretamente
        // para os relacionamentos OneToMany (RegistroTempo, ItemOSUtilizado, etc.).
        // Se não estiver, você precisará remover as entidades filhas manualmente antes de deletar a OS.
        ordemServicoRepository.deleteById(id);
    }

    // Método para gerar um NOVO número de OS único para a CRIAÇÃO.
    // Ele usa o `getNextOsNumber` para obter o próximo número sequencial.
    private String generateNewNumeroOS() {
        return "OS-" + getNextOsNumber();
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
        dto.setPrioridade(ordemServico.getPrioridade());
        // `dataHoraEmissao` geralmente é gerado no momento da emissão de um documento (ex: PDF),
        // não é um campo persistente na entidade OS. Por isso, removi o mapeamento direto.
        // Se for um campo na entidade, precisa ser definido e mapeado.

        if (ordemServico.getCliente() != null) {
            dto.setClienteId(ordemServico.getCliente().getId());
            dto.setNomeCliente(ordemServico.getCliente().getNomeCompleto());
        }
        if (ordemServico.getEquipamento() != null) {
            dto.setEquipamentoId(ordemServico.getEquipamento().getId());
            // Concatenando Marca/Modelo e Número de Série/Chassi para a descrição do equipamento
            dto.setDescricaoEquipamento(ordemServico.getEquipamento().getMarcaModelo() + " - " + ordemServico.getEquipamento().getNumeroSerieChassi());
        }
        if (ordemServico.getTecnicoAtribuido() != null) {
            dto.setTecnicoAtribuidoId(ordemServico.getTecnicoAtribuido().getId());
            dto.setNomeTecnicoAtribuido(ordemServico.getTecnicoAtribuido().getNome()); // Assumindo `getNome()` para o nome do usuário
        }

        dto.setProblemaRelatado(ordemServico.getProblemaRelatado());
        dto.setAnaliseFalha(ordemServico.getAnaliseFalha());
        dto.setSolucaoAplicada(ordemServico.getSolucaoAplicada());

        return dto;
    }

    // TODO: Remover ou ajustar se `convertToEntity` não for mais usado para criação
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
        OrdemServico lastOs = ordemServicoRepository.findTopByOrderByIdDesc();

        long nextNumber = 1; // Número inicial se não houver OS anterior
        if (lastOs != null && lastOs.getNumeroOS() != null) {
            try {
                // Remove prefixos como "OS-" e tenta extrair a parte numérica
                String lastNumStr = lastOs.getNumeroOS().replaceAll("OS-", "").replaceAll("[^0-9]", "");
                if (!lastNumStr.isEmpty()) {
                    long lastNum = Long.parseLong(lastNumStr);
                    nextNumber = lastNum + 1;
                }
            } catch (NumberFormatException e) {
                System.err.println("WARN: Não foi possível parsear o último numeroOS: " + lastOs.getNumeroOS() + ". Gerando novo número a partir de 1.");
            }
        }
        // Retorna o número como String. O prefixo "OS-" será adicionado em `generateNewNumeroOS`.
        return String.valueOf(nextNumber);
    }
}