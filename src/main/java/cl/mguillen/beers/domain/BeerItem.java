package cl.mguillen.beers.domain;

import cl.mguillen.beers.customvalidators.ValidCurrency;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@Entity
public class BeerItem {

	@Id
	@GeneratedValue(
			strategy = GenerationType.IDENTITY
	)
	private int id;

	@NotNull
	@NotEmpty
	@NotBlank
	private String name;

	@NotNull
	@NotEmpty
	@NotBlank
	private String brewery;

	@NotNull
	@NotEmpty
	@NotBlank
	private String country;

	@Positive
	private BigDecimal price;

	@NotNull
	@NotEmpty
	@NotBlank
	@ValidCurrency
	private String currency;

}
