package dashbah.hse.lexiscan.auth.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String username) {
        super(String.format("User not found with username = %s", username));
    }

}