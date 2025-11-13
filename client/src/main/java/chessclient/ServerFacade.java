package chessclient;

import chess.ChessGame;
import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import request.*;
import response.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResponse register(RegisterRequest request) {
        HttpRequest httpRequest = buildRequest("POST", "/user", request, null);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        HttpRequest httpRequest = buildRequest("POST", "/session", request, null);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, LoginResponse.class);
    }

    public LogoutResponse logout(LogoutRequest request) {
        HttpRequest httpRequest = buildRequest("DELETE", "/session", request, request.authToken());
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, LogoutResponse.class);
    }

    public void clearDatabase() {
        HttpRequest httpRequest = buildRequest("DELETE", "/db", null, null);
        var httpResponse = sendRequest(httpRequest);
        handleResponse(httpResponse, ClearDatabaseResponse.class);
    }

    public List<ChessGame> listGames() {
        return null;
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ClientException(ex.getMessage(), 500);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ClientException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new ClientException("Error", status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
