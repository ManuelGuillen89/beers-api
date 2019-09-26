package cl.mguillen.beers.exceptions;

/**
 * Exception que se genera al no encontrar una entidad por su ID
 * requerida a través de un request a la API REST.
 *
 * @author Manuel Guillén
 */
public class EntityIDNotFoundException extends RuntimeException {
	public EntityIDNotFoundException(String entityName, int id) {
		super(entityName + " con ID " + id + " no existe");
	}
}