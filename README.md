# Nordeste Serviços API

API RESTful desenvolvida para a gestão completa de ordens de serviço, clientes e operações da empresa Nordeste Serviços. O sistema oferece uma plataforma centralizada para administrar todos os aspectos do fluxo de trabalho, desde a criação de orçamentos até a finalização e faturamento dos serviços.

---

## ✨ Funcionalidades Principais

-   **Autenticação e Autorização**: Sistema de login seguro com JWT (JSON Web Tokens) e controle de acesso baseado em perfis de usuário (Ex: Administrador, Técnico).
-   **Gestão de Clientes**: CRUD completo para clientes, com validações para evitar exclusão de clientes com histórico de serviços.
-   **Ordens de Serviço (OS)**:
    -   Criação, atribuição e acompanhamento de OS.
    -   Diferentes status (Pendente, Em Andamento, Concluída, etc.).
    -   Associação de técnicos, equipamentos e clientes.
    -   Upload de fotos relacionadas à OS.
-   **Orçamentos**: Criação e gerenciamento de orçamentos, com possibilidade de conversão em Ordem de Serviço.
-   **Controle de Peças e Materiais**: Gerenciamento de inventário de peças e materiais utilizados nos serviços.
-   **Relatórios em PDF**: Geração automática de relatórios para Orçamentos e Ordens de Serviço.
-   **Tratamento de Erros Padronizado**: Respostas de erro consistentes e claras em toda a API.

---

## 🛠️ Tecnologias Utilizadas

-   **Backend**:
    -   [Java](https://www.java.com/pt-BR/)
    -   [Spring Boot](https://spring.io/projects/spring-boot)
    -   [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (com Hibernate)
    -   [Spring Security](https://spring.io/projects/spring-security)
    -   [Maven](https://maven.apache.org/) como gerenciador de dependências.
    -   [Lombok](https://projectlombok.org/) para redução de código boilerplate.
-   **Autenticação**:
    -   [JSON Web Tokens (JWT)](https://jwt.io/)
-   **Banco de Dados**:
    -   A API é compatível com bancos de dados SQL (configurável via `application.properties`).

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### **Pré-requisitos**

-   Java Development Kit (JDK) 17 ou superior.
-   Apache Maven.
-   Um banco de dados de sua escolha (PostgreSQL, MySQL, H2, etc.).

### **Configuração**

1.  **Clone o repositório:**
    ```bash
    git clone <URL_DO_REPOSITORIO>
    cd nordeste-servicos
    ```

2.  **Configure o Banco de Dados:**
    -   Abra o arquivo `src/main/resources/application.properties`.
    -   Modifique as propriedades `spring.datasource.url`, `spring.datasource.username`, e `spring.datasource.password` com as credenciais do seu banco de dados.

3.  **Configure as Variáveis de Ambiente:**
    -   Ainda em `application.properties`, configure a chave secreta para a geração de JWT na propriedade `jwt.secret`.

### **Execução**

-   Use o Maven para compilar e executar a aplicação:
    ```bash
    mvn spring-boot:run
    ```
-   A API estará disponível em `http://localhost:8080`.

---

## API Endpoints

Abaixo estão os principais grupos de endpoints da API:

-   `/auth` - Endpoints para autenticação (login).
-   `/usuarios` - Gerenciamento de usuários.
-   `/clientes` - Gerenciamento de clientes.
-   `/ordens-servico` - Gerenciamento de Ordens de Serviço.
-   `/orcamentos` - Gerenciamento de orçamentos.
-   `/pecas-materiais` - Gerenciamento de peças e materiais.
-   `/equipamentos` - Gerenciamento de equipamentos.
-   ... e outros endpoints auxiliares.

---
Desenvolvido com ❤️ por Codagis. 