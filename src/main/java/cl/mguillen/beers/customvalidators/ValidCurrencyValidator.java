package cl.mguillen.beers.customvalidators;

import cl.mguillen.beers.externalresources.ExchangeCurrencyService2;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, String> {
   public void initialize(ValidCurrency constraint) {
   }

   public boolean isValid(String currencyStr, ConstraintValidatorContext context) {
      return Objects.isNull(currencyStr) ||
          ExchangeCurrencyService2.listOfValidCurrencies.contains(currencyStr.toUpperCase());
   }
}
