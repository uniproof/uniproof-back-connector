package br.com.uniproof.integration.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static void main(String[] args) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		String teste = "rTeste";
		Map map = convertObjToMap(teste);

		String json = "{" +
				"\"id\": \"914f5ac3-3194-46ae-94fa-ba2f2a41ddee\"," +
				"\"lotId\": \"11987d8e-9a5c-48a6-b54d-e224b901b7b8\"," +
				"\"parentId\": null," +
				"\"protocol\":  null," +
				"\"protocol1\":  \"CDTSP 20220721105984\"," +
				"\"sellingPrice\": \"194.69\"," +
				"\"notaryPrice\": \"135.29\"," +
				"\"currentVersion\": 0," +
				"\"registeredVersion\": 2," +
				"\"archivedAt\": null," +
				"\"createdAt\": \"2022-05-25T19:36:43.191Z\"," +
				"\"updatedAt\": \"2022-05-31T20:21:33.508Z\"," +
				"\"event\": {" +
				"\"id\": 1909113," +
				"\"userId\": 477," +
				"\"ownerType\": \"LotItem\"," +
				"\"ownerId\": \"914f5ac3-3194-46ae-94fa-ba2f2a41ddee\"," +
				"\"status\": \"canceled\"," +
				"\"description\": \"<p><br></p>\\n<p><br></p>\"," +
				"\"manual\": true," +
				"\"createdAt\": \"2022-05-31T20:21:33.489Z\"," +
				"\"updatedAt\": \"2022-05-31T20:21:33.489Z\"" +
				"}," +
				"\"owner\": {" +
				"\"id\": 4," +
				"\"token\": \"217a43e6-50d6-4121-a8ae-394e9fa54a25\"," +
				"\"userId\": null," +
				"\"kind\": \"company\"," +
				"\"standard\": false," +
				"\"cityId\": 3550308," +
				"\"name\": \"Uniproof\"," +
				"\"fantasy\": \"Aureo Alto Astral\"," +
				"\"phone\": null," +
				"\"shortName\": \"Uniproof\"," +
				"\"cnpj\": \"29.383.051/0001-21\"," +
				"\"email\": null," +
				"\"onlyCommercialAddress\": false," +
				"\"endContractTerm\": null," +
				"\"startContractTerm\": null," +
				"\"comments\": null," +
				"\"createdAt\": \"2018-05-24T18:10:40.176Z\"," +
				"\"updatedAt\": \"2018-05-24T18:10:40.176Z\"" +
				"}," +
				"\"service\": {" +
				"\"id\": 62," +
				"\"name\": \"REGISTROS DE ATAS DIGITALIZADAS - RTD\"," +
				"\"alias\": \"Referente a Ata Condominial\"," +
				"\"description\": \"Registro de Atas Condominiais digitalizadas através de scanner ou foto.\"," +
				"\"fullDescription\": \"Registro de Atas Condominiais digitalizadas através de scanner ou foto.\"," +
				"\"helpUrl\": null," +
				"\"onDemand\": false," +
				"\"requireFile\": true," +
				"\"enableCity\": true" +
				"}," +
				"\"city\": {" +
				"\"id\": 5200050," +
				"\"name\": \"Abadia de Goiás\"," +
				"\"state\": \"GO\"" +
				"}," +
				"\"document\": {" +
				"\"id\": \"b17c09fa-966a-47f3-9735-51611fc1e81f\"," +
				"\"containerId\": null," +
				"\"lastEventId\": 1889195," +
				"\"extension\": \".json\"," +
				"\"mimetype\": \"application/json\"," +
				"\"name\": \"REGISTROS DE ATAS DIGITALIZADAS - RTD 25 de maio 16:36.json\"," +
				"\"pages\": 7," +
				"\"signed\": false," +
				"\"visible\": true," +
				"\"totalSize\": \"0\"," +
				"\"currentVersion\": 2," +
				"\"archivedAt\": null," +
				"\"createdAt\": \"2022-05-25T19:36:43.181Z\"," +
				"\"updatedAt\": \"2022-05-26T20:24:02.755Z\"," +
				"\"creator\": {" +
				"\"id\": 477," +
				"\"name\": \"Amanda Pereira de Freitas\"," +
				"\"shortName\": null," +
				"\"email\": \"adm@uniproof.com.br\"," +
				"\"cpf\": \"36124278812\"," +
				"\"origin\": null" +
				"}" +
				"}," +
				"\"attachments\": []," +
				"\"lot\": {" +
				"\"id\": \"11987d8e-9a5c-48a6-b54d-e224b901b7b8\"," +
				"\"lastEventId\": null," +
				"\"name\": null," +
				"\"name1\": \"Lote 25 de maio 16:36\"," +
				"\"description\": \"\"," +
				"\"containerId\": null," +
				"\"serviceId\": 62," +
				"\"cityId\": 5200050," +
				"\"priceId\": 1923," +
				"\"price\": \"194.69\"," +
				"\"createdAt\": \"2022-05-25T19:36:43.171Z\"," +
				"\"updatedAt\": \"2022-05-26T20:24:00.203Z\"," +
				"\"event\": null" +
				"}," +
				"\"blockchain\": {" +
				"\"id\": 63357," +
				"\"protocol\": \"0x863ecbd61966899d7207f1df879e8c3c3c4ba0ca0124de1982d096e7918d5215\"," +
				"\"validationCode\": \"0x863ecbd61966899d7207f1df879e8c3c3c4ba0ca0124de1982d096e7918d5215\"," +
				"\"notaryName\": \"https://blockscout.com/etc/mainnet/tx/0x863ecbd61966899d7207f1df879e8c3c3c4ba0ca0124de1982d096e7918d5215\"," +
				"\"registryDate\": \"2022-05-26T20:30:42.000Z\"," +
				"\"createdAt\": \"2022-05-25T19:36:43.191Z\"," +
				"\"updatedAt\": \"2022-05-26T20:36:02.977Z\"" +
				"}" +
				"}";
		String texto =
				"${owner.fantasy:-${owner.name}} - ${lot.name:-Avulso} - ${protocol:-Sem protocolo}\n" +
						"LotItem: ${id}\n${wallet.ownerType}_${container.id:-${owner.token}}\n" +
						"Este documento pode ser pago antes ou depois da data de seu vencimento. Prefira Pix.\n" +
						"O processo de REGISTRO SERÁ INICIADO QUANDO COMPENSADO, pelo banco.";


		System.out.println(replaceString(texto, json));
		System.out.println(StringUtils.getNestedProperty("owner.name1.alias.juca", convertObjToMap(json)));
	}

	private static final Pattern lookupPattern = Pattern.compile("\\$\\{([^\\}]+)\\}");

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
		ObjectMapper mapper = new ObjectMapper();
		if (object instanceof String) {
			try {
				return mapper.readValue((String) object, Map.class);
			} catch (Exception ex) {
				Map string =new HashMap<>();
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
