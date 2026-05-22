package com.crud;

import com.crud.config.HibernateConfig;
import com.crud.ui.MenuCliente;
import com.crud.ui.MenuPedido;

import java.util.Scanner;

/**
 * Ponto de entrada da aplicação.
 * Exibe o menu principal e delega para os submenus de Cliente e Pedido.
 */
public class Main {

    public static void main(String[] args) {

        // Garante encerramento limpo da conexão ao sair (Ctrl+C ou erro)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HibernateConfig.close();
            System.out.println("\n  Conexão com banco encerrada. Até logo!");
        }));

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Sistema de Clientes e Pedidos  v1.0   ║");
        System.out.println("║   Java + Hibernate + MySQL + ViaCEP     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Inicializa conexão com o banco (cria tabelas se não existirem)
        try {
            System.out.print("\n  Conectando ao banco de dados... ");
            HibernateConfig.getEntityManagerFactory(); // força inicialização
            System.out.println("OK ✔");
        } catch (ExceptionInInitializerError | RuntimeException e) {
            System.out.println("FALHOU ✘");
            System.err.println("  " + e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        MenuCliente menuCliente = new MenuCliente(scanner);
        MenuPedido  menuPedido  = new MenuPedido(scanner);

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║        MENU PRINCIPAL            ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Gerenciar Clientes           ║");
            System.out.println("║  2. Gerenciar Pedidos            ║");
            System.out.println("║  0. Sair                         ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("  Opção: ");

            String entrada = scanner.nextLine().trim();
            switch (entrada) {
                case "1" -> menuCliente.exibir();
                case "2" -> menuPedido.exibir();
                case "0" -> rodando = false;
                default  -> System.out.println("  ❌ Opção inválida.");
            }
        }

        scanner.close();
        System.exit(0); // aciona o shutdown hook
    }
}
