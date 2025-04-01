package gr.atc.urbreath.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import gr.atc.urbreath.controller.responses.BaseAppResponse;
import static gr.atc.urbreath.exception.CustomExceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR = "Validation failed";

    /*
     * Security Exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseAppResponse<Map<String, String>>> invalidSecurityExceptionHandler(
            @NotNull AccessDeniedException ex) {
        return new ResponseEntity<>(BaseAppResponse.error(
                "Invalid authorization parameters",
                "You don't have the rights to access the resource or check the JWT and CSRF Tokens"),
                HttpStatus.FORBIDDEN);
    }

    /*
     * Validation Exceptions
     */
    /*
     * Used for Request Body Validations in Requests
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseAppResponse<Map<String, String>>> validationExceptionHandler(
            @NotNull MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(BaseAppResponse.error(VALIDATION_ERROR, errors),
                HttpStatus.BAD_REQUEST);
    }

    /*
     * Validation fails on request parameters, path variables, or method arguments
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseAppResponse<Map<String, String>>> constraintValidationExceptionHandler(
            @NotNull ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return new ResponseEntity<>(BaseAppResponse.error(VALIDATION_ERROR, errors),
                HttpStatus.BAD_REQUEST);
    }

    /*
     * Handles missing request body or missing data in request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseAppResponse<String>> handleHttpMessageNotReadableExceptionHandler(
            HttpMessageNotReadableException ex) {
        String errorMessage = "Required request body is missing or invalid.";

        // Check if instance is for InvalidFormat Validation
        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx
                && invalidFormatEx.getTargetType().isEnum()) {
            String fieldName = invalidFormatEx.getPath().getFirst().getFieldName();
            String invalidValue = invalidFormatEx.getValue().toString();

            // Format the error message according to the Validation Type failure
            errorMessage = String.format("Invalid value '%s' for field '%s'. Allowed values are: %s",
                    invalidValue, fieldName, Arrays.stream(invalidFormatEx.getTargetType().getEnumConstants())
                            .map(Object::toString).collect(Collectors.joining(", ")));

        }
        // Generic error handling
        return ResponseEntity.badRequest().body(BaseAppResponse.error(VALIDATION_ERROR, errorMessage));
    }

    /*
     * Handles validation for Method Parameters
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseAppResponse<String>> validationExceptionHandler(
            @NonNull HandlerMethodValidationException ex) {
        return new ResponseEntity<>(BaseAppResponse.error(VALIDATION_ERROR, "Invalid input field"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseAppResponse<String>> handleGeneralExceptionHandler(@NotNull Exception ex) {
        return new ResponseEntity<>(BaseAppResponse.error("An unexpected error occurred", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<BaseAppResponse<String>> handlesConversionFailedException(
            @NotNull ConversionFailedException ex) {
        return new ResponseEntity<>(BaseAppResponse.error("Invalid data input format", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
     * Custom Exceptions
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<BaseAppResponse<String>> handlesResourceAlreadyExistsException(
            @NotNull ResourceAlreadyExistsException ex) {
        return new ResponseEntity<>(BaseAppResponse.error("Resource already exists", ex.getMessage()),
                HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseAppResponse<String>> handlesResourceNotFound(@NotNull ResourceNotFoundException ex) {
        return new ResponseEntity<>(BaseAppResponse.error("Resource not found", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataMappingException.class)
    public ResponseEntity<BaseAppResponse<String>> handlesDataMappingException(@NotNull DataMappingException ex) {
        return new ResponseEntity<>(BaseAppResponse.error("Data mapping error between Model-DTO", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<BaseAppResponse<String>> handlesWebClientRequestException(
            @NotNull WebClientRequestException ex) {
        return new ResponseEntity<>(BaseAppResponse.error("Internal proxy error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}