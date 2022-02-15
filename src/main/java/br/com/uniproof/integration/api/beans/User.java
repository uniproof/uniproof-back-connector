package br.com.uniproof.integration.api.beans;

import lombok.Data;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.Transient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class User {
    private String id;
    private String name;
    private String shortName;
    private String email;
    private String cpf;
    private String kind;

    @Transient
    public static String formatCPF(String cpf) {
        Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{2})");
        Matcher matcher = pattern.matcher(cpf);
        if (matcher.matches()) cpf = matcher.replaceAll("$1.$2.$3-$4");
        return cpf;
    }

    @Transient
    public String getFormatedCpf() {
        if (ObjectUtils.isEmpty(cpf)) {
            return null;
        }
        return formatCPF(cpf);
    }
}
