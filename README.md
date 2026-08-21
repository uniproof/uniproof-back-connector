# uniproof-back-connector

Biblioteca de acesso à **API da plataforma Uniproof**. Não é uma aplicação: não tem
`main`, não sobe servidor e não traz `application.yml`. Quem a consome é uma
aplicação Spring Boot, que fornece a configuração.

`br.com.uniproof.service:uniproof-back-connector:3.5.0-SNAPSHOT`

## O que ela oferece

Clientes Feign e serviços de fachada sobre os endpoints REST da plataforma:

| Serviço | Cobre |
|---|---|
| `UniproofApiNotaryService` | processos (lot items), anexos, documentos, eventos, preços, protocolo, storage/sha256 |
| `UniproofApiUserService` | tipos de anexo e de evento, carteiras, upload no lote, simulação de preço |
| `UniproofApiCoreService` | recursos de núcleo da plataforma |
| `UniproofApiSignatureService` | assinatura |

Os beans (`LotItem`, `Attachment`, `Document`, `Storage`, `Price`, `Event`…) ficam em
`br.com.uniproof.integration.api.beans`.

Dois utilitários que valem menção por serem usados de fora:

- `UniproofApiNotaryService.convertFlatAttachmentListToNestedList(List<Attachment>)` —
  monta a árvore de anexos (pai → filho → neto) a partir do `parentId`, que aponta
  para o **id do documento** do pai. Raízes com `removedAt` preenchido ficam de fora;
  descendentes removidos **não** são filtrados, quem consome precisa tratar.
- `ByteArrayMultipartFile` (`…api.support`) — implementação de `MultipartFile` usada
  nos uploads multipart.

## Configuração exigida da aplicação

```yaml
uniproof:
  api:
    restUrl: https://api.uniproof.com.br
    notary: <token da serventia>
    admin: <token administrativo>
```

`UniproofApiConfig` expõe `getNotary()` / `getAdmin()`, que é o token passado no
header `X-Company-Token` de cada chamada.

## Notas de comportamento

- **Uploads acima de 40 MB** seguem por um cliente Feign separado
  (`uniproofLargeFilesNotaryClient`); abaixo disso, pelo caminho multipart normal.
- **`uploadAttachmentToLotItem` engole a exceção e devolve `null`** em caso de falha.
  A plataforma responde HTTP 409 quando o **SHA-256 do conteúdo** já consta no
  processo (a mensagem cita o nome do arquivo, mas a chave é o conteúdo), e pelo
  retorno não há como distinguir 409 de falha de rede. Quem chama deve deduplicar
  antes, via `getLotItemBySha256`.

## Dependências de teste

O `spring-boot-starter-test` está em escopo `test`, como deve ser numa biblioteca.
Antes disso ele estava em `compile` porque o código de produção usava
`MockMultipartFile` (do `spring-test`) para montar os uploads — o que arrastava
JUnit, Mockito, AssertJ, Hamcrest e companhia para o **runtime de todos os
consumidores**. O `ByteArrayMultipartFile` substituiu o mock com comportamento
idêntico, verificado em `ByteArrayMultipartFileTest`, que compara as duas
implementações campo a campo.

## Build

```bash
mvn clean install
```
