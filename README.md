# 📦 CRUD Clientes e Pedidos

Aplicação Java com CRUD completo para as entidades **Cliente** e **Pedido**, integração com banco de dados **MySQL** via **JPA/Hibernate**, e consumo da **API pública ViaCEP** para preenchimento automático de endereços.

---

## 🏗️ Arquitetura

```
crud-clientes-pedidos/
├── src/main/java/com/crud/
│   ├── Main.java                          # Ponto de entrada (menu principal)
│   ├── config/
│   │   └── HibernateConfig.java          # Configuração JPA + carrega .env
│   ├── model/
│   │   ├── Cliente.java                  # Entidade Cliente (@Entity)
│   │   └── Pedido.java                   # Entidade Pedido (@Entity)
│   ├── repository/
│   │   ├── ClienteRepository.java        # CRUD no banco para Cliente
│   │   └── PedidoRepository.java         # CRUD no banco para Pedido
│   ├── service/
│   │   ├── ClienteService.java           # Regras de negócio de Cliente
│   │   ├── PedidoService.java            # Regras de negócio de Pedido
│   │   └── ViaCepService.java            # Consumo da API ViaCEP
│   └── ui/
│       ├── MenuCliente.java              # Menu interativo de Clientes
│       └── MenuPedido.java               # Menu interativo de Pedidos
├── src/main/resources/
│   └── META-INF/persistence.xml         # Configuração JPA/Hibernate
├── .env                                  # Variáveis de ambiente (NÃO commitado)
├── .env.example                          # Modelo do .env
├── .gitignore
└── pom.xml                               # Dependências Maven
```

---

## ✅ Funcionalidades

### Clientes
- **Criar**: nome, e-mail (único), CPF (único) e CEP (opcional)
- **Listar**: todos os clientes com quantidade de pedidos
- **Buscar**: por ID
- **Atualizar**: nome, e-mail e/ou CEP
- **Deletar**: remove o cliente e todos os seus pedidos (cascade)

### Pedidos
- **Criar**: vinculado a um cliente existente, com descrição e valor
- **Listar**: todos os pedidos ou filtrados por cliente
- **Buscar**: por ID
- **Atualizar**: descrição, valor e/ou status
- **Deletar**: por ID

### Integração ViaCEP
- Ao cadastrar ou atualizar um cliente com CEP, o sistema consulta automaticamente a API `https://viacep.com.br/ws/{cep}/json/` e preenche logradouro, bairro, cidade e UF.

---

## 🛠️ Pré-requisitos

| Ferramenta | Versão mínima |
|------------|---------------|
| Java JDK   | 17            |
| Maven      | 3.8           |
| MySQL      | 8.0           |

---

## 🚀 Instalação e Execução

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/crud-clientes-pedidos.git
cd crud-clientes-pedidos
```

### 2. Configure o banco de dados

Acesse o MySQL e crie o banco:

```sql
CREATE DATABASE crud_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> O Hibernate cria as tabelas automaticamente na primeira execução (`hbm2ddl.auto=update`).

### 3. Configure o arquivo .env

Copie o exemplo e preencha com suas credenciais:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
DB_URL=jdbc:mysql://localhost:3306/crud_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=sua_senha
```

### 4. Compile o projeto

```bash
mvn clean package -q
```

O Maven gerará o arquivo `target/crud-clientes-pedidos-1.0-SNAPSHOT.jar`.

### 5. Execute

```bash
java -jar target/crud-clientes-pedidos-1.0-SNAPSHOT.jar
```

---

## 🖥️ Exemplo de Uso

```
╔══════════════════════════════════════════╗
║   Sistema de Clientes e Pedidos  v1.0   ║
║   Java + Hibernate + MySQL + ViaCEP     ║
╚══════════════════════════════════════════╝

  Conectando ao banco de dados... OK ✔

╔══════════════════════════════════╗
║        MENU PRINCIPAL            ║
╠══════════════════════════════════╣
║  1. Gerenciar Clientes           ║
║  2. Gerenciar Pedidos            ║
║  0. Sair                         ║
╚══════════════════════════════════╝
  Opção: 1

── Cadastrar Novo Cliente ──────────────────
  Nome     : João Silva
  E-mail   : joao@email.com
  CPF      : 12345678901
  CEP (opcional): 01310100

  ✔ Endereço encontrado: Avenida Paulista, Bela Vista - São Paulo/SP

  ✅ Cliente cadastrado com sucesso!
┌─ Cliente #1 ──────────────────────────────
│  Nome    : João Silva
│  E-mail  : joao@email.com
│  CPF     : 123.456.789-01
│  Endereço: Avenida Paulista, Bela Vista - São Paulo/SP (CEP: 01310-100)
│  Pedidos : 0
└────────────────────────────────────────────
```

---

## 📚 Dependências

| Dependência         | Versão   | Finalidade                      |
|---------------------|----------|---------------------------------|
| hibernate-core      | 6.4.4    | ORM / JPA                       |
| mysql-connector-j   | 8.3.0    | Driver JDBC para MySQL          |
| dotenv-java         | 3.0.0    | Leitura do arquivo .env         |
| jackson-databind    | 2.17.0   | Parse do JSON retornado pelo ViaCEP |
| slf4j-simple        | 2.0.12   | Log interno do Hibernate        |

---

## 🔗 API Externa — ViaCEP

- **Endpoint**: `GET https://viacep.com.br/ws/{cep}/json/`
- **Documentação**: https://viacep.com.br
- **Gratuita**: sim, sem necessidade de chave de API
- **Uso no projeto**: Consultada ao cadastrar/atualizar o endereço de um cliente

---

## 🗂️ Versionamento Git

```bash
git init
git add .
git commit -m "feat: CRUD completo de Clientes e Pedidos com ViaCEP"
git remote add origin https://github.com/seu-usuario/crud-clientes-pedidos.git
git push -u origin main
```

> ⚠️ O arquivo `.env` está no `.gitignore` e **nunca** deve ser commitado.
