package chessclient;

public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }


    public String toJson() {
        return "{ \"message\": \"" + getMessage() + "\" }";
    }
}
