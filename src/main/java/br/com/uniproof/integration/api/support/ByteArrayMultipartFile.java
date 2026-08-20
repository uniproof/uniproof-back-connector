package br.com.uniproof.integration.api.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link MultipartFile} respaldado por um array de bytes, usado para montar as
 * requisições multipart enviadas aos endpoints da plataforma Uniproof.
 *
 * <p>Substitui o {@code org.springframework.mock.web.MockMultipartFile}, que
 * vinha do {@code spring-test}: código de produção dependia de uma biblioteca
 * de teste, e por isso o {@code spring-boot-starter-test} precisava estar em
 * escopo {@code compile} neste módulo — o que arrastava JUnit, Mockito, AssertJ
 * e companhia para o classpath de runtime de todos os consumidores.</p>
 *
 * <p>O comportamento é o mesmo do {@code MockMultipartFile}: o conteúdo é lido
 * integralmente na construção, {@link #getOriginalFilename()} nunca devolve
 * {@code null} e {@link #getSize()} é o tamanho do array.</p>
 */
public class ByteArrayMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFilename;
    @Nullable
    private final String contentType;
    private final byte[] content;

    /**
     * @param name             nome do campo do formulário (ex.: {@code file})
     * @param originalFilename nome do arquivo; {@code null} vira string vazia
     * @param contentType      MIME type, opcional
     * @param content          conteúdo do arquivo
     */
    public ByteArrayMultipartFile(String name, @Nullable String originalFilename,
                                  @Nullable String contentType, @Nullable byte[] content) {
        Assert.hasLength(name, "O nome do campo não pode ser vazio");
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    /**
     * Lê o stream por inteiro. O stream <strong>não</strong> é fechado aqui —
     * quem o abriu continua responsável por fechá-lo, como acontecia com o
     * {@code MockMultipartFile}.
     */
    public ByteArrayMultipartFile(String name, @Nullable String originalFilename,
                                  @Nullable String contentType, InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, StreamUtils.copyToByteArray(contentStream));
    }

    /** Conveniência: lê o arquivo e deduz o MIME type pelo próprio arquivo. */
    public static ByteArrayMultipartFile of(String name, String originalFilename, Path arquivo) throws IOException {
        return new ByteArrayMultipartFile(name, originalFilename,
                Files.probeContentType(arquivo), Files.readAllBytes(arquivo));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() {
        return this.content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
}
