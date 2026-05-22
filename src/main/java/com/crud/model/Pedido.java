package com.crud.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade Pedido — representa a tabela `pedidos` no banco de dados.
 * Relacionamento: muitos Pedidos pertencem a um Cliente (N:1).
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    /** Status possíveis de um pedido */
    public enum Status {
        PENDENTE, EM_ANDAMENTO, CONCLUIDO, CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Chave estrangeira: cada pedido pertence a um Cliente.
     * fetch=LAZY → cliente carregado somente quando acessado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ─── Construtores ──────────────────────────────────────────────────────────

    public Pedido() {}

    public Pedido(String descricao, BigDecimal valor, Cliente cliente) {
        this.descricao   = descricao;
        this.valor       = valor;
        this.cliente     = cliente;
        this.status      = Status.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    // ─── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId()                        { return id; }
    public String getDescricao()               { return descricao; }
    public void setDescricao(String d)         { this.descricao = d; }
    public BigDecimal getValor()               { return valor; }
    public void setValor(BigDecimal v)         { this.valor = v; }
    public Status getStatus()                  { return status; }
    public void setStatus(Status s)            { this.status = s; }
    public LocalDateTime getDataCriacao()      { return dataCriacao; }
    public void setDataCriacao(LocalDateTime d){ this.dataCriacao = d; }
    public Cliente getCliente()                { return cliente; }
    public void setCliente(Cliente c)          { this.cliente = c; }

    // ─── toString para exibição no menu ────────────────────────────────────────

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
            "┌─ Pedido #%d ───────────────────────────────\n" +
            "│  Descrição: %s\n" +
            "│  Valor    : R$ %.2f\n" +
            "│  Status   : %s\n" +
            "│  Criado em: %s\n" +
            "│  Cliente  : %s (ID: %d)\n" +
            "└────────────────────────────────────────────",
            id, descricao, valor, status,
            dataCriacao.format(fmt),
            cliente.getNome(), cliente.getId()
        );
    }
}
