package com.crud.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Cliente — representa a tabela `clientes` no banco de dados.
 * Relacionamento: um Cliente pode ter vários Pedidos (1:N).
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(length = 9)
    private String cep;

    @Column(length = 200)
    private String logradouro;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    /**
     * Relacionamento bidirecional: um Cliente tem muitos Pedidos.
     * cascade=ALL  → operações no Cliente propagam para os Pedidos
     * orphanRemoval → remove pedido do banco ao removê-lo da lista
     * fetch=LAZY   → pedidos carregados somente quando acessados
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    // ─── Construtores ──────────────────────────────────────────────────────────

    public Cliente() {}

    public Cliente(String nome, String email, String cpf) {
        this.nome  = nome;
        this.email = email;
        this.cpf   = cpf;
    }

    // ─── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId()                  { return id; }
    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }
    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }
    public String getCpf()               { return cpf; }
    public void setCpf(String cpf)       { this.cpf = cpf; }
    public String getCep()               { return cep; }
    public void setCep(String cep)       { this.cep = cep; }
    public String getLogradouro()        { return logradouro; }
    public void setLogradouro(String l)  { this.logradouro = l; }
    public String getBairro()            { return bairro; }
    public void setBairro(String b)      { this.bairro = b; }
    public String getCidade()            { return cidade; }
    public void setCidade(String c)      { this.cidade = c; }
    public String getUf()               { return uf; }
    public void setUf(String uf)        { this.uf = uf; }
    public List<Pedido> getPedidos()    { return pedidos; }

    // ─── toString para exibição no menu ────────────────────────────────────────

    @Override
    public String toString() {
        String endereco = (logradouro != null && !logradouro.isBlank())
                ? logradouro + ", " + bairro + " - " + cidade + "/" + uf + " (CEP: " + cep + ")"
                : "Endereço não informado";
        return String.format(
            "┌─ Cliente #%d ──────────────────────────────\n" +
            "│  Nome    : %s\n" +
            "│  E-mail  : %s\n" +
            "│  CPF     : %s\n" +
            "│  Endereço: %s\n" +
            "│  Pedidos : %d\n" +
            "└────────────────────────────────────────────",
            id, nome, email, cpf, endereco, pedidos.size()
        );
    }
}
