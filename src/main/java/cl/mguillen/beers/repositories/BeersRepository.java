package cl.mguillen.beers.repositories;

import cl.mguillen.beers.domain.BeerItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeersRepository
		extends JpaRepository<BeerItem, Integer> {}
