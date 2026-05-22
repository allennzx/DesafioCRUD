package com.crud.service;

import com.crud.model.Cliente;
import com.crud.repository.ClienteRepository;
import com.crud.service.ViaCepService.EnderecoDTO;
import com.crud.service.ViaCepService.ViaCepException;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço para Cliente.
 * Contém a lógica de negócio: validações, regras, e orquestração
 * entre repositório e serviços externos (ViaCEP).
 */
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViaCepService     viaCepService;

    public ClienteService() {
        this.clienteRepository = new ClienteRepository();
        this.viaCepService     = new ViaCepService();
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Cadastra um novo cliente.
     * Se um CEP for informado, consulta a API ViaCEP para preencher o endereço.
     *
     * @throws IllegalArgumentException se dados obrigatórios forem inválidos
     * @throws RuntimeException         se e-mail ou CPF já estiverem cadastrados
     */
    public Cliente cadastrar(String nome, String email, String cpf, String cep) {
        // Validações básicas
        validarNome(nome);
        validarEmail(email);
        validarCpf(cpf);

        // Unicidade
        if (clienteRepository.emailJaCadastrado(email)) {
            throw new RuntimeException("E-mail já cadastrado: " + email);
        }
        if (clienteRepository.cpfJaCadastrado(cpf)) {
            throw new RuntimeException("CPF já cadastrado: " + cpf);
        }

        Cliente cliente = new Cliente(nome.trim(), email.trim().toLowerCase(), formatarCpf(cpf));

        // Enriquecimento via ViaCEP
        if (cep != null && !cep.isBlank()) {
            try {
                EnderecoDTO endereco = viaCepService.buscarEndereco(cep);
                cliente.setCep(endereco.cep);
                cliente.setLogradouro(endereco.logradouro);
                cliente.setBairro(endereco.bairro);
                cliente.setCidade(endereco.cidade);
                cliente.setUf(endereco.uf);
                System.out.println("  ✔ Endereço encontrado: " + endereco);
            } catch (ViaCepException e) {
                // CEP inválido não impede o cadastro — apenas avisa
                System.out.println("  ⚠ Aviso: " + e.getMessage() + " (endereço não preenchido)");
            }
        }

        return clienteRepository.salvar(cliente);
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um cliente.
     * Campos nulos ou em branco mantêm o valor original.
     */
    public Cliente atualizar(Long id, String novoNome, String novoEmail, String novoCep) {
        Cliente cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente #" + id + " não encontrado."));

        if (novoNome  != null && !novoNome.isBlank())  { validarNome(novoNome);  cliente.setNome(novoNome.trim()); }
        if (novoEmail != null && !novoEmail.isBlank())  {
            validarEmail(novoEmail);
            if (!novoEmail.equalsIgnoreCase(cliente.getEmail()) && clienteRepository.emailJaCadastrado(novoEmail)) {
                throw new RuntimeException("E-mail já pertence a outro cliente.");
            }
            cliente.setEmail(novoEmail.trim().toLowerCase());
        }

        if (novoCep != null && !novoCep.isBlank()) {
            try {
                EnderecoDTO endereco = viaCepService.buscarEndereco(novoCep);
                cliente.setCep(endereco.cep);
                cliente.setLogradouro(endereco.logradouro);
                cliente.setBairro(endereco.bairro);
                cliente.setCidade(endereco.cidade);
                cliente.setUf(endereco.uf);
                System.out.println("  ✔ Endereço atualizado: " + endereco);
            } catch (ViaCepException e) {
                System.out.println("  ⚠ Aviso: " + e.getMessage());
            }
        }

        return clienteRepository.atualizar(cliente);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Remove um cliente e, por cascade, todos os seus pedidos.
     */
    public boolean deletar(Long id) {
        if (!clienteRepository.buscarPorId(id).isPresent()) {
            throw new RuntimeException("Cliente #" + id + " não encontrado.");
        }
        return clienteRepository.deletar(id);
    }

    // ─── Validações ────────────────────────────────────────────────────────────

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome não pode ser vazio.");
        if (nome.trim().length() < 2)       throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres.");
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$"))
            throw new IllegalArgumentException("E-mail inválido: " + email);
    }

    private void validarCpf(String cpf) {
        String digits = cpf.replaceAll("[^0-9]", "");
        if (digits.length() != 11)
            throw new IllegalArgumentException("CPF deve ter 11 dígitos numéricos.");
    }

    private String formatarCpf(String cpf) {
        String d = cpf.replaceAll("[^0-9]", "");
        return d.substring(0,3)+"."+d.substring(3,6)+"."+d.substring(6,9)+"-"+d.substring(9,11);
    }
}
