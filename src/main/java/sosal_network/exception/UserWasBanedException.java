package sosal_network.exception;

public class UserWasBanedException extends RuntimeException {

    public UserWasBanedException() {}

    public UserWasBanedException(String message) {
        super(message);
    }

    public UserWasBanedException(String message, Throwable cause) {
        super(message, cause);
    }
}
