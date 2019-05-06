package pl.ewa.tess4j.db;

public class DBException extends RuntimeException {

    public DBException(Throwable cause) {
        super(cause);
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
