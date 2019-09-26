package cl.mguillen.beers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * APIExceptionsHandler es componente de la API REST que maneja las respuesta
 * que generan excepciones producidas al efectuar operaciones con esta API.
 *
 * Es capaz de manejar excepciones específicamente creadas para esta aplicación y
 * también optimizar la respuesta de las excepciones generadas por Spring JPA,
 * Bean Validations, JSONparser, Hibernate y otras componentes del framework Spring,
 * para omitir el mensaje del stack de error de nivel servidor y retornar un mensaje
 * interpretables por los consumidores.
 *
 * @author Manuel Guillén
 *
 */
@Slf4j
@ControllerAdvice
public class APIExceptionsHandler extends ResponseEntityExceptionHandler {


	/**
	 * Maneja respuesta de EntityIDNotFoundException creada especificamente para esta
	 * API, la cual se produce cuando no se encuentra una Entidad con el ID requerido.
	 * @param ex es una instancia de EntityIDNotFoundException
	 * @return Mensaje de Error
	 */
	@ResponseBody
	@ExceptionHandler({EntityIDNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleEntityIDNotFoundException(EntityIDNotFoundException ex) {
		return ex.getMessage();
	}

	/**
	 * Maneja respuesta de EmptyResultDataAccessException que genera Spring
	 * cuando no encuentra un recurso desde un Repository
	 * @param ex es una instancia de EmptyResultDataAccessException
	 * @return Mensaje de Error
	 */
	@ResponseBody
	@ExceptionHandler({EmptyResultDataAccessException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
		return ex.getMessage()
				.replace(
						"class com.mguillen.beers.domain.",
						""
				);
	}

	/**
	 * Maneja respuesta de ConstraintViolationException que genera Hibernate
	 * cuando existe inconsistencias en la data de una Entidad al intentar persistirla
	 * @param ex es una instancia de ConstraintViolationException
	 * @return Mensaje de Error en formato Json indicando causa de cada campo invalido
	 */
	@ResponseBody
	@ExceptionHandler({ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public Map handleConstraintViolationException(ConstraintViolationException ex) {
		return ex.getConstraintViolations()
				.stream()
				.collect(Collectors.toMap(
							ConstraintViolation::getPropertyPath,
							ConstraintViolation::getMessage,
							(path1, path2) -> path1
						)
				);
	}

	/**
	 * Maneja respuesta de MethodArgumentTypeMismatchException que genera Spring
	 * cuando no puede parsear un campo del Input request de acuerdo al tipo requerido
	 * @param ex es una instancia de MethodArgumentTypeMismatchException
	 * @return Mensaje de Error
	 */
	@ResponseBody
	@ExceptionHandler({MethodArgumentTypeMismatchException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		return ex.getName()
				+ " -> should by of type: "
				+ ex.getRequiredType().getName();
	}

	/**
	 * Maneja respuesta de MethodArgumentNotValidException que genera Spring
	 * cuando existen  uno o mas campos de un Input request (Json) inválidos
	 * @param ex es una instancia de ConstraintViolationException
	 * @return Mensaje de Error en formato Json indicando el error de cada campo.
	 */
	@Override
	protected ResponseEntity handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		return ResponseEntity
				.status(status)
				.headers(headers)
				.body(ex.getBindingResult()
						.getFieldErrors()
						.stream()
						.collect(
								Collectors.toMap(
										FieldError::getField,
										FieldError::getDefaultMessage,
										(field1, field2) -> field1
								)
						)
				);

	}


	/**
	 * Este método es un Hack para poder ocultar el stack completo de mensaje de error HttpMessageNotReadableException
	 * que se generan en capas internas del framework.
	 * @param ex la HttpMessageNotReadableException capturada
	 * @return mensaje de error
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		return ResponseEntity
				.status(status)
				.headers(headers)
				.body(getHttpMessageNotReadableExceptionMessageByExceptionType(ex));
	}

	/**
	 * Este método fue construido ya que no hay una forma mas elegante de momento para extraer un mensaje
	 * de error legible desde algunas Exceptions que provienen de capas internas de Spring Framewokr.
	 *
	 * Aquí se pueden incluir mecanismos manuales conocidos para descomponer un mensaje de HttpMessageNotReadableException
	 * y convertirlos en un mensaje legible, evitando  la salida del stack completo del error a sus
	 * consumidores.
	 *
	 * @param ex la HttpMessageNotReadableException capturada
	 * @return mensaje de error
	 */
	private static final String getHttpMessageNotReadableExceptionMessageByExceptionType(
			HttpMessageNotReadableException ex) {
		try {
			if (ex.getCause().getCause() instanceof DateTimeParseException) {
				//TODO: encontrar informacion del campo que produce el error
				return "This value: '"
						+ ((DateTimeParseException) ex.getCause().getCause()).getParsedString()
						+ "' : is not parsable as DATE. \n "
						+ "The expected format is DateTimeFormat.ISO.DATE";
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return ex.getMessage().split(":")[0];
	}

}
