package br.com.uniproof.integration.api.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Todo bean deste pacote atravessa um decoder do Feign, e nenhum decoder consegue instanciar uma
 * classe sem construtor vazio.
 *
 * <p>Escrito depois de uma quebra em runtime: {@code Price} tinha {@code @Data @Builder} e mais
 * nada. O {@code @Builder} faz o Lombok deixar de gerar o construtor vazio, e o Jackson 3 que o
 * Spring Boot 4 usa no Feign recusou a classe com
 * {@code Cannot construct instance of Price (no Creators, like default constructor, exist)}. Todas
 * as emissoes de boleto do caminho normal falharam. Antes do Boot 4 passava despercebido.</p>
 *
 * <p>O teste varre o pacote inteiro em vez de olhar os beans conhecidos porque o risco nao esta
 * nos que existem hoje: esta no proximo bean que alguem escrever com {@code @Builder} e sem
 * {@code @NoArgsConstructor}. E este componente e consumido por outros servicos da casa, entao a
 * quebra nao aparece necessariamente aqui.</p>
 */
class BeansTemConstrutorVazioTest {

    private List<Class<?>> beansDoPacote() throws Exception {
        String pacote = getClass().getPackageName();
        // Pelo code source de um bean, e nao pelo ClassLoader: o classpath do surefire tem
        // test-classes na frente, e perguntar pelo pacote traria a pasta deste teste, onde nao
        // existe bean nenhum e a varredura passaria vazia.
        URL raiz = Price.class.getProtectionDomain().getCodeSource().getLocation();
        assertThat(raiz).as("sem code source nao ha o que varrer").isNotNull();

        File diretorio = new File(new File(raiz.toURI()), pacote.replace('.', '/'));
        assertThat(diretorio).as("as classes compiladas dos beans deveriam estar em %s", diretorio)
                .isDirectory();
        List<Class<?>> beans = new ArrayList<>();
        for (File arquivo : Arrays.stream(diretorio.listFiles()).sorted().toList()) {
            String nome = arquivo.getName();
            // Classes internas (Builder, e as $1 de enums) chegam como arquivos proprios e nao sao
            // beans de transporte.
            if (!nome.endsWith(".class") || nome.contains("$")) {
                continue;
            }
            Class<?> classe = Class.forName(pacote + "." + nome.substring(0, nome.length() - ".class".length()));
            if (classe.isEnum() || classe.isInterface() || classe.isAnnotation() || classe.isRecord()) {
                continue;
            }
            if (naoEAlvoDeDesserializacao(classe)) {
                continue;
            }
            beans.add(classe);
        }
        return beans;
    }

    /**
     * Nem tudo que mora neste pacote e corpo de resposta. As excecoes sao nominais de proposito:
     * uma regra generica ("pule quem so tem construtor privado") deixaria passar justamente o
     * bean quebrado que este teste existe para pegar.
     */
    private boolean naoEAlvoDeDesserializacao(Class<?> classe) {
        // Excecao: o Feign a constroi no ErrorDecoder, nunca vem de JSON.
        if (Throwable.class.isAssignableFrom(classe)) {
            return true;
        }
        // Envelope de resultado montado pelos factory methods ok()/fail(). O que e desserializado
        // e o conteudo dele, nao ele.
        return classe.equals(ApiResponse.class);
    }

    @Test
    @DisplayName("o pacote de beans nao esta vazio - se estiver, a varredura nao esta varrendo nada")
    void avarreduraEncontraBeans() throws Exception {
        // Sem esta trava, um erro de caminho transformaria o teste seguinte em sempre verde.
        assertThat(beansDoPacote()).hasSizeGreaterThan(20);
    }

    @Test
    @DisplayName("todo bean tem construtor vazio, entao qualquer decoder consegue instancia-lo")
    void todoBeanTemConstrutorVazio() throws Exception {
        List<String> semConstrutorVazio = new ArrayList<>();

        for (Class<?> bean : beansDoPacote()) {
            boolean tem = Arrays.stream(bean.getDeclaredConstructors())
                    .anyMatch(construtor -> construtor.getParameterCount() == 0);
            if (!tem) {
                semConstrutorVazio.add(bean.getSimpleName());
            }
        }

        assertThat(semConstrutorVazio)
                .as("estes beans nao podem ser desserializados. Com @Builder o Lombok para de gerar "
                        + "o construtor vazio: anote tambem com @NoArgsConstructor e "
                        + "@AllArgsConstructor, que o @Builder precisa")
                .isEmpty();
    }
}
