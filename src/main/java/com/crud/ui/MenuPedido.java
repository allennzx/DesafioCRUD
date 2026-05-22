package com.crud.ui;

import com.crud.model.Pedido;
import com.crud.model.Pedido.Status;
import com.crud.service.PedidoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuPedido {

    private final PedidoService pedidoService;
    private final Scanner       scanner;

    public MenuPedido(Scanner scanner) {
        this.pedidoService = new PedidoService();
        this.scanner       = scanner;
    }

    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║        MENU — PEDIDOS            ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Criar novo pedido            ║");
            System.out.println("║  2. Listar todos os pedidos      ║");
            System.out.println("║  3. Listar pedidos por cliente   ║");
            System.out.println("║  4. Buscar pedido por ID         ║");
            System.out.println("║  5. Atualizar pedido             ║");
            System.out.println("║  6. Deletar pedido               ║");
            System.out.println("║  0. Voltar ao menu principal     ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("  Opção: ");

            int opcao = lerInteiro();
            switch (opcao) {
                case 1 -> criar();
                case 2 -> listarTodos();
                case 3 -> listarPorCliente();
                case 4 -> buscarPorId();
                case 5 -> atualizar();
                case 6 -> deletar();
                case 0 -> continuar = false;
                default -> System.out.println("   Opção inválida.");
            }
        }
    }


    private void criar() {
        System.out.println("\n── Criar Novo Pedido ───────────────────────");

        System.out.print("  ID do cliente: ");
        Long clienteId = lerLong();
        if (clienteId == null) return;

        System.out.print("  Descrição    : ");
        String descricao = lerTexto();

        System.out.print("  Valor (R$)   : ");
        BigDecimal valor = lerDecimal();
        if (valor == null) return;

        try {
            Pedido pedido = pedidoService.criar(clienteId, descricao, valor);
            System.out.println("\n   Pedido criado com sucesso!");
            System.out.println(pedido);
        } catch (Exception e) {
            System.out.println("\n   Erro: " + e.getMessage());
        }
    }


    private void listarTodos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        if (pedidos.isEmpty()) {
            System.out.println("\n  ℹ Nenhum pedido cadastrado.");
            return;
        }
        System.out.println("\n── Todos os Pedidos (" + pedidos.size() + ") ───────────────────");
        pedidos.forEach(p -> System.out.println("\n" + p));
    }

    private void listarPorCliente() {
        System.out.print("\n  ID do cliente: ");
        Long clienteId = lerLong();
        if (clienteId == null) return;

        try {
            List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
            if (pedidos.isEmpty()) {
                System.out.println("  ℹ Este cliente não possui pedidos.");
                return;
            }
            System.out.println("\n── Pedidos do Cliente #" + clienteId + " (" + pedidos.size() + ") ────");
            pedidos.forEach(p -> System.out.println("\n" + p));
        } catch (Exception e) {
            System.out.println("   Erro: " + e.getMessage());
        }
    }


    private void buscarPorId() {
        System.out.print("\n  ID do pedido: ");
        Long id = lerLong();
        if (id == null) return;

        Optional<Pedido> resultado = pedidoService.buscarPorId(id);
        if (resultado.isPresent()) {
            System.out.println("\n" + resultado.get());
        } else {
            System.out.println("   Pedido #" + id + " não encontrado.");
        }
    }


    private void atualizar() {
        System.out.print("\n  ID do pedido a atualizar: ");
        Long id = lerLong();
        if (id == null) return;

        System.out.println("  (Enter para manter o valor atual)");

        System.out.print("  Nova descrição : ");
        String descricao = scanner.nextLine().trim();

        System.out.print("  Novo valor (R$): ");
        String valorStr = scanner.nextLine().trim();
        BigDecimal valor = null;
        if (!valorStr.isEmpty()) {
            try {
                valor = new BigDecimal(valorStr.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("   Valor inválido.");
                return;
            }
        }

        System.out.println("  Novo status:");
        System.out.println("    1. PENDENTE  |  2. EM_ANDAMENTO  |  3. CONCLUIDO  |  4. CANCELADO  |  0. Manter");
        System.out.print("  Opção: ");
        int statusOpcao = lerInteiro();
        Status status = switch (statusOpcao) {
            case 1 -> Status.PENDENTE;
            case 2 -> Status.EM_ANDAMENTO;
            case 3 -> Status.CONCLUIDO;
            case 4 -> Status.CANCELADO;
            default -> null;
        };

        try {
            Pedido atualizado = pedidoService.atualizar(
                    id,
                    descricao.isEmpty() ? null : descricao,
                    valor,
                    status
            );
            System.out.println("\n   Pedido atualizado!");
            System.out.println(atualizado);
        } catch (Exception e) {
            System.out.println("\n   Erro: " + e.getMessage());
        }
    }


    private void deletar() {
        System.out.print("\n  ID do pedido a deletar: ");
        Long id = lerLong();
        if (id == null) return;

        System.out.print("  ⚠ Confirma exclusão? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("  Operação cancelada.");
            return;
        }

        try {
            pedidoService.deletar(id);
            System.out.println("   Pedido #" + id + " removido com sucesso.");
        } catch (Exception e) {
            System.out.println("   Erro: " + e.getMessage());
        }
    }


    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Long lerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("   ID inválido.");
            return null;
        }
    }

    private BigDecimal lerDecimal() {
        try {
            return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("   Valor inválido. Use ponto ou vírgula como separador decimal.");
            return null;
        }
    }

    private String lerTexto() {
        return scanner.nextLine().trim();
    }
}
