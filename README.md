# Tutorial de clean architecture utilizando spring boot

- Autor: Iratuã Júnior
- Data: 07/11/2024
- Versão do springboot: 3.3.5
- Versão jdk: 18
- [Respositorio no github](https://github.com/iratuan/tutorial-springboot-jwt)

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
**Resumo sobre a Arquitetura Limpa:**

A Arquitetura Limpa, proposta por Robert C. Martin (conhecido como Uncle Bob), é uma abordagem de design de software que enfatiza a independência e a manutenção do código. Seu principal objetivo é separar a lógica de negócio (regras e entidades) dos detalhes de implementação (frameworks, bancos de dados, interfaces de usuário), criando camadas distintas que se comunicam através de interfaces. Isso facilita a testabilidade, a escalabilidade e a adaptabilidade da aplicação, permitindo que mudanças em uma camada não afetem as demais.

**Tabela explicativa dos pacotes:**

| **Pacote**     | **Descrição**                                                                                                                                                                                                                                                                                             |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **domain**     | Contém as **entidades** e **regras de negócio** fundamentais da aplicação. Representa o núcleo do sistema, independente de detalhes técnicos ou frameworks. Aqui estão as classes que modelam os conceitos centrais do domínio, focando na lógica pura de negócio.                                          |
| **application**| Abriga os **casos de uso** da aplicação, que orquestram as interações entre as entidades do domínio para atender às funcionalidades requisitadas. O subpacote **usecase** contém a lógica que define como os dados são processados e como as operações de negócio são executadas, sem depender de detalhes externos. |
| **gateway**    | Define as **interfaces** que permitem a comunicação entre a camada de aplicação (casos de uso) e os sistemas externos, como bancos de dados, serviços web, ou outros meios de persistência e comunicação. Essa camada abstrai os detalhes de implementação, promovendo a inversão de dependências.          |
| **infra**      | Implementa os detalhes de infraestrutura e tecnologia, como acesso a bancos de dados, configurações de frameworks, serviços externos e outras dependências técnicas. Aqui estão as classes concretas que realizam as operações definidas pelas interfaces no pacote **gateway**.                               |
| **main**       | Contém o código de inicialização da aplicação, configurações gerais e a composição das dependências. É onde o framework (por exemplo, Spring Boot) é utilizado para montar o aplicativo, iniciar o servidor e conectar todas as camadas, respeitando as diretrizes da arquitetura limpa.                        |

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

    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private Contato contato;
    private Endereco endereco;
    private List<HistoricoMedico> historicoMedico;

    public Paciente(String nome, LocalDate dataNascimento, String genero, Contato contato, Endereco endereco, List<HistoricoMedico> historicoMedico) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.contato = contato;
        this.endereco = endereco;
        this.historicoMedico = historicoMedico;
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

    // Getters e Setters
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

    // Getters e Setters
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
