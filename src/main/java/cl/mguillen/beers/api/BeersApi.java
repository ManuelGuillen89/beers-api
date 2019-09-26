package cl.mguillen.beers.api;


import cl.mguillen.beers.customvalidators.ValidCurrency;
import cl.mguillen.beers.domain.BeerItem;
import cl.mguillen.beers.exceptions.EntityIDNotFoundException;
import cl.mguillen.beers.externalresources.ExchangeCurrencyService2;
import cl.mguillen.beers.repositories.BeersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * BeersApi es una API REST para la entidad {@link BeerItem}
 * <p>
 * Soporta paginación y ordenamiento de resultados proporcionados por Spring Data Rest
 *
 * @author Manuel Guillén
 * @see <a href="https://docs.spring.io/spring-data/rest/docs/current/reference/html/#paging-and-sorting">Spring Docs</a>
 */
@RestController
@RequestMapping("/beers")
@RequiredArgsConstructor
@Validated
public class BeersApi {

	@Autowired
	ExchangeCurrencyService2 exchangeCurrencyService2;

	private final BeersRepository beersRepository;

	/**
	 * Encuentra todos los {@link BeerItem}
	 *
	 * @param pageable es opcional, instancia del tipo {@link Pageable}
	 * @return Page<Beers> objeto representación de una página
	 * que contiene un Set de {@link BeerItem}
	 * encontrados
	 */
	@GetMapping
	Page<BeerItem> findAll(Pageable pageable) {
		return beersRepository.findAll(pageable);
	}

	/**
	 * Encuentra un {@link BeerItem} a través de Id
	 *
	 * @param id es el ID del {@link BeerItem} requerido
	 * @return {@link BeerItem} encontrado
	 */
	@GetMapping("/{id}")
	BeerItem getById(@PathVariable int id) {
		return beersRepository
				.findById(id)
				.orElseThrow(
						() -> new EntityIDNotFoundException(
								"Cerveza", id)
				);
	}


	/**
	 * Obtiene el costo de una caja de {@link BeerItem} a través de Id
	 *
	 * @param id       es el ID del {@link BeerItem} requerido
	 * @param currency es la moneda en la que se necesita obtener el costo
	 * @return {@link BeerBoxDTO} que contiene valor de la caja de cervezas
	 */
	@GetMapping("/{id}/boxprice")
	BeerBoxDTO getBoxPrice(@PathVariable int id,
	                   @RequestParam @ValidCurrency String currency,
	                   @RequestParam(defaultValue = "6", required = false) int quantity) {
		final BeerItem beerItem = beersRepository
				.findById(id)
				.orElseThrow(
						() -> new EntityIDNotFoundException(
								"Cerveza", id)
				);

		return new BeerBoxDTO(
				String.valueOf(quantity),
				exchangeCurrencyService2
						.convert(
								beerItem.getCurrency(),
								beerItem.getPrice().multiply(BigDecimal.valueOf(quantity)),
								currency
						).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString(),
						currency
		);
	}


	/**
	 * Crea un {@link BeerItem}
	 *
	 * @param beerItem es una instancia de {@link BeerItem} sin Id
	 * @return {@link BeerItem} creado
	 */
	@PostMapping
	BeerItem addBeer(@RequestBody BeerItem beerItem) {
		return beersRepository.save(beerItem);
	}


}
