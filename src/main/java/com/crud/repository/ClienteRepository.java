package com.crud.repository;

import com.crud.config.HibernateConfig;
import com.crud.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável por todas as operações de banco de dados
 * relacionadas à entidade Cliente.
 */
public class ClienteRepository {

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Persiste um novo cliente no banco de dados.
     * @param cliente objeto Cliente a ser salvo
     * @return o cliente salvo (com ID gerado)
     */
    public Cliente salvar(Cliente cliente) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
            return cliente;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    /**
     * Busca um cliente pelo ID.
     * @return Optional com o cliente, ou empty() se não encontrado
     */
    public Optional<Cliente> buscarPorId(Long id) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            Cliente cliente = em.find(Cliente.class, id);
            // Força o carregamento dos pedidos enquanto a sessão está aberta
            if (cliente != null) cliente.getPedidos().size();
            return Optional.ofNullable(cliente);
        } finally {
            em.close();
        }
    }

    /**
     * Busca um cliente pelo e-mail (campo único).
     */
    public Optional<Cliente> buscarPorEmail(String email) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.email = :email", Cliente.class
            );
            query.setParameter("email", email);
            List<Cliente> result = query.getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    /**
     * Retorna todos os clientes cadastrados.
     */
    public List<Cliente> listarTodos() {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            List<Cliente> clientes = em.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.nome", Cliente.class
            ).getResultList();
            // Força carregamento dos pedidos
            clientes.forEach(c -> c.getPedidos().size());
            return clientes;
        } finally {
            em.close();
        }
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um cliente já existente.
     * @param cliente objeto Cliente com os dados atualizados (deve ter ID)
     * @return o cliente atualizado
     */
    public Cliente atualizar(Cliente cliente) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente atualizado = em.merge(cliente); // merge = update se já existe
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Remove um cliente pelo ID.
     * Graças ao cascade=ALL, todos os pedidos do cliente também são removidos.
     * @return true se removido, false se não encontrado
     */
    public boolean deletar(Long id) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(cliente);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Verifica se já existe um cliente cadastrado com o e-mail informado.
     */
    public boolean emailJaCadastrado(String email) {
        return buscarPorEmail(email).isPresent();
    }

    /**
     * Verifica se já existe um cliente cadastrado com o CPF informado.
     */
    public boolean cpfJaCadastrado(String cpf) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE c.cpf = :cpf", Long.class
            ).setParameter("cpf", cpf).getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
