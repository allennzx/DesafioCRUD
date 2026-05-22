package com.crud.service;

import com.crud.model.Cliente;
import com.crud.model.Pedido;
import com.crud.model.Pedido.Status;
import com.crud.repository.ClienteRepository;
import com.crud.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço para Pedido.
 * Aplica validações e regras de negócio antes de persistir os dados.
 */
public class PedidoService {

    private final PedidoRepository  pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService() {
        this.pedidoRepository  = new PedidoRepository();
        this.clienteRepository = new ClienteRepository();
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Cria um novo pedido vinculado a um cliente existente.
     *
     * @param clienteId  ID do cliente dono do pedido
     * @param descricao  Descrição do pedido
     * @param valor      Valor em reais (deve ser > 0)
     */
    public Pedido criar(Long clienteId, String descricao, BigDecimal valor) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente #" + clienteId + " não encontrado."));

        validarDescricao(descricao);
        validarValor(valor);

        Pedido pedido = new Pedido(descricao.trim(), valor, cliente);
        return pedidoRepository.salvar(pedido);
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.listarTodos();
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        // Verifica se o cliente existe antes de listar
        clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente #" + clienteId + " não encontrado."));
        return pedidoRepository.listarPorCliente(clienteId);
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Atualiza descrição, valor e/ou status de um pedido.
     * Campos nulos mantêm o valor original.
     */
    public Pedido atualizar(Long id, String novaDescricao, BigDecimal novoValor, Status novoStatus) {
        Pedido pedido = pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido #" + id + " não encontrado."));

        // Regra: não permite editar pedidos cancelados
        if (pedido.getStatus() == Status.CANCELADO) {
            throw new RuntimeException("Pedidos CANCELADOS não podem ser editados.");
        }

        if (novaDescricao != null && !novaDescricao.isBlank()) {
            validarDescricao(novaDescricao);
            pedido.setDescricao(novaDescricao.trim());
        }
        if (novoValor != null) {
            validarValor(novoValor);
            pedido.setValor(novoValor);
        }
        if (novoStatus != null) {
            pedido.setStatus(novoStatus);
        }

        return pedidoRepository.atualizar(pedido);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    public boolean deletar(Long id) {
        if (!pedidoRepository.buscarPorId(id).isPresent()) {
            throw new RuntimeException("Pedido #" + id + " não encontrado.");
        }
        return pedidoRepository.deletar(id);
    }

    // ─── Validações ────────────────────────────────────────────────────────────

    private void validarDescricao(String desc) {
        if (desc == null || desc.isBlank())
            throw new IllegalArgumentException("Descrição não pode ser vazia.");
        if (desc.trim().length() < 3)
            throw new IllegalArgumentException("Descrição deve ter pelo menos 3 caracteres.");
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null)
            throw new IllegalArgumentException("Valor não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
    }
}
