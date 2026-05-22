package pe.edu.utec.nutritrack.exception;

public class InvalidBatchDateException extends RuntimeException {
    public InvalidBatchDateException(String message) {
        super(message);
    }
}
