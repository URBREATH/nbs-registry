package gr.atc.urbreath.validation;

import gr.atc.urbreath.validation.validators.NbsStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NbsStatusValidator.class)
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNbsStatus {
    String message() default "Invalid NBS Status. Only `To be Implemented`, `Under Implementation` and `Implemented` are permitted.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
