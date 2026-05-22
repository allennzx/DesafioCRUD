package com.crud.ui;

import com.crud.model.Cliente;
import com.crud.service.ClienteService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuCliente {

    private final ClienteService clienteService;
    private final Scanner        scanner;

    public MenuCliente(Scanner scanner) {
        this.clienteService = new ClienteService();
        this.scanner        = scanner;
    }

    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║       MENU — CLIENTES            ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Cadastrar novo cliente       ║");
            System.out.println("║  2. Listar todos os clientes     ║");
            System.out.println("║  3. Buscar cliente por ID        ║");
            System.out.println("║  4. Atualizar cliente            ║");
            System.out.println("║  5. Deletar cliente              ║");
            System.out.println("║  0. Voltar ao menu principal     ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("  Opção: ");

            int opcao = lerInteiro();
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listarTodos();
                case 3 -> buscarPorId();
                case 4 -> atualizar();
                case 5 -> deletar();
                case 0 -> continuar = false;
                default -> System.out.println("  Opção inválida.");
            }
        }
    }


    private void cadastrar() {
        System.out.println("\n── Cadastrar Novo Cliente ──────────────────");

        System.out.print("  Nome     : ");
        String nome = lerTexto();

        System.out.print("  E-mail   : ");
        String email = lerTexto();

        System.out.print("  CPF      : ");
        String cpf = lerTexto();

        System.out.print("  CEP (opcional, Enter para pular): ");
        String cep = scanner.nextLine().trim();

        try {
            Cliente cliente = clienteService.cadastrar(nome, email, cpf, cep.isEmpty() ? null : cep);
            System.out.println("\n   Cliente cadastrado com sucesso!");
            System.out.println(cliente);
        } catch (Exception e) {
            System.out.println("\n   Erro: " + e.getMessage());
        }
    }

    // ─── LISTAR ────────────────────────────────────────────────────────────────

    private void listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        if (clientes.isEmpty()) {
            System.out.println("\n  ℹ Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("\n── Clientes Cadastrados (" + clientes.size() + ") ─────────────");
        clientes.forEach(c -> System.out.println("\n" + c));
    }


    private void buscarPorId() {
        System.out.print("\n  ID do cliente: ");
        Long id = lerLong();
        if (id == null) return;

        Optional<Cliente> resultado = clienteService.buscarPorId(id);
        if (resultado.isPresent()) {
            System.out.println("\n" + resultado.get());
        } else {
            System.out.println("  ❌ Cliente #" + id + " não encontrado.");
        }
    }


    private void atualizar() {
        System.out.print("\n  ID do cliente a atualizar: ");
        Long id = lerLong();
        if (id == null) return;

        System.out.println("  (Enter para manter o valor atual)");

        System.out.print("  Novo nome  : ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Novo e-mail: ");
        String email = scanner.nextLine().trim();

        System.out.print("  Novo CEP   : ");
        String cep = scanner.nextLine().trim();

        try {
            Cliente atualizado = clienteService.atualizar(
                    id,
                    nome.isEmpty()  ? null : nome,
                    email.isEmpty() ? null : email,
                    cep.isEmpty()   ? null : cep
            );
            System.out.println("\n   Cliente atualizado com sucesso!");
            System.out.println(atualizado);
        } catch (Exception e) {
            System.out.println("\n   Erro: " + e.getMessage());
        }
    }


    private void deletar() {
        System.out.print("\n  ID do cliente a deletar: ");
        Long id = lerLong();
        if (id == null) return;

        System.out.print("  ⚠ Isso removerá o cliente e TODOS os seus pedidos. Confirma? (s/n): ");
        String confirmacao = scanner.nextLine().trim();

        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("  Operação cancelada.");
            return;
        }

        try {
            clienteService.deletar(id);
            System.out.println("   Cliente #" + id + " removido com sucesso.");
        } catch (Exception e) {
            System.out.println("   Erro: " + e.getMessage());
        }
    }


    private int lerInteiro() {
        try {
            int valor = Integer.parseInt(scanner.nextLine().trim());
            return valor;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Long lerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ❌ ID inválido. Informe apenas números.");
            return null;
        }
    }

    private String lerTexto() {
        return scanner.nextLine().trim();
    }
}
