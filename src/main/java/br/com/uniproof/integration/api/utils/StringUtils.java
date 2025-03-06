package br.com.uniproof.integration.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern lookupPattern = Pattern.compile("\\$\\{([^\\}]+)\\}");

    public static void main(String[] args) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String teste = "rTeste";
        Map map = convertObjToMap(teste);
        String json = """
                {
                	"id": "7738edfa-7862-4058-b0f6-7c9aabbd2a78",
                	"lotId": "e9cf4553-2a9e-4a41-a1eb-0ba546efcb1e",
                	"creatorId": 2,
                	"couponId": null,
                	"currentStatus": "pendent",
                	"notaryId": 2,
                	"hasNotification": false,
                	"notificationCount": 0,
                	"parentId": null,
                	"protocol": null,
                	"startedAt": "2025-02-13T19:17:30.133Z",
                	"sellingPrice": 0,
                	"notaryPrice": 0,
                	"currentVersion": 0,
                	"registeredVersion": null,
                	"archivedAt": null,
                	"currentStatusAt": "2025-02-20T21:16:32.780Z",
                	"createdAt": "2025-02-13T19:14:53.238Z",
                	"updatedAt": "2025-02-27T18:45:59.065Z",
                	"event": {
                		"id": 8029421,
                		"userId": 2,
                		"notaryId": null,
                		"ownerType": "LotItem",
                		"ownerId": "7738edfa-7862-4058-b0f6-7c9aabbd2a78",
                		"status": "pendent",
                		"description": "",
                		"archivedAt": null,
                		"archiverId": null,
                		"createdAt": "2025-02-20T21:16:32.780Z",
                		"updatedAt": "2025-02-20T21:16:32.780Z"
                	},
                	"owner": {
                		"id": 4,
                		"token": "217a43e6-50d6-4121-a8ae-394e9fa54a25",
                		"userId": null,
                		"kind": "company",
                		"standard": false,
                		"cityId": 3550308,
                		"name": "Uniproof",
                		"fantasy": null,
                		"phone": null,
                		"shortName": "Uniproof",
                		"cnpj": "29.383.051/0001-21",
                		"email": null,
                		"onlyCommercialAddress": false,
                		"endContractTerm": null,
                		"startContractTerm": null,
                		"comments": null,
                		"companyId": null,
                		"superCompany": false,
                		"notaryIds": [],
                		"companyIds": [],
                		"createdAt": "2018-05-24T18:10:40.176Z",
                		"updatedAt": "2018-05-24T18:10:40.176Z",
                		"entity": "Company"
                	},
                	"service": {
                		"id": 3,
                		"name": "REGISTRO DE DOCUMENTOS NATO DIGITAIS JÁ ASSINADOS - RTD",
                		"identifier": "external",
                		"alias": "Referente ao Registro do Contrato",
                		"description": "Registro de documentos nato digitais já contém assinaturas eletrônicas e ou digitais (ICP) das partes.",
                		"fullDescription": "Registro de documentos nato digitais já contém assinaturas eletrônicas e ou digitais (ICP) das partes.",
                		"editUrl": "https://df-app-stage.uniproof.com.br/dynamic_form/2d047129-25c7-4b6b-ad6e-f5fe52f2f76d/{lot_id}/edit",
                		"viewUrl": "https://df-app-stage.uniproof.com.br/dynamic_form/2d047129-25c7-4b6b-ad6e-f5fe52f2f76d/{lot_id}/view",
                		"validateUrl": "https://df-api-stage.uniproof.com.br/templates/2d047129-25c7-4b6b-ad6e-f5fe52f2f76d/forms/{lot_id}/status",
                		"dataUrl": "https://df-api-stage.uniproof.com.br/templates/2d047129-25c7-4b6b-ad6e-f5fe52f2f76d/forms/{lot_id}",
                		"copyUrl": "https://df-api-stage.uniproof.com.br/forms/2d047129-25c7-4b6b-ad6e-f5fe52f2f76d/copy",
                		"helpUrl": null,
                		"active": true,
                		"onlyPdf": false,
                		"onDemand": true,
                		"requireFile": true,
                		"requireSignature": false,
                		"options": {
                			"formWidth": "900",
                			"formHeight": "750",
                			"formMarginTop": "-10",
                			"formMarginLeft": "0",
                			"folderDisclamer": "Atenção: Preenchendo o campo CPF/CNPJ do Centro de custo, o faturamento será feito para este Centro de Custo indicado. Do contrário será faturado para a empresa que está solicitando o pedido",
                			"formMarginRight": "0",
                			"formMarginBottom": "0"
                		},
                		"initialStatus": "created"
                	},
                	"city": {
                		"id": 5200050,
                		"name": "Abadia de Goiás",
                		"state": "GO"
                	},
                	"document": {
                		"id": "2bd62bdb-6de5-4867-93c2-66a1f402c87d",
                		"containerId": "06e76c96-1cfe-4ff1-bd14-41853450572d",
                		"lastEventId": 8029133,
                		"extension": ".json",
                		"mimetype": "application/json",
                		"name": "REGISTRO DE DOCUMENTOS NATO DIGITAIS JÁ ASSINADOS - RTD 13 de fevereiro 16:14.json",
                		"pages": 0,
                		"signed": false,
                		"visible": true,
                		"totalSize": "0",
                		"currentVersion": 0,
                		"archivedAt": null,
                		"createdAt": "2025-02-13T19:14:53.211Z",
                		"updatedAt": "2025-02-13T19:14:53.283Z",
                		"creator": {
                			"id": 2,
                			"blockCompany": false,
                			"name": "User 2",
                			"shortName": null,
                			"email": "user2@uniproof.com.br",
                			"login": "user2@uniproof.com.br",
                			"cpf": "00000000000",
                			"origin": null,
                			"skipPasswordExpiration": false,
                			"sso": false,
                			"expiresPasswordAt": null,
                			"otpBase32": "MO6PIEWEJZREPFSUKNC2ZUDU",
                			"otpEnabled": false
                		}
                	},
                	"attachments": [
                		{
                			"id": "71ce520b-bbb5-4377-b6c1-579eb04671d7",
                			"ownerType": "LotItem",
                			"ownerId": "7738edfa-7862-4058-b0f6-7c9aabbd2a78",
                			"documentId": "813f9daa-f2a2-4231-9982-f7d0fc1f066f",
                			"attachmentTypeId": 7,
                			"parentId": null,
                			"currentVersion": 1,
                			"removedAt": null,
                			"expiresAt": null,
                			"createdAt": "2025-02-13T19:17:22.672Z",
                			"updatedAt": "2025-02-13T19:17:22.672Z",
                			"document": {
                				"id": "813f9daa-f2a2-4231-9982-f7d0fc1f066f",
                				"containerId": "06e76c96-1cfe-4ff1-bd14-41853450572d",
                				"lastEventId": null,
                				"extension": ".pdf",
                				"mimetype": "application/pdf",
                				"name": "assinatura_egov.pdf",
                				"pages": 1,
                				"signed": false,
                				"visible": true,
                				"totalSize": "304660",
                				"currentVersion": 1,
                				"archivedAt": null,
                				"createdAt": "2025-02-13T19:17:22.625Z",
                				"updatedAt": "2025-02-13T19:17:22.717Z"
                			},
                			"attachmentType": {
                				"id": 7,
                				"name": "attachment",
                				"label": "Anexo",
                				"notaryLabel": "Anexo",
                				"active": true,
                				"deletable": true,
                				"visible": true,
                				"forwardable": false,
                				"registered": false,
                				"icon": "paperclip",
                				"color": "rgb(59, 114, 231)",
                				"expirationDays": null,
                				"sortOrder": 0
                			}
                		},
                		{
                			"id": "86226517-137f-44cb-a30e-970293541085",
                			"ownerType": "LotItem",
                			"ownerId": "7738edfa-7862-4058-b0f6-7c9aabbd2a78",
                			"documentId": "4d9ba5ff-8cae-4b7c-832d-248594b8b636",
                			"attachmentTypeId": 10,
                			"parentId": "813f9daa-f2a2-4231-9982-f7d0fc1f066f",
                			"currentVersion": 1,
                			"removedAt": "2025-02-20T21:06:29.280Z",
                			"expiresAt": null,
                			"createdAt": "2025-02-13T20:52:02.447Z",
                			"updatedAt": "2025-02-20T21:06:29.285Z",
                			"document": {
                				"id": "4d9ba5ff-8cae-4b7c-832d-248594b8b636",
                				"containerId": "06e76c96-1cfe-4ff1-bd14-41853450572d",
                				"lastEventId": null,
                				"extension": ".pdf",
                				"mimetype": "application/pdf",
                				"name": "uniproof-assinatura_egov.pdf",
                				"pages": 2,
                				"signed": false,
                				"visible": false,
                				"totalSize": "521574",
                				"currentVersion": 1,
                				"archivedAt": null,
                				"createdAt": "2025-02-13T20:52:02.418Z",
                				"updatedAt": "2025-02-13T20:52:02.480Z"
                			},
                			"attachmentType": {
                				"id": 10,
                				"name": "uniproof_signed",
                				"label": "Assinado Uniproof",
                				"notaryLabel": "Assinado Uniproof",
                				"active": true,
                				"deletable": false,
                				"visible": false,
                				"forwardable": true,
                				"registered": false,
                				"icon": "signature",
                				"color": "rgb(59, 114, 231)",
                				"expirationDays": null,
                				"sortOrder": 30
                			}
                		}
                	],
                	"lot": {
                		"id": "e9cf4553-2a9e-4a41-a1eb-0ba546efcb1e",
                		"lastEventId": 8029131,
                		"creatorId": 2,
                		"name": "Proc: 1739474093152",
                		"description": "",
                		"containerId": "06e76c96-1cfe-4ff1-bd14-41853450572d",
                		"serviceId": 3,
                		"cityId": 5200050,
                		"priceId": 1921,
                		"price": "0.00",
                		"createdAt": "2025-02-13T19:14:53.157Z",
                		"updatedAt": "2025-02-27T18:45:59.022Z",
                		"event": {
                			"id": 8029131,
                			"userId": 2,
                			"notaryId": null,
                			"ownerType": "Lot",
                			"ownerId": "e9cf4553-2a9e-4a41-a1eb-0ba546efcb1e",
                			"status": "draft",
                			"description": null,
                			"archivedAt": null,
                			"archiverId": null,
                			"createdAt": "2025-02-13T19:14:53.193Z",
                			"updatedAt": "2025-02-13T19:14:53.193Z"
                		}
                	},
                	"container": {
                		"id": "06e76c96-1cfe-4ff1-bd14-41853450572d",
                		"name": "2342 - Morada da Serra",
                		"description": "23423423424234",
                		"sequentialId": 7155,
                		"tooltip": null
                	},
                	"blockchain": null,
                	"tags": []
                }
                """;

        String texto1 =
                "${owner.fantasy:-${owner.name}} - ${lot.name:-Avulso} - ${protocol:-Sem protocolo}\n" +
                        "LotItem: ${id}\n${wallet.ownerType}_${container.id:-${owner.token}}\n" +
                        "Este documento pode ser pago antes ou depois da data de seu vencimento. Prefira Pix.\n" +
                        "O processo de REGISTRO SERÁ INICIADO QUANDO COMPENSADO, pelo banco.";

        String texto =
                "${owner.fantasy:-${owner.name}} - ${lot.name:- }\n" +
                        "LotItem: ${id}\n"+
                        "${container.name}\n" +
                        "Este documento pode ser pago antes ou depois da data de seu vencimento. Prefira Pix.";
        //+                        "O processo de REGISTRO SERÁ INICIADO QUANDO COMPENSADO, pelo banco.";

        System.out.println(replaceString(texto, json));


        String[] test = {
           "2342 - Morada da Serra",
           "B2Br - Empresa X",
           "2-Morada da Serra",
           "Morada da Serra - office",
                "31.454.503/0001-60 - Juca",
                "010001 0599-00 - 010006 0597-00 - LETICIA DE PAULA SOUZA MORAES",
                "CCE048237-7 REQUERIMENTO - 90.608.084/0001-33",
                "689 - CONDOMINIO RESIDENCIAL GARDEN - CNPJ:58.223.018/0001-32"
        } ;

        System.out.println("\n\n\n");
        for (String s : test) {
            System.out.println(s + " /-> " + s.replaceFirst("^[0-9,. \\\\/-]*",""));
        }
        //System.out.println(StringUtils.getNestedProperty("owner.name1.alias.juca", convertObjToMap(json)));
    }

    public static String replaceString(String input, Object context) throws JsonProcessingException {
        Map<String, Object> map = convertObjToMap(context);
        return replaceString(input, map);
    }

    public static String replaceString(String input, Object context1, Object context2) throws JsonProcessingException {
        Map<String, Object> map1 = convertObjToMap(context1);
        Map<String, Object> map2 = convertObjToMap(context2);
        map1.putAll(map2);
        return replaceString(input, map1);
    }

    public static Map<String, Object> convertObjToMap(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (object instanceof String) {
            try {
                return mapper.readValue((String) object, Map.class);
            } catch (Exception ex) {
                Map string = new HashMap<>();
                string.put("texto", object);
                return string;
            }
        } else {
            return mapper.readValue(mapper.writeValueAsString(object), Map.class);
        }
    }

    public static String replaceString(String input, Map<String, Object> context) {
        int position = 0;
        StringBuilder result = new StringBuilder();

        Matcher m = lookupPattern.matcher(input);
        while (m.find()) {
            result.append(input, position, m.start());
            String xpath = m.group(1);
            String defaultNotFound = "";
            if (xpath.contains(":-")) {
                String[] split = xpath.split(":-");
                xpath = split[0];
                defaultNotFound = split[1];
            }
            String afterSearch = null;

            try {
                afterSearch = StringUtils.getNestedProperty(xpath, context);
            } catch (Exception ignored) {
            }

            if (!ObjectUtils.isEmpty(afterSearch)) {
                result.append(afterSearch);
            } else {
                result.append(defaultNotFound);
            }
            position = m.end();
        }

        if (position == 0) {
            return input;
        } else {
            result.append(input.substring(position));
            String resultado = result.toString();
            Matcher sub = lookupPattern.matcher(resultado);
            if (sub.find()) {
                return replaceString(resultado, context);
            }
            return result.toString().replaceAll("}", "");
        }
    }

    public static String getNestedProperty(String prop, Object object) {
        if (ObjectUtils.isEmpty(prop) || !(object instanceof Map)) {
            return (object != null ? object.toString() : null);
        }

        Map<String, Object> map = (Map<String, Object>) object;
        String firstPart = prop;
        String secondPart = "";
        if (prop.contains(".")) {
            firstPart = prop.substring(0, prop.indexOf("."));
            secondPart = prop.substring(prop.indexOf(".") + 1);
        }
        return getNestedProperty(secondPart, map.get(firstPart));
    }
}
