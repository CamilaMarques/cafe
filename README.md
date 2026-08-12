# ☕ Aroma & Origem

API RESTful desenvolvida em Spring Boot para o gerenciamento de catálogos e recomendação de cafés especiais, integrando filtros avançados de perfil sensorial e suporte à arquitetura moderna.

---

### 🚀 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot** (Web, Data JPA)
* **PostgreSQL** (Banco de dados relacional)
* **Apache Kafka** (Mensageria para eventos de assinatura e recorrência)
* **Lombok** (Redução de boilerplate code)
* **Maven** (Gerenciamento de dependências)

---

### 🛠️ Arquitetura e Estrutura do Projeto

O projeto segue os princípios de separação de responsabilidades em camadas:
* **Controller:** Camada de exposição dos endpoints REST.
* **Service:** Centralização das regras de negócio (filtros e motor de busca).
* **Repository:** Comunicação com o banco de dados via Spring Data JPA.
* **Model:** Entidades de domínio mapeadas para o PostgreSQL.

---

### ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos
* Java 21 instalado
* PostgreSQL configurado na sua máquina (ou via DBeaver)
* Maven

### 1. Clonar o Repositório

- git clone [https://github.com/SEU_USUARIO/cafe.git](https://github.com/SEU_USUARIO/cafe.git)
- cd cafe

---

### 2. Configurar o arquivo de Segredos (secrets.properties)

Crie um arquivo chamado secrets.properties dentro da pasta src/main/resources/ com as suas credenciais locais do PostgreSQL:
- db.username=postgres
- db.password=sua_senha_aqui

---

### 3. Criar o Banco de Dados

No seu DBeaver, crie um banco PostgreSQL chamado:
- CREATE DATABASE aroma_origem;

---

### 4. Executar a Aplicação

Inicie o projeto através da sua IDE favorita (IntelliJ IDEA, Eclipse) rodando a classe principal AromaOrigemApplication.java, ou via terminal:
- ./mvnw spring-boot:run

---

### 📌 Endpoints Principais

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/cafes` | Lista todos os cafés cadastrados |
| `POST` | `/api/cafes` | Cadastra um novo café |
| `GET` | `/api/cafes/filtro/regiao?regiao=...` | Filtra cafés por região de origem |
| `GET` | `/api/cafes/filtro/altitude?min=...` | Filtra cafés por altitude mínima |

---

### 👨‍💻 Desenvolvido por

- Camila Marques / https://github.com/CamilaMarques


