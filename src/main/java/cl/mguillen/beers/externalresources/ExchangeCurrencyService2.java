package cl.mguillen.beers.externalresources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Este servicio obtiene  la taza de cambio de divisas desde
 * sistema externo: https://api.exchangerate-api.com/v4/latest/
 * <p>
 * Posee la capacidad de convertir entre una divisa y otra un valor dado.
 * <p>
 * Adicionalmente es capaz de mantener una lista estática con el nombre
 * de cada divisas soportada, para utilizarlas en validación de parametros.
 *
 * @author Manuel Guillén
 */
@Service
@Slf4j
public class ExchangeCurrencyService2 {

	@Value("${endpoint.api.exchangerate}")
	private static String URL_RATES;

	@Value("${filename.suportedcurrency}")
	private static String FILENAME_SUPORTED_CURRENCIES;

	private final RestTemplate restTemplate = new RestTemplate();

	public static Set<String> listOfValidCurrencies;

	@PostConstruct
	public void init() {
		log.info("INICIANDO SERVICIO "+this.getClass().getName());
		listOfValidCurrencies = getListOfValidCurrencies();
	}

	private Set<String> getListOfValidCurrencies() {
		return new HashSet<>(
				Arrays.asList(loadFileStringFromClassPath().split(","))
		);
	}

	private String loadFileStringFromClassPath(){
		String data = "";
		log.info("CARGANDO ARCHIVO: "+FILENAME_SUPORTED_CURRENCIES);
		ClassPathResource cpr = new ClassPathResource(FILENAME_SUPORTED_CURRENCIES);
		try {
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			data = new String(bdata, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public BigDecimal convert(String thisCurrency, BigDecimal thisAmount, String toThatCurrency) {
		return thisAmount.multiply(requestExchangeRateOf(thisCurrency, toThatCurrency));
	}

	private BigDecimal requestExchangeRateOf(String thisCurrency, String toThatCurrency) {
		return requestExchangeRate(
				UriComponentsBuilder.fromHttpUrl(URL_RATES+thisCurrency.toUpperCase())
		).get(toThatCurrency).getAsBigDecimal();
	}

	private JsonObject requestExchangeRate(UriComponentsBuilder builder) {
		return new JsonParser()
				.parse(restTemplate.getForObject(builder.toUriString(), String.class))
				.getAsJsonObject()
				.get("rates")
				.getAsJsonObject();
	}

}
