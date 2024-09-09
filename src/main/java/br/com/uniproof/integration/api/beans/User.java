package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.beans.Transient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class User extends Owner {
    private Long id;
    private String name;
    private String shortName;
    private String email;
    private String cpf;
    private String kind;
    private String login;
    private String origin;

    @Transient
    public static String formatCPF(String cpf) {
        Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{2})");
        Matcher matcher = pattern.matcher(cpf);
        if (matcher.matches()) cpf = matcher.replaceAll("$1.$2.$3-$4");
        return cpf;
    }

    @Transient
    public String getFormatedCpf() {
        if (cpf == null) {
            return null;
        }
        return formatCPF(cpf);
    }
}
