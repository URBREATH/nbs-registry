package gr.atc.urbreath.validation;

import gr.atc.urbreath.validation.validators.ClimateZoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ClimateZoneValidator.class)
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidClimateZone {
    String message() default "Invalid Climate Zone. Only `Atlantic`, `Boreal`, `Continental` and `Mediterranean` are permitted.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
