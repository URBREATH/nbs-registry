package gr.atc.urbreath.validation.validators;

import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.validation.ValidNbsStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NbsStatusValidator implements ConstraintValidator<ValidNbsStatus, String> {

    @Override
    public boolean isValid(String status, ConstraintValidatorContext context){
        if (status == null) {
            return false;
        }

        return ClimateZone.fromString(status) != null;
    }
}
