package ro.mediqueue.api.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super("%s not found with id: %s".formatted(resourceName, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
