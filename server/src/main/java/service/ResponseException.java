package service;

public class ResponseException extends RuntimeException {
    private final int httpResponseCode;

    public ResponseException(String message, int httpResponseCode) {
        super(message);
        this.httpResponseCode = httpResponseCode;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public String toJson() {
        return "{ \"message\": \"" + getMessage() + "\" }";
    }
}
