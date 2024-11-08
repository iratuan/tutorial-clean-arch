![Arquitetura Limpa](/images/f03_capa.png "Capa")

# Tutorial de clean architecture utilizando spring boot

- Autor: Iratuã Júnior
- Data: 07/11/2024
- Versão do springboot: 3.3.5
- Versão jdk: 17
- [Respositorio no github](https://github.com/iratuan/tutorial-clean-arch)

## Sobre o tutorial
Neste tutorial, abordaremos os fundamentos da Clean Architecture e demonstraremos como aplicar esse conceito no desenvolvimento de uma API REST utilizando Spring Boot. Vamos explorar a estrutura modular da Clean Architecture, destacando as camadas de domínio, aplicação, infraestrutura e interfaces. Cada seção do tutorial mostrará como organizar o código de forma a garantir independência entre camadas, facilitando a manutenção e a escalabilidade do projeto. Além disso, abordaremos práticas como a inversão de dependência e o uso de interfaces para garantir a flexibilidade e adaptabilidade da API ao longo de sua evolução, mantendo uma estrutura clara e bem definida.

## Passo 1 - Criação do projeto
Utilizando o spring initializer, crie um projeto spring boot, utilizando a versão 3.3.5 ou posterior, e adicione as seguintes dependências

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
### Explicando cada uma das dependências

1. **spring-boot-starter-data-jpa**:
    - Fornece suporte ao JPA (Java Persistence API) e ao Hibernate, permitindo interações com o banco de dados de forma simplificada usando repositórios.

2. **spring-boot-starter-web**:
    - Inclui os componentes necessários para construir aplicações web com Spring MVC, incluindo suporte a RESTful APIs e o servidor embutido Tomcat.

3. **spring-boot-devtools**:
    - Ferramenta para facilitar o desenvolvimento, oferecendo funcionalidades como recarga automática de classes e desativação de cache, usadas apenas em ambiente de desenvolvimento.

4. **postgresql**:
    - Driver JDBC para conectar-se a um banco de dados PostgreSQL, necessário para que o Spring Boot possa interagir com o PostgreSQL.

5. **lombok**:
    - Biblioteca que reduz o código repetitivo ao gerar automaticamente getters, setters, construtores, e outros métodos, simplificando a manutenção do código.

6. **spring-boot-starter-test**:
    - Conjunto de ferramentas de teste para Spring Boot, incluindo JUnit e Mockito, permitindo a criação de testes unitários e de integração para a aplicação.

## Passo 2 - Configurando o banco de dados que utilizaremos

Para configurar o banco de dados da nossa API, utilizaremos o PostgreSQL, mas em vez de instalar o banco de dados diretamente na máquina, subiremos um container Docker para facilitar a configuração e garantir maior portabilidade. Esse container PostgreSQL será gerenciado pelo Docker Compose, permitindo que o banco de dados seja iniciado e configurado de forma rápida e consistente com o restante do projeto. Para isso, precisaremos criar um arquivo `docker-compose.yml` na raiz do projeto. Esse arquivo contém todas as instruções necessárias para que o Docker Compose crie o ambiente PostgreSQL configurado corretamente para nossa aplicação. Não esqueça de criar o diretório `data` na raiz do seu projeto, isso garantirá que você não irá perder os dados após encerrar a execução do seu container docker.
```yaml
version: '3.8'

services:
    postgres:
        image: postgres:15  # Utilize a versão que preferir
        container_name: pgc_api_rest
        environment:
            POSTGRES_USER: postgres        # Substitua por seu usuário
            POSTGRES_PASSWORD: postgres  # Substitua pela sua senha
            POSTGRES_DB: apirest       # Substitua pelo nome do seu banco de dados
        volumes:
            - ./data:/var/lib/postgresql/data  # Monta a pasta `data` para persistir os dados
        ports:
            - "5432:5432"

```
> Para subir o container, utilize o comando `docker compose up -d`


Agora, precisamos configurar o arquivo `application.properties` para conectar à imagem docker do banco de dados.
```properties

# Configuração do DataSource
spring.datasource.url=jdbc:postgresql://localhost:5432/apirest
spring.datasource.username=postgres
spring.datasource.password=postgres

# Configurações adicionais para o PostgreSQL
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Configuração para mostrar as queries SQL no console
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Configuração para gerenciamento de schema (opcional)
spring.jpa.hibernate.ddl-auto=update

```

Feito isso, já teremos a nossa aplicação configurada para acessar o banco de dados `Postgres` através do container `docker`.

## Passo 3 - Criando a estrutura de pastas do projeto

![Arquitetura Limpa](/images/f02_cleanarch.png "Clean Arch")

**Resumo sobre a Arquitetura Limpa:**

A Arquitetura Limpa, proposta por Robert C. Martin (conhecido como Uncle Bob), é uma abordagem de design de software que enfatiza a independência e a manutenção do código. Seu principal objetivo é separar a lógica de negócio (regras e entidades) dos detalhes de implementação (frameworks, bancos de dados, interfaces de usuário), criando camadas distintas que se comunicam através de interfaces. Isso facilita a testabilidade, a escalabilidade e a adaptabilidade da aplicação, permitindo que mudanças em uma camada não afetem as demais.

**Tabela explicativa dos pacotes:**

| **Pacote**     | **Descrição**                                                                                                                                                                                                                                                                                      |
|----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **domain**     | Contém as **entidades** e **regras de negócio** fundamentais da aplicação. Representa o núcleo do sistema, independente de detalhes técnicos ou frameworks. Aqui estão as classes que modelam os conceitos centrais do domínio, focando na lógica pura de negócio.                                 |
| **application**| Abriga os **casos de uso** da aplicação, que orquestram as interações entre as entidades do domínio para atender às funcionalidades requisitadas. Contém a lógica que define como os dados são processados e como as operações de negócio são executadas, sem depender de detalhes externos.       |
| **gateway**    | Define as **interfaces** que permitem a comunicação entre a camada de aplicação (casos de uso) e os sistemas externos, como bancos de dados, serviços web, ou outros meios de persistência e comunicação. Essa camada abstrai os detalhes de implementação, promovendo a inversão de dependências. |
| **infra**      | Implementa os detalhes de infraestrutura e tecnologia, como acesso a bancos de dados, configurações de frameworks, serviços externos e outras dependências técnicas. Aqui estão as classes concretas que realizam as operações definidas pelas interfaces no pacote **gateway**.                   |
| **main**       | Contém o código de inicialização da aplicação, configurações gerais e a composição das dependências. É onde o framework (por exemplo, Spring Boot) é utilizado para montar o aplicativo, iniciar o servidor e conectar todas as camadas, respeitando as diretrizes da arquitetura limpa.           |

Essa estrutura modular promove a separação de preocupações, facilitando a manutenção e evolução do sistema. Ao isolar as regras de negócio dos detalhes técnicos, a aplicação se torna mais robusta frente a mudanças, permitindo substituições ou atualizações de tecnologias sem impactar o núcleo do negócio.

![Estrutura de diretórios do projeto!](/images/f01_diretorios.png "Estrutura de diretórios")

## Passo 4 - Criando as entidades da API
Entramos na etapa mais importante desse tutorial, que é a codificação propriamente dita, porém, para codificar de forma eficiente, pensando em padrões de projeto e em boas práticas de código, é necessário que se conheça a `API` e se entenda seu propósito. Abaixo forneço uma breve descrição da `API`.

**Descrição da API de Pacientes:**

Essa API terá como principal recurso o **paciente**, permitindo o gerenciamento dos dados pessoais de cada paciente, bem como o acesso ao seu histórico médico resumido. A API será estruturada para realizar operações CRUD (criação, leitura, atualização e exclusão) sobre os dados dos pacientes, e o histórico médico incluirá informações relevantes sobre consultas, diagnósticos e tratamentos passados.

### Estrutura de Dados em JSON para o Recurso Paciente

Abaixo está um exemplo de como o JSON de resposta da API poderia ser estruturado para representar os dados de um paciente e seu histórico médico:

```json
{
  "id": 1,
  "nome": "João da Silva",
  "data_nascimento": "1985-08-15",
  "genero": "Masculino",
  "contato": {
    "telefone": "+5511999999999",
    "email": "joao.silva@example.com"
  },
  "endereco": {
    "rua": "Rua Exemplo",
    "numero": 123,
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01000-000"
  },
  "historico_medico": [
    {
      "data_consulta": "2023-01-20",
      "diagnostico": "Hipertensão",
      "tratamento": "Uso contínuo de medicamento",
      "observacoes": "Recomendado controle de pressão arterial mensal."
    },
    {
      "data_consulta": "2022-12-15",
      "diagnostico": "Gripe",
      "tratamento": "Repouso e hidratação",
      "observacoes": "Consulta de retorno desnecessária."
    }
  ]
}
```

### Explicação dos Campos:

- **id**: Identificador único do paciente.
- **nome**: Nome completo do paciente.
- **data_nascimento**: Data de nascimento do paciente.
- **genero**: Gênero do paciente (opcionalmente pode ser mais específico, como “Masculino”, “Feminino”, “Outro”).
- **contato**: Informações de contato do paciente.
    - **telefone**: Número de telefone.
    - **email**: Endereço de e-mail.
- **endereco**: Endereço completo do paciente.
- **historico_medico**: Lista com os registros médicos do paciente.
    - **data_consulta**: Data da consulta.
    - **diagnostico**: Diagnóstico feito na consulta.
    - **tratamento**: Tratamento recomendado ou realizado.
    - **observacoes**: Observações adicionais feitas pelo médico.

Esse formato JSON fornece uma estrutura clara e organizada para a API, permitindo fácil acesso e compreensão dos dados de cada paciente, além de um histórico médico resumido que facilita a visualização das consultas passadas.

### Identificando o domínio e suas entidades de domínio
Observando o `json` fornecido, podemos identificar quatro `agrupamentos` de informações. Podemos dizer quem um `paciente` contém `informações pessoais`, `um endereço`, `um contato` e `uma lista de atendimentos`. Note que, dito isso, podemos iniciar a modelagem das nossas entidades de domínio, que será representações limpas e agnósticas em relação a `frameworks`. São `POJOs` em sua essência. 

### Codificando as classes
As entidades de domínio identificadas são:
- Paciente
- Contato
- Endereco
- HistoricoMedico

Domínio: **Paciente**
```java
package br.com.aygean.cleanarch.domain;

import java.time.LocalDate;
import java.util.List;

public class Paciente {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private Contato contato;
    private Endereco endereco;
    private List<HistoricoMedico> historicoMedico;

    public Paciente() {
        // no args constructor
    }

    public Paciente(Long id, String nome, LocalDate dataNascimento, String genero, Contato contato, Endereco endereco, List<HistoricoMedico> historicoMedico) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<HistoricoMedico> getHistoricoMedico() {
        return historicoMedico;
    }

    public void setHistoricoMedico(List<HistoricoMedico> historicoMedico) {
        this.historicoMedico = historicoMedico;
    }
}

```

Domínio: **Contato**
```java
package br.com.aygean.cleanarch.domain;

public class Contato {
    private String telefone;
    private String email;

    public Contato() {
        // no args constructor
    }

    public Contato(String telefone, String email) {
        this.telefone = telefone;
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

```
Domínio: **Endereco**
```java
package br.com.aygean.cleanarch.domain;

public class Endereco {
    private String rua;
    private Integer numero;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {
        // no args constructor
    }

    public Endereco(String rua, Integer numero, String cidade, String estado, String cep) {
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}

```
Domínio: **HistoricoMedico**
```java
package br.com.aygean.cleanarch.domain;

import java.time.LocalDate;

public class HistoricoMedico {
    private LocalDate dataConsulta;
    private String diagnostico;
    private String tratamento;
    private String observacoes;

    public HistoricoMedico() {
        //no args constructor
    }

    public HistoricoMedico(LocalDate dataConsulta, String diagnostico, String tratamento, String observacoes) {
        this.dataConsulta = dataConsulta;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }

    public LocalDate getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDate dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
```

Note que todas as classes são representações `planas` do  domínio e não fazem referência a `frameworks`, garantindo o isolamento da camada de domínio.

### Codificando os gateways (ou o gateway)
Essa é a segunda etapa da codificação da nossa api. Nesse ponto, perceba que existem `verbos` que utilizamos falar de um paciente no contexto da `api`. 

Exemplo:
- eu posso `cadastrar` um paciente
- eu posso `buscar` as informações de um paciente
- eu posso `atualizar` as informações de um paciente
- eu posso `excluir` um paciente

Essa é a dica que precisamos para criarmos os nosso `gateways` que definem as interfaces de comunicação entre a aplicação e os recursos externos, no nosso caso, banco de dados `postgres`.

No pacote gateway, crie a interface abaixo, que é auto-explicativa.
```java
package br.com.aygean.cleanarch.gateway;

import br.com.aygean.cleanarch.domain.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteGateway {
    List<Paciente> listarPacientes();
    Paciente adicionarPaciente(Paciente paciente);
    Optional<Paciente> buscarPacientePorId(Long id);
    Optional<Paciente> atualizarPaciente(Paciente paciente);
    Optional<List<Paciente>> buscarPacientes(String nome);
    void deletarPaciente(Paciente paciente);
}
```

Note que trata-se de uma interface, ou seja, só possui assinaturas dos métodos (contratos).

### Implementando o primeiro caso de uso

Para implementar os casos de usos referentes a nossa api, precisamos criar uma classe `PacienteUseCase` na pasta `application`. Essa classe fará uso da interface do `gateway`, tendo em vista que seu objetivo é prover funcionalidade para a api. 

Foi necessário criar também uma classe `PacienteNotFoundException`, que herda de `RuntimeException`. Ela reside dentro de application, mas é uma questão filosófica, podendo residir também dentro da pasta de domínio ou de infra. 

```java
package br.com.aygean.cleanarch.application;

import br.com.aygean.cleanarch.application.exception.PacienteNotFoundException;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.gateway.PacienteGateway;

import java.util.List;
import java.util.Optional;

public class PacienteUseCase {
    // Dependência para acessar o repositório de pacientes
    private final PacienteGateway gateway;

    // Construtor que recebe uma implementação de PacienteGateway
    public PacienteUseCase(PacienteGateway gateway) {
        this.gateway = gateway;
    }

    // Lista todos os pacientes
    public List<Paciente> listarPacientes() {
        return gateway.listarPacientes();
    }

    // Adiciona um novo paciente e o retorna após o salvamento
    public Paciente adicionarPaciente(Paciente paciente) {
        return gateway.adicionarPaciente(paciente);
    }

    // Busca um paciente por ID e lança uma exceção se não for encontrado
    public Paciente buscarPacientePorId(Long id) {
        return gateway.buscarPacientePorId(id)
            .orElseThrow(() -> new PacienteNotFoundException("Paciente não encontrado"));
    }

    // Atualiza os dados de um paciente existente e lança exceção se o ID não existir
    public Paciente atualizarPaciente(Long id, Paciente paciente) {
        // Verifica se o paciente existe, lançando exceção se não for encontrado
        buscarPacientePorId(id);

        // Define o ID do paciente a ser atualizado e salva
        paciente.setId(id);

        // Redefine o paciente para sua versão atualizada
        paciente = gateway.atualizarPaciente(paciente).get();
        return paciente;
    }

    // Busca pacientes pelo nome e lança exceção se não houver resultados
    public List<Paciente> buscarPacientes(String nome) {
        Optional<List<Paciente>> pacientes = gateway.buscarPacientes(nome);

        // Lança exceção se a lista estiver vazia
        if (pacientes.isEmpty()) {
            throw new PacienteNotFoundException("Não encontramos resultados que satisfaçam os critérios de sua busca");
        }
        // Retorna a lista de pacientes
        return pacientes.get();
    }

    // Deleta um paciente pelo ID, após verificar sua existência
    public void deletarPaciente(Long id) {
        // Verifica se o paciente existe, lançando exceção se não for encontrado
        Paciente paciente = buscarPacientePorId(id);

        // Realiza a exclusão do paciente
        gateway.deletarPaciente(paciente);
    }
}
```

#### Explicação dos Comentários

- **Dependência e Construtor**: O construtor `PacienteUseCase` injeta a dependência `PacienteGateway`, usada para interagir com o armazenamento de dados dos pacientes.
- **Métodos de CRUD**:
    - `listarPacientes`: Retorna todos os pacientes.
    - `adicionarPaciente`: Adiciona um novo paciente ao repositório.
    - `buscarPacientePorId`: Tenta buscar um paciente pelo `id`. Lança `PacienteNotFoundException` se não encontrado.
    - `atualizarPaciente`: Atualiza um paciente existente, após validar sua existência.
    - `buscarPacientes`: Busca pacientes por `nome`. Lança uma exceção se a busca não encontrar resultados.
    - `deletarPaciente`: Deleta um paciente pelo `id` após verificar se ele existe.

Esses métodos implementam a lógica de negócio para cada operação, garantindo que exceções sejam lançadas em casos de dados não encontrados, o que facilita o tratamento de erros no nível de aplicação.

**PacienteNotFoundException**
```java
package br.com.aygean.cleanarch.application.exception;

public class PacienteNotFoundException extends RuntimeException {

    public PacienteNotFoundException(Long id) {
        super("Paciente com ID " + id + " não foi encontrado.");
    }

    public PacienteNotFoundException(String message) {
        super(message);
    }
}
```

### Codificando nossa camada de infraestrutura

**Breve explicação sobre essa camada, que é muito importante que você domine o conceito.**

Na **camada de infraestrutura** da Clean Architecture, centralizamos todos os componentes que lidam com a comunicação externa e detalhes específicos de implementação, mantendo a independência das regras de negócio. Nessa camada, vamos implementar os **DTOs**, **Entidades de Persistência**, **Services** e **Controllers**. Esses elementos interagem diretamente com o mundo externo, como banco de dados, APIs, e interfaces de usuário.

| Componente               | Descrição                                                                                                                                                                                |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **DTOs (Data Transfer Objects)**   | Objetos criados para transferir dados entre as camadas da aplicação, como entre o controlador e o cliente. Eles encapsulam dados de entrada e saída, mantendo a segurança e abstração do domínio. |
| **Entidades de Persistência** | Representam as tabelas do banco de dados e são mapeadas para classes do domínio por meio de frameworks como JPA/Hibernate. Essas entidades isolam a lógica de persistência da lógica de negócios.         |
| **Services**             | Contêm a lógica que depende de detalhes de infraestrutura, como serviços de banco de dados ou integrações externas. Esses serviços implementam interfaces definidas em camadas superiores. |
| **Controllers**          | Responsáveis por expor a API da aplicação e manipular as requisições HTTP. Eles recebem dados, acionam os casos de uso apropriados e retornam respostas formatadas aos clientes.       |

### Detalhamento de Cada Componente

- **DTOs**: Definem os dados necessários para requisições e respostas. São independentes das entidades de domínio e ajudam a proteger a aplicação de exposições desnecessárias de dados.

- **Entidades de Persistência**: Ligam a aplicação ao banco de dados, traduzindo as entidades de domínio para um formato compreensível pela infraestrutura de armazenamento, como tabelas relacionais.

- **Services**: Implementam a lógica de infraestrutura, como recuperação e manipulação de dados persistentes, além de gerenciar dependências com ferramentas e frameworks específicos.

- **Controllers**: Definem os endpoints da API e lidam com a entrada e saída de dados. Os controladores são a ponte entre o mundo externo e o núcleo da aplicação, enviando os dados para os casos de uso adequados.

Essa estrutura permite uma aplicação flexível, modular e adaptável, onde a camada de infraestrutura suporta a operação do sistema sem interferir na lógica de negócios do domínio.

Iremos criar agora os componentes de infra, e esses sim, podem utilizar frameworks como `jpa`, `lombock`, `anottations do spring` etc.

### Criando as entidades `de persistência`, que irão relacionar nosso domínio com as tabelas do nosso banco de dados.

No pacote `infra.entity`, crie as seguintes classes:


**PacienteEntity**
```java
package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataNascimento;
    private String genero;

    // Relacionamento um-para-um com Contato
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contato_id", referencedColumnName = "id")
    private ContatoEntity contato;

    // Relacionamento um-para-um com Endereco
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecoEntity endereco;

    // Relacionamento um-para-muitos com HistoricoMedico
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<HistoricoMedicoEntity> historicoMedico = new ArrayList<>();

    // Construtor sem ID para facilitar a criação de instâncias sem definir ID
    public PacienteEntity(String nome, LocalDate dataNascimento, String genero, ContatoEntity contato, EnderecoEntity endereco, List<HistoricoMedicoEntity> historicoMedico) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico != null ? historicoMedico : new ArrayList<>();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PacienteEntity that = (PacienteEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
```

**ContatoEntity**
```java
package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "contatos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefone;
    private String email;
}
```

**EnderecoEntity**
```java
package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "enderecos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rua;
    private Integer numero;
    private String cidade;
    private String estado;
    private String cep;
}

```

**HistoricoMedicoEntity**
```java
package br.com.aygean.cleanarch.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "historicos_medicos_paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataConsulta;
    private String diagnostico;
    private String tratamento;
    private String observacoes;

    public HistoricoMedicoEntity(LocalDate dataConsulta, String diagnostico, String tratamento, String observacoes) {
        this.dataConsulta = dataConsulta;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }
}
```
Note que as entities tem a função de representar o mapeamento do objeto em relação ao banco de dados.

**Criando o `repository` da entidade `PacienteEntity`.
````java
package br.com.aygean.cleanarch.infra.repository;

import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends CrudRepository<PacienteEntity, Long> {
    @Query("select p from PacienteEntity p where upper(p.nome) like upper(?1)")
    List<PacienteEntity> findByNome(String nome);
}

````
O repository é responsável pelas operações entre a entidade de o banco de dados.

**Criando os DTOs**

Nessa etapa, crie os `dto's` que são classes simples (até certo ponto) para transferência de dados.

**ContatoDTO**
```java
package br.com.aygean.cleanarch.infra.dto;

public record ContatoDTO(String telefone,
                         String email) {
}
```

**EnderecoDTO**
```java
package br.com.aygean.cleanarch.infra.dto;

public record EnderecoDTO(String rua,
                          Integer numero,
                          String cidade,
                          String estado,
                          String cep) {
}
```

**HistoricoMedicoDTO**
```java
package br.com.aygean.cleanarch.infra.dto;

import java.time.LocalDate;

public record HistoricoMedicoDTO(LocalDate dataConsulta,
                                 String diagnostico,
                                 String tratamento,
                                 String observacoes) {
}

```

**PacienteDTO**
```java
package br.com.aygean.cleanarch.infra.dto;

import java.time.LocalDate;
import java.util.List;

public record PacienteDTO(Long id,
                          String nome,
                          LocalDate dataNascimento,
                          String genero,
                          ContatoDTO contato,
                          EnderecoDTO endereco,
                          List<HistoricoMedicoDTO> historicoMedico) {
}


```

**Criando a classe converter, que irá converter um objeto de um tipo para outro**

No pacote `infra.converter`, crie a classe `PacienteConverter.java`

```java
package br.com.aygean.cleanarch.infra.converter;

import br.com.aygean.cleanarch.domain.Contato;
import br.com.aygean.cleanarch.domain.Endereco;
import br.com.aygean.cleanarch.domain.HistoricoMedico;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.infra.dto.ContatoDTO;
import br.com.aygean.cleanarch.infra.dto.EnderecoDTO;
import br.com.aygean.cleanarch.infra.dto.HistoricoMedicoDTO;
import br.com.aygean.cleanarch.infra.dto.PacienteDTO;
import br.com.aygean.cleanarch.infra.entity.ContatoEntity;
import br.com.aygean.cleanarch.infra.entity.EnderecoEntity;
import br.com.aygean.cleanarch.infra.entity.HistoricoMedicoEntity;
import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class PacienteConverter {

    public static Paciente fromDtoToDomain(PacienteDTO dto) {

        var contato = new Contato();
        BeanUtils.copyProperties(dto.contato(), contato);
        var endereco = new Endereco();
        BeanUtils.copyProperties(dto.endereco(), endereco);
        var historicoMedico = dto.historicoMedico().stream().map((h) -> {
            var historicoMedicoDomain = new HistoricoMedico();
            BeanUtils.copyProperties(h, historicoMedicoDomain);
            return historicoMedicoDomain;
        }).toList();
        return new Paciente(dto.id(), dto.nome(), dto.dataNascimento(), dto.genero(), contato, endereco, historicoMedico);
    }

    public static PacienteEntity fromDtoToEntity(PacienteDTO dto) {

        var contatoEntity = new ContatoEntity();
        BeanUtils.copyProperties(dto.contato(), contatoEntity);
        var enderecoEntity = new EnderecoEntity();
        BeanUtils.copyProperties(dto.endereco(), enderecoEntity);
        var historicoMedicoEntity = dto.historicoMedico().stream().map((h) -> {
            var historicoMedicoInnerEntity = new HistoricoMedicoEntity();
            BeanUtils.copyProperties(h, historicoMedicoInnerEntity);
            return historicoMedicoInnerEntity;
        }).toList();
        return new PacienteEntity(
            dto.nome(),
            dto.dataNascimento(),
            dto.genero(),
            contatoEntity,
            enderecoEntity,
            historicoMedicoEntity);
    }

    public static PacienteDTO fromDomainToDTO(Paciente domain) {
        var contatoDTO = new ContatoDTO(domain.getContato().getTelefone(),
            domain.getContato().getEmail());
        var enderecoDTO = new EnderecoDTO(domain.getEndereco().getRua(),
            domain.getEndereco().getNumero(),
            domain.getEndereco().getCidade(),
            domain.getEndereco().getEstado(),
            domain.getEndereco().getCep());
        var historicoMedicoDTO = domain.getHistoricoMedico().stream().map((h) -> new HistoricoMedicoDTO(
                h.getDataConsulta(),
                h.getDiagnostico(),
                h.getTratamento(),
                h.getObservacoes()))
            .toList();

        return new br.com.aygean.cleanarch.infra.dto.PacienteDTO(
            domain.getId(),
            domain.getNome(),
            domain.getDataNascimento(),
            domain.getGenero(),
            contatoDTO,
            enderecoDTO,
            historicoMedicoDTO);
    }


    public static Paciente fromEntityToDomain(PacienteEntity entity) {
        var contato = new Contato();
        BeanUtils.copyProperties(entity.getContato(), contato);
        var endereco = new Endereco();
        BeanUtils.copyProperties(entity.getEndereco(), endereco);

        var historicoMedico = entity.getHistoricoMedico().stream().map((h) -> new HistoricoMedico(
                h.getDataConsulta(),
                h.getDiagnostico(),
                h.getTratamento(),
                h.getObservacoes()))
            .toList();

        return new Paciente(
            entity.getId(),
            entity.getNome(),
            entity.getDataNascimento(),
            entity.getGenero(),
            contato,
            endereco,
            historicoMedico);
    }

    public static PacienteEntity fromDomainToEntity(Paciente domain) {
        var contatoEntity = new ContatoEntity();
        BeanUtils.copyProperties(domain.getContato(), contatoEntity);
        var enderecoEntity = new EnderecoEntity();
        BeanUtils.copyProperties(domain.getEndereco(), enderecoEntity);
        List<HistoricoMedicoEntity> historicoMedicoEntity = new ArrayList<>();
        List<HistoricoMedico> listaHistoricoMedico = domain.getHistoricoMedico();
        for (HistoricoMedico h : listaHistoricoMedico) {
            var historicoMedico = new HistoricoMedicoEntity(h.getDataConsulta(), h.getDiagnostico(), h.getTratamento(), h.getObservacoes());
            historicoMedicoEntity.add(historicoMedico);
        }
        return new PacienteEntity(
            domain.getId(),
            domain.getNome(),
            domain.getDataNascimento(),
            domain.getGenero(),
            contatoEntity,
            enderecoEntity,
            historicoMedicoEntity);
    }
}
```
### Criando a entidade de serviço 

A classe de serviço implementa os gateways e realiza a lógica transacional, ou seja, ela se comunica com base de dados, outros provedores de dados, integrações etc.

```java
package br.com.aygean.cleanarch.infra.service;

import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.gateway.PacienteGateway;
import br.com.aygean.cleanarch.infra.converter.PacienteConverter;
import br.com.aygean.cleanarch.infra.entity.PacienteEntity;
import br.com.aygean.cleanarch.infra.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteGatewayImpl implements PacienteGateway {

    private final PacienteRepository pacienteRepository;

    public PacienteGatewayImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public List<Paciente> listarPacientes() {
        var entityList = pacienteRepository.findAll();
        var pacienteList = new ArrayList<Paciente>();
        entityList.forEach(e -> {
            pacienteList.add(PacienteConverter.fromEntityToDomain(e));
        });
        return pacienteList;
    }

    @Override
    public Paciente adicionarPaciente(Paciente paciente) {
        var pacienteEntity = PacienteConverter.fromDomainToEntity(paciente);
        var pacienteEntityPersisted = pacienteRepository.save(pacienteEntity);
        return PacienteConverter.fromEntityToDomain(pacienteEntityPersisted);
    }

    @Override
    public Optional<Paciente> buscarPacientePorId(Long id) {
        Optional<PacienteEntity> optionalPacienteEntity = pacienteRepository.findById(id);
        return optionalPacienteEntity.map(PacienteConverter::fromEntityToDomain);
    }

    @Override
    public Optional<Paciente> atualizarPaciente(Paciente paciente) {
        var pacienteEntity = pacienteRepository.save(PacienteConverter.fromDomainToEntity(paciente));
        return Optional.of(PacienteConverter.fromEntityToDomain(pacienteEntity));
    }

    @Override
    public Optional<List<Paciente>> buscarPacientes(String nome) {
        List<PacienteEntity> pacienteEntityList = pacienteRepository.findByNome(nome);

        // Retorna Optional.empty() se a lista estiver vazia
        if (pacienteEntityList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pacienteEntityList.stream()
            .map(PacienteConverter::fromEntityToDomain)
            .toList());
    }

    @Override
    public void deletarPaciente(Paciente paciente) {
        // Verifica se o paciente existe antes de deletar
        if (paciente.getId() != null && pacienteRepository.existsById(paciente.getId())) {
            pacienteRepository.deleteById(paciente.getId());
        } else {
            throw new IllegalArgumentException("Paciente não encontrado ou ID inválido.");
        }
    }
}
```

Explicando o funcionamento da classe `PacienteGatewayImpl`:

1. **Classe Implementa a Interface `PacienteGateway`**:
    - Define métodos para gerenciar pacientes, atuando como uma camada de acesso aos dados.

2. **`listarPacientes()`**:
    - Busca todos os pacientes do repositório e converte cada entidade `PacienteEntity` para o objeto de domínio `Paciente`.

3. **`adicionarPaciente(Paciente paciente)`**:
    - Converte o objeto de domínio `Paciente` para `PacienteEntity`, salva-o no repositório e retorna o objeto salvo convertido novamente para `Paciente`.

4. **`buscarPacientePorId(Long id)`**:
    - Busca um paciente pelo ID. Se encontrado, converte o `PacienteEntity` para `Paciente` e o retorna dentro de um `Optional`.

5. **`atualizarPaciente(Paciente paciente)`**:
    - Converte o objeto `Paciente` para `PacienteEntity`, salva a atualização no repositório e retorna o paciente atualizado como `Paciente` dentro de um `Optional`.

6. **`buscarPacientes(String nome)`**:
    - Busca pacientes pelo nome. Retorna uma lista de pacientes convertidos, ou `Optional.empty()` se a lista estiver vazia.

7. **`deletarPaciente(Paciente paciente)`**:
    - Verifica se o paciente existe no repositório e, se sim, exclui-o usando o ID. Caso contrário, lança uma exceção.

8. **Uso de `PacienteConverter`**:
    - Utiliza o `PacienteConverter` para converter entre as entidades de domínio (`Paciente`) e de persistência (`PacienteEntity`).


### Criando o controller REST

Agora, iremos criar o controller rest que irá interagir com o usuário do sistema.

Crie a seguinte clase dentro do pacote `infra.controller`.

```java
package br.com.aygean.cleanarch.infra.controller;

import br.com.aygean.cleanarch.application.PacienteUseCase;
import br.com.aygean.cleanarch.domain.Paciente;
import br.com.aygean.cleanarch.infra.dto.PacienteDTO;
import br.com.aygean.cleanarch.infra.converter.PacienteConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteUseCase pacienteUseCase;

    public PacienteController(PacienteUseCase pacienteUseCase) {
        this.pacienteUseCase = pacienteUseCase;
    }

    // Método para listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarPacientes() {
        List<PacienteDTO> pacientes = pacienteUseCase.listarPacientes()
            .stream()
            .map(PacienteConverter::fromDomainToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pacientes);
    }

    // Método para buscar um paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> buscarPacientePorId(@PathVariable Long id) {
        Paciente paciente = pacienteUseCase.buscarPacientePorId(id);
        var pacienteDto = PacienteConverter.fromDomainToDTO(paciente);
        return ResponseEntity.ok(pacienteDto);
    }

    // Método para adicionar um novo paciente
    @PostMapping
    public ResponseEntity<PacienteDTO> adicionarPaciente(@RequestBody PacienteDTO pacienteDTO) {
        var paciente = PacienteConverter.fromDtoToDomain(pacienteDTO);
        var pacienteSalvo = pacienteUseCase.adicionarPaciente(paciente);
        return ResponseEntity.ok(PacienteConverter.fromDomainToDTO(pacienteSalvo));
    }

    // Método para atualizar um paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> atualizarPaciente(@PathVariable Long id, @RequestBody PacienteDTO pacienteDTO) {
        var paciente = PacienteConverter.fromDtoToDomain(pacienteDTO);
        Paciente pacienteAtualizado = pacienteUseCase.atualizarPaciente(id, paciente);
        var pacienteDto = PacienteConverter.fromDomainToDTO(pacienteAtualizado);
        return ResponseEntity.ok(pacienteDto);
    }

    // Método para deletar um paciente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        try {
            pacienteUseCase.deletarPaciente(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

### Explicação dos Métodos CRUD

1. **listarPacientes** (`GET /pacientes`): Retorna uma lista de todos os pacientes cadastrados.
2. **buscarPacientePorId** (`GET /pacientes/{id}`): Busca um paciente específico pelo ID. Retorna `404 Not Found` se não encontrar o paciente.
3. **adicionarPaciente** (`POST /pacientes`): Adiciona um novo paciente a partir do objeto `PacienteDTO` recebido no corpo da requisição.
4. **atualizarPaciente** (`PUT /pacientes/{id}`): Atualiza os dados de um paciente existente usando o ID. Retorna `404 Not Found` se o paciente não for encontrado.
5. **deletarPaciente** (`DELETE /pacientes/{id}`): Exclui um paciente pelo ID. Retorna `204 No Content` se a exclusão for bem-sucedida e `404 Not Found` se o paciente não existir.


### Criando o arquivo de configuração, que vai instanciar a implementação de gateway.

No pacote `main` crie a classe `BeansConfig.java` e adicione o código abaixo:

```java
package br.com.aygean.cleanarch.main;

import br.com.aygean.cleanarch.application.PacienteUseCase;
import br.com.aygean.cleanarch.gateway.PacienteGateway;
import br.com.aygean.cleanarch.infra.repository.PacienteRepository;
import br.com.aygean.cleanarch.infra.service.PacienteGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public PacienteGateway pacienteGateway(PacienteRepository pacienteRepository) {
        return new PacienteGatewayImpl(pacienteRepository);
    }

    @Bean
    public PacienteUseCase pacienteUseCase(PacienteGateway pacienteGateway) {
        return new PacienteUseCase(pacienteGateway);
    }
}
```

**Explicando a classe de configuração**

A classe `BeansConfig` é uma configuração de beans do Spring que fornece a criação e injeção das dependências principais para a aplicação. Com a anotação `@Configuration`, a classe é reconhecida pelo Spring como uma fonte de definições de beans.

1. **`pacienteGateway`**: Este método define um bean para a interface `PacienteGateway`. Ele retorna uma instância de `PacienteGatewayImpl`, que é a implementação concreta da interface, injetando o `PacienteRepository` necessário para acessar o banco de dados. Isso configura a camada de acesso a dados de maneira transparente para o Spring, permitindo a injeção de `PacienteGateway` em outras partes da aplicação.

2. **`pacienteUseCase`**: Este método configura um bean para `PacienteUseCase`, a classe que implementa a lógica de negócios para o recurso "paciente". Ele recebe a dependência `PacienteGateway`, que o Spring injeta automaticamente, e a utiliza para instanciar o `PacienteUseCase`.

Essa configuração permite que o Spring gerencie automaticamente as dependências da aplicação, garantindo que `PacienteUseCase` e `PacienteGateway` estejam prontos para uso com suas respectivas dependências já injetadas.

## Testando a aplicação
Os testes podem ser feitos através de algum cliente REST, mas irei disponibilizar as requisições `CURL` para o recurso `paciente`.

Aqui estão as requisições `cURL` para cada endpoint do `PacienteController` com os `DTOs` fornecidos.

### 1. Listar Todos os Pacientes

```bash
curl -X GET http://localhost:8080/pacientes -H "Accept: application/json"
```

### 2. Buscar Paciente por ID

```bash
curl -X GET http://localhost:8080/pacientes/{id} -H "Accept: application/json"
```

> Substitua `{id}` pelo ID do paciente desejado.

### 3. Adicionar um Novo Paciente

```bash
curl -X POST http://localhost:8080/pacientes \
-H "Content-Type: application/json" \
-d '{
  "nome": "João da Silva",
  "dataNascimento": "1985-08-15",
  "genero": "Masculino",
  "contato": {
    "telefone": "+5511999999999",
    "email": "joao.silva@example.com"
  },
  "endereco": {
    "rua": "Rua Exemplo",
    "numero": 123,
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01000-000"
  },
  "historicoMedico": [
    {
      "dataConsulta": "2023-01-20",
      "diagnostico": "Hipertensão",
      "tratamento": "Uso contínuo de medicamento",
      "observacoes": "Recomendado controle de pressão arterial mensal."
    },
    {
      "dataConsulta": "2022-12-15",
      "diagnostico": "Gripe",
      "tratamento": "Repouso e hidratação",
      "observacoes": "Consulta de retorno desnecessária."
    }
  ]
}'
```

### 4. Atualizar um Paciente Existente

```bash
curl -X PUT http://localhost:8080/pacientes/{id} \
-H "Content-Type: application/json" \
-d '{
  "nome": "João da Silva",
  "dataNascimento": "1985-08-15",
  "genero": "Masculino",
  "contato": {
    "telefone": "+5511999999999",
    "email": "joao.silva@example.com"
  },
  "endereco": {
    "rua": "Rua Exemplo",
    "numero": 123,
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01000-000"
  },
  "historicoMedico": [
    {
      "dataConsulta": "2023-01-20",
      "diagnostico": "Hipertensão",
      "tratamento": "Uso contínuo de medicamento",
      "observacoes": "Recomendado controle de pressão arterial mensal."
    },
    {
      "dataConsulta": "2022-12-15",
      "diagnostico": "Gripe",
      "tratamento": "Repouso e hidratação",
      "observacoes": "Consulta de retorno desnecessária."
    }
  ]
}'
```

> Substitua `{id}` pelo ID do paciente que deseja atualizar.

### 5. Deletar um Paciente

```bash
curl -X DELETE http://localhost:8080/pacientes/{id}
```

> Substitua `{id}` pelo ID do paciente que deseja deletar.

Essas requisições `cURL` permitem que você interaja com a API REST de pacientes usando os dados fornecidos em JSON, facilitando o teste de cada um dos métodos CRUD do `PacienteController`.

### Extra: Orquestração
![Orquestração](/images/f04_diagrama.png "Orquestração")

## Considerações finais

Para finalizar este tutorial, refletimos sobre o processo de construção de uma API REST robusta seguindo a Clean Architecture e utilizando Spring Boot. Passamos por conceitos importantes de organização de código e separação de responsabilidades, o que facilita a manutenção e escalabilidade da aplicação.

Neste guia, implementamos os princípios da arquitetura limpa para que cada camada do sistema — desde os casos de uso até a infraestrutura de persistência — fosse coesa e bem definida, contribuindo para um design claro e extensível. O uso de DTOs e de mapeamento entre entidades de domínio e entidades de persistência foi essencial para manter a integridade dos dados e garantir que as responsabilidades de cada componente estivessem isoladas.

Além disso, mostramos como o Spring Boot, com suas facilidades de configuração, e o PostgreSQL, como banco de dados gerenciado via Docker, podem ser integrados com segurança e eficiência. Com os exemplos práticos de configuração, conversão de objetos, e integração com o banco de dados, cobrimos a criação de um ambiente que utiliza princípios modernos e boas práticas.

Espero que este tutorial tenha sido esclarecedor e ofereça a você uma base sólida para projetos futuros com a Clean Architecture. Aplicar esses conceitos em suas implementações ajudará a construir soluções mais organizadas e resilientes.

**Atenciosamente `IratuaN Júnior`**.
