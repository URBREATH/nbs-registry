package gr.atc.urbreath.validation.validators;

import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.validation.ValidClimateZone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClimateZoneValidator implements ConstraintValidator<ValidClimateZone, String> {

    @Override
    public boolean isValid(String zone, ConstraintValidatorContext context){
        if (zone == null) {
            return false;
        }

        return ClimateZone.fromString(zone) != null;
    }
}
