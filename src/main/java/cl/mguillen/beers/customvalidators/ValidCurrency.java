package cl.mguillen.beers.customvalidators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ValidCurrencyValidator.class)

public @interface ValidCurrency {
	String message() default "Divisa ${validatedValue} no está disponible";
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };
}
