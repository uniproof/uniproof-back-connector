package br.com.uniproof.integration.api;

import br.com.uniproof.integration.api.beans.ApiResponse;
import br.com.uniproof.integration.api.beans.Attachment;
import br.com.uniproof.integration.api.beans.DownloadListItem;
import br.com.uniproof.integration.api.beans.LotItem;
import br.com.uniproof.integration.api.beans.LotItemReport;
import br.com.uniproof.integration.api.beans.Tag;
import br.com.uniproof.integration.api.beans.UniproofApiException;
import br.com.uniproof.integration.api.beans.User;
import br.com.uniproof.integration.api.client.UniproofLargeFilesNotaryClient;
import br.com.uniproof.integration.api.client.UniproofNotaryClient;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import br.com.uniproof.integration.api.service.UniproofApiNotaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integracao real com a API Uniproof (ambiente de stage), exercitando a fiacao
 * de producao: {@code UniproofClientConfig} com o interceptor de token, o
 * {@code UniproofApiErrorDecoder} e os wrappers do
 * {@link UniproofApiNotaryService}.
 *
 * <p>Desabilitado por padrao — exige credenciais. Para executar:</p>
 *
 * <pre>
 * mvn test -Dtest=UniproofNotaryClientIT -Duniproof.it=true \
 *   -Duniproof.it.restUrl=https://api-stage.uniproof.com.br \
 *   -Duniproof.it.notary=&lt;x-company-token&gt; \
 *   -Duniproof.it.email=&lt;login&gt; -Duniproof.it.pass=&lt;senha&gt; \
 *   -Duniproof.it.lotItemId=&lt;uuid&gt; -Duniproof.it.tagId=&lt;uuid&gt;
 * </pre>
 *
 * <p>Nao ha credencial escrita aqui: tudo vem por propriedade de sistema.</p>
 */
@EnabledIfSystemProperty(named = "uniproof.it", matches = "true")
@SpringBootTest(classes = UniproofNotaryClientIT.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UniproofNotaryClientIT {

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = UniproofNotaryClient.class)
    @Import({UniproofApiConfig.class, UniproofApiCoreService.class, UniproofApiNotaryService.class,
            UniproofLargeFilesNotaryClient.class})
    static class TestConfig {
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registro) {
        registro.add("uniproof.api.restUrl", () -> System.getProperty("uniproof.it.restUrl"));
        registro.add("uniproof.api.notary", () -> System.getProperty("uniproof.it.notary"));
        registro.add("uniproof.api.admin", () -> System.getProperty("uniproof.it.notary"));
        registro.add("uniproof.api.serviceUrl", () -> System.getProperty("uniproof.it.restUrl"));
        registro.add("uniproof.api.login.email", () -> System.getProperty("uniproof.it.email"));
        registro.add("uniproof.api.login.pass", () -> System.getProperty("uniproof.it.pass"));
    }

    @Autowired
    private UniproofApiNotaryService service;
    @Autowired
    private UniproofApiConfig config;

    private String token() {
        return config.getNotary();
    }

    private static String lotItemId() {
        return System.getProperty("uniproof.it.lotItemId");
    }

    private static String tagId() {
        return System.getProperty("uniproof.it.tagId");
    }

    private static final String ID_INEXISTENTE = "00000000-0000-0000-0000-000000000000";

    // =========================================================================
    // Caminho de erro — o motivo desta mudanca
    // =========================================================================

    /**
     * A tag informada ja esta no processo, entao a API responde 422 com
     * {@code {"statusCode":422,"message":"Tag já adicionada"}}. O erro tem de
     * chegar dentro do {@link ApiResponse}, sem excecao vazando.
     */
    @Test
    void erroDeNegocioVemDentroDoApiResponse() {
        ApiResponse<Tag> resposta = service.addTagToLotItem(lotItemId(), tagId(), token());

        assertFalse(resposta.isSuccess(), "esperado erro, veio sucesso: " + resposta.getData());
        assertEquals(422, resposta.getStatusCode());
        assertNotNull(resposta.getError(), "o corpo de erro deveria ter sido interpretado");
        assertEquals(422, resposta.getError().getStatusCode());
        assertNotNull(resposta.getErrorMessage());
        System.out.println("erro interpretado -> " + resposta.getStatusCode()
                + " / " + resposta.getErrorMessage());
    }

    // =========================================================================
    // Caminhos de sucesso das rotas novas
    // =========================================================================

    @Test
    void listaTagsComOsCamposVisuais() {
        ApiResponse<List<Tag>> resposta = service.getTags(null, null, null, 0, 5, token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        assertFalse(resposta.getData().isEmpty());
        Tag tag = resposta.getData().get(0);
        assertNotNull(tag.getId());
        assertNotNull(tag.getLabel());
        // Campos acrescentados ao bean depois de conferir o retorno real
        assertNotNull(tag.getIcon(), "icon deveria vir preenchido");
        assertNotNull(tag.getColor(), "color deveria vir preenchido");
        System.out.println("tag -> " + tag);
    }

    /** Valida a decisao de mapear a listagem como array puro, e nao envelope paginado. */
    @Test
    void listaProcessosComoArray() {
        ApiResponse<List<LotItem>> resposta =
                service.getLotItems(null, null, null, null, null, null, null, 0, 2, token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        assertFalse(resposta.getData().isEmpty());
        assertNotNull(resposta.getData().get(0).getId());
    }

    @Test
    void anexosAninhadosPelaApi() {
        ApiResponse<List<Attachment>> resposta =
                service.getNestedAttachmentFromLotItem(lotItemId(), token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        System.out.println("anexos raiz -> " + resposta.getData().size());
    }

    @Test
    void servicoDoProcesso() {
        ApiResponse<br.com.uniproof.integration.api.beans.Service> resposta =
                service.getServiceByLotItemId(lotItemId(), token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        assertNotNull(resposta.getData().getName());
        System.out.println("servico -> " + resposta.getData().getName());
    }

    /** Valida o envelope {@code {results, count}} do relatorio. */
    @Test
    void relatorioTemResultadosEContagem() {
        ApiResponse<LotItemReport> resposta = service.getLotItemsReport(
                java.util.Map.of("limit", 2, "offset", 0), token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        assertNotNull(resposta.getData().getResults(), "results nao deveria ser nulo");
        assertNotNull(resposta.getData().getCount(), "count nao deveria ser nulo");
        System.out.println("relatorio -> " + resposta.getData().getResults().size()
                + " linhas de " + resposta.getData().getCount());
    }

    @Test
    void listaDeDocumentosParaDownload() {
        ApiResponse<List<DownloadListItem>> resposta = service.getLotItemsDownloadList(
                java.util.Map.of("lotItemId", lotItemId()), token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
        if (!resposta.getData().isEmpty()) {
            DownloadListItem item = resposta.getData().get(0);
            assertNotNull(item.getName());
            assertNotNull(item.getLocation());
            System.out.println("documento -> " + item.getName());
        }
    }

    @Test
    void listaUsuarios() {
        ApiResponse<List<User>> resposta = service.getUsers(null, null, 0, 2, token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
    }

    @Test
    void listaLotes() {
        ApiResponse<List<br.com.uniproof.integration.api.beans.Lot>> resposta = service.getLots(token());

        assertTrue(resposta.isSuccess(), "falhou: " + resposta.getErrorMessage());
        assertNotNull(resposta.getData());
    }

    /**
     * Nem toda rota devolve erro para id inexistente: o {@code /service}
     * responde <strong>200 com corpo vazio</strong>. Fica registrado aqui para
     * quem consumir nao confundir "nao achou" com "falhou".
     */
    @Test
    void servicoDeProcessoInexistenteVem200ComCorpoVazio() {
        ApiResponse<br.com.uniproof.integration.api.beans.Service> resposta =
                service.getServiceByLotItemId(ID_INEXISTENTE, token());

        assertTrue(resposta.isSuccess(), "esperado 200: " + resposta.getErrorMessage());
        assertEquals(200, resposta.getStatusCode());
        assertNull(resposta.getData(), "corpo deveria vir vazio");
    }

    /**
     * Os metodos anteriores a esta mudanca continuam propagando excecao — mas
     * agora ela e {@code UniproofApiException}, com o corpo interpretado, em vez
     * de uma {@code FeignException} generica. E o ganho do ErrorDecoder para as
     * mais de 30 operacoes que ja existiam.
     */
    @Test
    void metodoAntigoLancaExcecaoTipadaComOCorpoInterpretado() {
        UniproofApiException erro = assertThrows(UniproofApiException.class,
                () -> service.getLotItemById(ID_INEXISTENTE, token()));

        assertEquals(404, erro.getStatusCode());
        assertNotNull(erro.getApiError(), "o corpo {statusCode,message} deveria ter sido interpretado");
        assertEquals(404, erro.getApiError().getStatusCode());
        assertNotNull(erro.getApiMessage());
        // Continua sendo FeignException: os catch (FeignException) existentes seguem valendo.
        assertInstanceOf(feign.FeignException.class, erro);
        System.out.println("excecao tipada -> " + erro.getStatusCode() + " / " + erro.getApiMessage());
    }
}
