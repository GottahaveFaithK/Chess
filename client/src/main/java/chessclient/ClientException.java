package chessclient;

public class ClientException extends RuntimeException {
    private final int code;

    public ClientException(String message, int id) {
        super(message);
        this.code = id;
    }

    public int getCode() {
        return code;
    }

    public String toJson() {
        return "{ \"message\": \"" + getMessage() + "\" }";
    }
}
