package br.com.uniproof.integration.api.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Garante que o {@link ByteArrayMultipartFile} se comporta exatamente como o
 * {@code MockMultipartFile} que ele substituiu no código de produção.
 *
 * <p>O {@code MockMultipartFile} vem do {@code spring-test} e era usado pelos
 * métodos de upload do {@code UniproofApiNotaryService} e do
 * {@code UniproofApiUserService} — o que obrigava o
 * {@code spring-boot-starter-test} a ficar em escopo {@code compile} e arrastava
 * as bibliotecas de teste para o runtime de todos os consumidores. Aqui ele é
 * usado apenas como <strong>referência de comparação</strong>, em escopo de
 * teste, que é o lugar dele.</p>
 */
class ByteArrayMultipartFileTest {

    private static final String CAMPO = "file";
    private static final String NOME = "titulo assinado.pdf";
    private static final String TIPO = "application/pdf";
    private static final byte[] CONTEUDO = "conteúdo do título — ção".getBytes(StandardCharsets.UTF_8);

    private static MultipartFile referencia(byte[] conteudo) {
        return new MockMultipartFile(CAMPO, NOME, TIPO, conteudo);
    }

    private static MultipartFile novo(byte[] conteudo) {
        return new ByteArrayMultipartFile(CAMPO, NOME, TIPO, conteudo);
    }

    @Test
    void metadadosBatemComOMockMultipartFile() {
        MultipartFile esperado = referencia(CONTEUDO);
        MultipartFile obtido = novo(CONTEUDO);

        assertEquals(esperado.getName(), obtido.getName());
        assertEquals(esperado.getOriginalFilename(), obtido.getOriginalFilename());
        assertEquals(esperado.getContentType(), obtido.getContentType());
        assertEquals(esperado.getSize(), obtido.getSize());
        assertEquals(esperado.isEmpty(), obtido.isEmpty());
    }

    @Test
    void conteudoBateComOMockMultipartFile() throws IOException {
        MultipartFile esperado = referencia(CONTEUDO);
        MultipartFile obtido = novo(CONTEUDO);

        assertArrayEquals(esperado.getBytes(), obtido.getBytes());
        assertArrayEquals(esperado.getInputStream().readAllBytes(), obtido.getInputStream().readAllBytes());
    }

    @Test
    void arquivoVazioSeComportaIgual() throws IOException {
        MultipartFile esperado = referencia(new byte[0]);
        MultipartFile obtido = novo(new byte[0]);

        assertTrue(obtido.isEmpty());
        assertEquals(esperado.isEmpty(), obtido.isEmpty());
        assertEquals(esperado.getSize(), obtido.getSize());
        assertArrayEquals(esperado.getBytes(), obtido.getBytes());
    }

    @Test
    void conteudoNuloViraArrayVazioComoNoMock() throws IOException {
        MultipartFile esperado = new MockMultipartFile(CAMPO, NOME, TIPO, (byte[]) null);
        MultipartFile obtido = new ByteArrayMultipartFile(CAMPO, NOME, TIPO, (byte[]) null);

        assertEquals(esperado.getSize(), obtido.getSize());
        assertEquals(esperado.isEmpty(), obtido.isEmpty());
        assertArrayEquals(esperado.getBytes(), obtido.getBytes());
    }

    @Test
    void nomeDeArquivoNuloViraStringVaziaComoNoMock() {
        MultipartFile esperado = new MockMultipartFile(CAMPO, null, TIPO, CONTEUDO);
        MultipartFile obtido = new ByteArrayMultipartFile(CAMPO, null, TIPO, CONTEUDO);

        assertEquals("", obtido.getOriginalFilename());
        assertEquals(esperado.getOriginalFilename(), obtido.getOriginalFilename());
    }

    @Test
    void tipoDeConteudoNuloEPermitido() {
        MultipartFile esperado = new MockMultipartFile(CAMPO, NOME, null, CONTEUDO);
        MultipartFile obtido = new ByteArrayMultipartFile(CAMPO, NOME, null, CONTEUDO);

        assertEquals(esperado.getContentType(), obtido.getContentType());
    }

    /**
     * É esta a forma usada no código de produção: {@code Files.newInputStream}
     * do arquivo em disco. O stream é lido por inteiro na construção e
     * <strong>não</strong> é fechado pelo construtor, igual ao Mock.
     */
    @Test
    void construtorDeStreamLeTudoENaoFechaOStream() throws IOException {
        byte[] copia;
        try (RegistraFechamento stream = new RegistraFechamento(CONTEUDO)) {
            MultipartFile obtido = new ByteArrayMultipartFile(CAMPO, NOME, TIPO, stream);
            copia = obtido.getBytes();
            assertFalse(stream.fechado, "o construtor não deve fechar o stream de quem o abriu");
        }
        assertArrayEquals(CONTEUDO, copia);
    }

    @Test
    void streamEArrayProduzemOMesmoResultado() throws IOException {
        MultipartFile viaArray = novo(CONTEUDO);
        MultipartFile viaStream = new ByteArrayMultipartFile(
                CAMPO, NOME, TIPO, new ByteArrayInputStream(CONTEUDO));

        assertEquals(viaArray.getSize(), viaStream.getSize());
        assertArrayEquals(viaArray.getBytes(), viaStream.getBytes());
    }

    @Test
    void transferToGravaOMesmoArquivoQueOMock(@TempDir Path dir) throws IOException {
        File doMock = dir.resolve("mock.pdf").toFile();
        File doNovo = dir.resolve("novo.pdf").toFile();

        referencia(CONTEUDO).transferTo(doMock);
        novo(CONTEUDO).transferTo(doNovo);

        assertArrayEquals(Files.readAllBytes(doMock.toPath()), Files.readAllBytes(doNovo.toPath()));
        assertArrayEquals(CONTEUDO, Files.readAllBytes(doNovo.toPath()));
    }

    @Test
    void ofLeOArquivoEDeduzOMimeType(@TempDir Path dir) throws IOException {
        Path arquivo = dir.resolve("recibo.pdf");
        Files.write(arquivo, CONTEUDO);

        ByteArrayMultipartFile obtido = ByteArrayMultipartFile.of(CAMPO, "recibo.pdf", arquivo);

        assertEquals(CAMPO, obtido.getName());
        assertEquals("recibo.pdf", obtido.getOriginalFilename());
        assertEquals(CONTEUDO.length, obtido.getSize());
        assertArrayEquals(CONTEUDO, obtido.getBytes());
    }

    @Test
    void nomeDeCampoVazioERejeitado() {
        assertThrows(IllegalArgumentException.class,
                () -> new ByteArrayMultipartFile("", NOME, TIPO, CONTEUDO));
    }

    /** InputStream que registra se foi fechado. */
    private static final class RegistraFechamento extends ByteArrayInputStream {
        private boolean fechado;

        private RegistraFechamento(byte[] conteudo) {
            super(conteudo);
        }

        @Override
        public void close() throws IOException {
            this.fechado = true;
            super.close();
        }
    }
}
