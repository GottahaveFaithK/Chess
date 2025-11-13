package chessclient;

public class ClientException extends RuntimeException {
    private final int id;

    public ClientException(String message, int id) {
        super(message);
        this.id = id;
    }


    public String toJson() {
        return "{ \"message\": \"" + getMessage() + "\" }";
    }
}
