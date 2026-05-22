package com.crud.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração central do Hibernate/JPA.
 * Carrega variáveis do arquivo .env e cria a EntityManagerFactory (singleton).
 */
public class HibernateConfig {

    private static EntityManagerFactory emf;

    // Bloco estático: executa uma única vez ao carregar a classe
    static {
        try {
            // Carrega o arquivo .env da raiz do projeto
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing() // não lança erro se .env não existir (usa variáveis de ambiente do SO)
                    .load();

            String dbUrl      = dotenv.get("DB_URL",      System.getenv("DB_URL"));
            String dbUser     = dotenv.get("DB_USER",     System.getenv("DB_USER"));
            String dbPassword = dotenv.get("DB_PASSWORD", System.getenv("DB_PASSWORD"));

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new RuntimeException(
                    "Variáveis de ambiente DB_URL, DB_USER ou DB_PASSWORD não encontradas.\n" +
                    "Crie o arquivo .env na raiz do projeto (veja .env.example)."
                );
            }

            // Passa as propriedades diretamente para o JPA (sobrescreve o persistence.xml)
            Map<String, String> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url",      dbUrl);
            props.put("jakarta.persistence.jdbc.user",     dbUser);
            props.put("jakarta.persistence.jdbc.password", dbPassword);

            emf = Persistence.createEntityManagerFactory("crudPU", props);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(
                "Falha ao inicializar conexão com banco de dados: " + e.getMessage()
            );
        }
    }

    /** Retorna a fábrica de EntityManagers (singleton). */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /** Cria e retorna um novo EntityManager. Lembre-se de fechá-lo após o uso. */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /** Fecha a fábrica ao encerrar a aplicação. */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
