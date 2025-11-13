package response;

public record ListGamesResponse(int gameID, String gameName, String whiteUsername, String blackUsername) {
}
