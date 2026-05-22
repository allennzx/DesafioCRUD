package com.crud.repository;

import com.crud.config.HibernateConfig;
import com.crud.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável por todas as operações de banco de dados
 * relacionadas à entidade Pedido.
 */
public class PedidoRepository {

    // ─── CREATE ────────────────────────────────────────────────────────────────

    public Pedido salvar(Pedido pedido) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
            return pedido;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public Optional<Pedido> buscarPorId(Long id) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            Pedido pedido = em.find(Pedido.class, id);
            // Força carregamento do cliente associado
            if (pedido != null) pedido.getCliente().getNome();
            return Optional.ofNullable(pedido);
        } finally {
            em.close();
        }
    }

    /** Lista todos os pedidos, ordenados por data de criação (mais recentes primeiro). */
    public List<Pedido> listarTodos() {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            List<Pedido> pedidos = em.createQuery(
                "SELECT p FROM Pedido p JOIN FETCH p.cliente ORDER BY p.dataCriacao DESC",
                Pedido.class
            ).getResultList();
            return pedidos;
        } finally {
            em.close();
        }
    }

    /** Lista apenas os pedidos de um cliente específico. */
    public List<Pedido> listarPorCliente(Long clienteId) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                "SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.cliente.id = :cid " +
                "ORDER BY p.dataCriacao DESC",
                Pedido.class
            );
            query.setParameter("cid", clienteId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public Pedido atualizar(Pedido pedido) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Pedido atualizado = em.merge(pedido);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    public boolean deletar(Long id) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(pedido);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
