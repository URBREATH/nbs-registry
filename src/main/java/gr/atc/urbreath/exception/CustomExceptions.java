package gr.atc.urbreath.exception;

public class CustomExceptions {

    private CustomExceptions() {}

    public static class ResourceAlreadyExistsException extends RuntimeException {
        public ResourceAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class DataMappingException extends RuntimeException {
        public DataMappingException(String message) {
            super(message);
        }
    }

    public static class WebClientRequestException extends RuntimeException {
        public WebClientRequestException(String message) {
            super(message);
        }
    }
}
