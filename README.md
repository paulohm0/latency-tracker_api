<h1 align="center">Latency Tracker API</h1>

API RESTful desenvolvida para processamento assíncrono de arquivos de log, focada na detecção e
exposição de gargalos de latência em sistemas distribuídos.

A aplicação lê periodicamente um arquivo de logs (`.xlsx`, `.csv` ou `.txt`), classifica os endpoints
por nível de criticidade com base no tempo de resposta e disponibiliza os resultados via API com suporte
a filtros e paginação.

A aplicação foi construída utilizando Java e Spring, focando em boas práticas, clean code
e padrões de arquitetura como Strategy e Factory.

---

## Estrutura do projeto

```
/src/main/java/paulodev/latencytracker_api
    /controller                  # Endpoints da API
    /converter                   # Conversores de parâmetros HTTP
    /dto                         # Objetos de transferência de dados
    /enums                       # Enums e regras de classificação
    /reader                      # Camada de leitura de arquivos
      /factory                   # Factory para seleção do reader correto
      /impl                      # Implementações por formato (xlsx, csv, txt)
    /scheduler                   # Agendamento da leitura periódica
    /service                     # Regras de negócio e auditoria
    LatencyTrackerApiApplication.java
```

---

## Tecnologias

- **Java 21**
- **Spring Boot**
- **Maven**
- **Lombok**
- **Apache POI + Streaming Reader**

---

## Práticas Adotadas

- **Strategy Pattern**: Cada formato de arquivo possui sua própria implementação de leitura, todas contratadas pela interface `LogFileReader`.
- **Factory Pattern**: `LogFileReaderFactory` identifica a extensão do arquivo e devolve a implementação correta.
- **Resiliência por linha**: Falhas em uma linha individual são informadas por logs sem interromper o processamento das demais.
- **Cache em memória com Thread safety**: Os resultados processados são mantidos em memória com `volatile` para garantir visibilidade entre a thread do scheduler e as threads do controller.

---

## Como rodar o projeto

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/paulohm0/latency-tracker_api.git
cd latency-tracker_api
```

2. Configure o `application.properties`:
```properties
# Caminho da pasta onde o arquivo de logs será buscado
backlog.folder.path=C:/seus-logs/

# Expressão cron para agendamento da leitura
# Exemplo abaixo executa a cada 1 minuto
scheduler.cron=0 * * * * *
```

3. Coloque seu arquivo de logs (`.xlsx`, `.csv` ou `.txt`) na pasta configurada em `backlog.folder.path`.

4. Suba a aplicação:
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Formatos de arquivo suportados

| Formato | Delimitador | Observação |
|---|---|---|
| `.xlsx` | — | Lido com StreamingReader para baixo consumo de memória |
| `.csv` | `,` | Leitura nativa Java com `BufferedReader` |
| `.txt` | `\t` (tabulação) | Leitura nativa Java com `BufferedReader` |

> O arquivo deve seguir a estrutura de colunas esperada pela aplicação. Consulte os arquivos de exemplo na seção abaixo.

---

## Níveis de Criticidade

A classificação é feita automaticamente com base no tempo de resposta de cada endpoint:

| Nível | Tempo de Resposta |
|---|---|
| `NORMAL` | Abaixo de 1000ms |
| `WARNING` | Entre 1000ms e 1999ms |
| `HIGH` | Entre 2000ms e 3999ms |
| `CRITICAL` | 4000ms ou mais |

Apenas os níveis `WARNING`, `HIGH` e `CRITICAL` são armazenados e expostos pela API.

---

## API Endpoints

### Consultar Gargalos de Latência

```http
GET /logs/audit
```

**Parâmetros opcionais:**

| Parâmetro | Tipo | Padrão | Descrição |
|---|---|---|---|
| `filter` | `String` | — | Filtra por nível: `WARNING`, `HIGH`, `CRITICAL` |
| `page` | `int` | `0` | Número da página |
| `size` | `int` | `20` | Itens por página |

**Exemplo de requisição:**
```http
GET /logs/audit?filter=CRITICAL&page=0&size=10
```

**Exemplo de resposta:**
```json
{
  "totalLogsAnalyzed": 100000,
  "totalFiltered": 38420,
  "filterApplied": "CRITICAL",
  "maxLatencyMs": 9998,
  "slowestEndpoint": "/api/v1/analytics/export",
  "slowestService": "ReportService",
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 3842
  },
  "bottleneckList": [
    {
      "endpoint": "/api/v1/analytics/export",
      "serviceName": "ReportService",
      "responseTimeMs": 9998,
      "criticalityLevel": "CRITICAL"
    }
  ]
}
```

> Quando nenhum filtro é aplicado, os campos `totalFiltered` e `filterApplied` são omitidos da resposta.

---

## Tratamento de Erros

Parâmetros inválidos retornam uma resposta padronizada:

```http
GET /logs/audit?filter=INVALIDO
```

```json
{
  "status": 400,
  "message": "Filtro inválido! Os valores aceitos são: NORMAL, WARNING, HIGH, CRITICAL."
}
```
