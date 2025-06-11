package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.AssinaturaOSResponseDTO;
import com.codagis.nordeste_servicos.dto.FotoOSResponseDTO;
import com.codagis.nordeste_servicos.dto.ItemOSUtilizadoResponseDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoResponseDTO;
import com.codagis.nordeste_servicos.dto.RegistroDeslocamentoResponseDTO;
import com.codagis.nordeste_servicos.dto.RegistroTempoResponseDTO;
import com.codagis.nordeste_servicos.dto.UsuarioResponseDTO; // Importe o UsuarioResponseDTO
import com.codagis.nordeste_servicos.dto.TecnicoDTO; // <<< Importar o TecnicoDTO


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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> findAllOrdensServico() {
        List<OrdemServico> ordens = ordemServicoRepository.findAll();
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

    @Transactional
    public OrdemServicoResponseDTO createOrdemServico(OrdemServicoRequestDTO ordemServicoRequestDTO) {
        Cliente cliente = clienteRepository.findById(ordemServicoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + ordemServicoRequestDTO.getClienteId()));

        Equipamento equipamento = equipamentoRepository.findById(ordemServicoRequestDTO.getEquipamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + ordemServicoRequestDTO.getEquipamentoId()));

        Usuario tecnicoAtribuido = null;
        // >>> AJUSTE AQUI: Verificar se tecnicoAtribuido (objeto) não é nulo e se o ID dentro dele não é nulo
        if (ordemServicoRequestDTO.getTecnicoAtribuido() != null && ordemServicoRequestDTO.getTecnicoAtribuido().getId() != null) {
            tecnicoAtribuido = usuarioRepository.findById(ordemServicoRequestDTO.getTecnicoAtribuido().getId()) // <<< Acesso ao ID
                    .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado com ID: " + ordemServicoRequestDTO.getTecnicoAtribuido().getId())); // <<< Acesso ao ID
            // TODO: Adicionar validação para garantir que o usuário atribuído seja de fato um TÉCNICO (ex: verificando um campo de perfil no Usuario)
        }

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

        // >>> AJUSTE AQUI: Lógica para update do Tecnico Atribuído
        // Se o objeto tecnicoAtribuido for fornecido no DTO
        if (ordemServicoRequestDTO.getTecnicoAtribuido() != null) {
            Long novoTecnicoId = ordemServicoRequestDTO.getTecnicoAtribuido().getId();
            // Se o ID do novo técnico for nulo, significa que querem desatribuir o técnico
            if (novoTecnicoId == null) {
                existingOrdemServico.setTecnicoAtribuido(null);
            } else {
                // Se o ID for diferente do técnico atual, ou se não houver técnico atual, busca e atribui
                if (existingOrdemServico.getTecnicoAtribuido() == null || !novoTecnicoId.equals(existingOrdemServico.getTecnicoAtribuido().getId())) {
                    Usuario tecnico = usuarioRepository.findById(novoTecnicoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Técnico atribuído não encontrado com ID: " + novoTecnicoId));
                    existingOrdemServico.setTecnicoAtribuido(tecnico);
                }
            }
        } else {
            // Se o objeto tecnicoAtribuido não for fornecido no DTO (é null), mantém o técnico atual ou define como null se não houver
            // Você pode decidir manter o técnico existente se o objeto não for fornecido, ou definir como null.
            // A linha abaixo define como null se o objeto tecnicoAtribuidoRequestDTO for null.
            // Se a intenção é só alterar se explicitamente passado, a lógica acima é suficiente.
            // Para ser explícito, se o objeto tecnicoAtribuido for null, desatribui.
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
                if (existingOrdemServico.getDataFechamento() == null) {
                    existingOrdemServico.setDataFechamento(LocalDateTime.now());
                }
            } else {
                existingOrdemServico.setDataFechamento(null);
            }
        }

        // **3. Salvar e Retornar:**
        OrdemServico updatedOrdemServico = ordemServicoRepository.save(existingOrdemServico);
        return convertToDTO(updatedOrdemServico);
    }

    @Transactional
    public void deleteOrdemServico(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + id);
        }
        ordemServicoRepository.deleteById(id);
    }

    private String generateNewNumeroOS() {
        return "OS-" + getNextOsNumber();
    }

    // Método utilitário para converter Entidade para DTO
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

        if (ordemServico.getCliente() != null) {
            dto.setClienteId(ordemServico.getCliente().getId());
            dto.setNomeCliente(ordemServico.getCliente().getNomeCompleto());
        }
        if (ordemServico.getEquipamento() != null) {
            dto.setEquipamentoId(ordemServico.getEquipamento().getId());
            dto.setDescricaoEquipamento(ordemServico.getEquipamento().getMarcaModelo() + " - " + ordemServico.getEquipamento().getNumeroSerieChassi());
        }

        // --- POPULANDO O CAMPO TECNICO ATRIBUÍDO COM USUARIORESPONSEDTO ---
        if (ordemServico.getTecnicoAtribuido() != null) {
            Usuario tecnicoEntity = ordemServico.getTecnicoAtribuido();
            dto.setTecnicoAtribuido(new UsuarioResponseDTO(
                    tecnicoEntity.getId(),
                    tecnicoEntity.getNome(),
                    tecnicoEntity.getCracha(),
                    tecnicoEntity.getEmail(),
                    tecnicoEntity.getPerfil()
            ));
        } else {
            dto.setTecnicoAtribuido(null); // Define como nulo se não houver técnico atribuído
        }

        dto.setProblemaRelatado(ordemServico.getProblemaRelatado());
        dto.setAnaliseFalha(ordemServico.getAnaliseFalha());
        dto.setSolucaoAplicada(ordemServico.getSolucaoAplicada());

        // --- Popular os DTOs aninhados com seus ResponseDTOs (mantido dos ajustes anteriores) ---

        // Popular Registros de Tempo
        if (ordemServico.getRegistrosTempo() != null && !ordemServico.getRegistrosTempo().isEmpty()) {
            dto.setRegistrosTempo(ordemServico.getRegistrosTempo().stream()
                    .map(rt -> new RegistroTempoResponseDTO(
                            rt.getId(),
                            ordemServico.getId(),
                            rt.getTecnico() != null ? rt.getTecnico().getId() : null,
                            rt.getTecnico() != null ? rt.getTecnico().getNome() : null, // Assumindo rt.getTecnico() é Usuario
                            rt.getTipoServico() != null ? rt.getTipoServico().getId() : null,
                            rt.getTipoServico() != null ? rt.getTipoServico().getDescricao() : null,
                            rt.getHoraInicio(),
                            rt.getHoraTermino(),
                            rt.getHorasTrabalhadas()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setRegistrosTempo(Collections.emptyList());
        }

        // Popular Registros de Deslocamento
        if (ordemServico.getRegistrosDeslocamento() != null && !ordemServico.getRegistrosDeslocamento().isEmpty()) {
            dto.setRegistrosDeslocamento(ordemServico.getRegistrosDeslocamento().stream()
                    .map(rd -> new RegistroDeslocamentoResponseDTO(
                            rd.getId(),
                            ordemServico.getId(),
                            rd.getTecnico() != null ? rd.getTecnico().getId() : null,
                            rd.getTecnico() != null ? rd.getTecnico().getNome() : null, // Assumindo rd.getTecnico() é Usuario
                            rd.getData(),
                            rd.getPlacaVeiculo(),
                            rd.getKmInicial(),
                            rd.getKmFinal(),
                            rd.getTotalKm(),
                            rd.getSaidaDe(),
                            rd.getChegadaEm()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setRegistrosDeslocamento(Collections.emptyList());
        }

        // Popular Itens Utilizados
        if (ordemServico.getItensUtilizados() != null && !ordemServico.getItensUtilizados().isEmpty()) {
            dto.setItensUtilizados(ordemServico.getItensUtilizados().stream()
                    .map(iu -> new ItemOSUtilizadoResponseDTO(
                            iu.getId(),
                            ordemServico.getId(),
                            iu.getPecaMaterial() != null ? iu.getPecaMaterial().getId() : null,
                            iu.getPecaMaterial() != null ? iu.getPecaMaterial().getCodigo() : null,
                            iu.getPecaMaterial() != null ? iu.getPecaMaterial().getDescricao() : null,
                            iu.getPecaMaterial() != null ? iu.getPecaMaterial().getPreco() : null,
                            iu.getQuantidadeRequisitada(),
                            iu.getQuantidadeUtilizada(),
                            iu.getQuantidadeDevolvida()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setItensUtilizados(Collections.emptyList());
        }

        // Popular Fotos
        if (ordemServico.getFotos() != null && !ordemServico.getFotos().isEmpty()) {
            dto.setFotos(ordemServico.getFotos().stream()
                    .map(foto -> new FotoOSResponseDTO(
                            foto.getId(),
                            ordemServico.getId(),
                            foto.getCaminhoArquivo(),
                            foto.getNomeArquivoOriginal(),
                            foto.getTipoConteudo(),
                            foto.getTamanhoArquivo(),
                            foto.getDataUpload()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setFotos(Collections.emptyList());
        }

        // Popular Assinatura
        if (ordemServico.getAssinatura() != null) {
            AssinaturaOS assinaturaEntity = ordemServico.getAssinatura();
            dto.setAssinatura(new AssinaturaOSResponseDTO(
                    assinaturaEntity.getId(),
                    ordemServico.getId(),
                    assinaturaEntity.getCaminhoArquivo(),
                    assinaturaEntity.getTipoConteudo(),
                    assinaturaEntity.getTamanhoArquivo(),
                    assinaturaEntity.getDataHoraColeta()
            ));
        } else {
            dto.setAssinatura(null);
        }

        return dto;
    }

    // TODO: Remover ou ajustar se `convertToEntity` não for mais usado para criação
    private OrdemServico convertToEntity(OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setProblemaRelatado(ordemServicoRequestDTO.getProblemaRelatado());
        ordemServico.setDataAgendamento(ordemServicoRequestDTO.getDataAgendamento());
        return ordemServico;
    }

    public String getNextOsNumber() {
        OrdemServico lastOs = ordemServicoRepository.findTopByOrderByIdDesc();

        long nextNumber = 1;
        if (lastOs != null && lastOs.getNumeroOS() != null) {
            try {
                String lastNumStr = lastOs.getNumeroOS().replaceAll("OS-", "").replaceAll("[^0-9]", "");
                if (!lastNumStr.isEmpty()) {
                    long lastNum = Long.parseLong(lastNumStr);
                    nextNumber = lastNum + 1;
                }
            } catch (NumberFormatException e) {
                System.err.println("WARN: Não foi possível parsear o último numeroOS: " + lastOs.getNumeroOS() + ". Gerando novo número a partir de 1.");
            }
        }
        return String.valueOf(nextNumber);
    }
}