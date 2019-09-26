package cl.mguillen.beers.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeerBoxDTO {
	private String pack;
	private String totalPrice;
	private String currency;
}
