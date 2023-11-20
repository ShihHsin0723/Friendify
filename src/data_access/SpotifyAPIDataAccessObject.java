package data_access;

// added the json jar library in order to import this
import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class SpotifyAPIDataAccessObject {
    // Return all the playlists the user has that maps playlistId to playlistName
    public HashMap<String, String> getPlaylists(String access_token) {
        String playlistUrl = "https://api.spotify.com/v1/me/playlists";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(playlistUrl))
                .header("Authorization", "Bearer " + access_token)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HashMap<String, String> playlists = new HashMap<>();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray itemsArray = jsonResponse.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject playlistObject = itemsArray.getJSONObject(i);
                    String playlistId = playlistObject.getString("id");
                    String playlistName = playlistObject.getString("name");
                    playlists.put(playlistId, playlistName);
                }
                return playlists;
            } else {
                System.out.println("Error getting user playlists. Status code: " + response.statusCode() +
                        ", Response: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all information about the chosen playlist from the API and store them in the playlist csv file and the users
    // csv file (playlist id only)
    public ArrayList<Object> returnPlaylistInfo(String username, String playlistId, String access_token) {
        ArrayList<Object> playlistInfo = new ArrayList<>();
        ArrayList<String> trackIds = getTrackIds(playlistId, access_token);
        ArrayList<Object> titlesArtists = getTracksTitlesArtists(trackIds, access_token);
        ArrayList<Double> attributes = getAttributes(trackIds, access_token);

        ArrayList<String> titles = (ArrayList<String>) titlesArtists.get(0);
        playlistInfo.add(titles);

        HashMap<String, Integer> artists = (HashMap<String, Integer>) titlesArtists.get(1);
        playlistInfo.add(artists);

        // not adding this to playlistInfo - just for getGenres() method
        ArrayList<String> artistIds = (ArrayList<String>) titlesArtists.get(2);

        HashMap<String, Integer> genres = getGenres(artistIds, access_token);
        playlistInfo.add(genres);

        double acousticness = attributes.get(0);
        double energy = attributes.get(1);
        double instrumentalness = attributes.get(2);
        double valence = attributes.get(3);
        playlistInfo.add(acousticness);
        playlistInfo.add(energy);
        playlistInfo.add(instrumentalness);
        playlistInfo.add(valence);

        return playlistInfo;
    }

    // Get an arrayList of all tracks' ids in a playlist
    private ArrayList<String> getTrackIds(String playlistId, String access_token) {
        String playlistUrl = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(playlistUrl))
                .header("Authorization", "Bearer " + access_token)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            ArrayList<String> trackIds = new ArrayList<>();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray itemsArray = jsonResponse.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject trackObject = itemsArray.getJSONObject(i);
                    String trackId = trackObject.getJSONObject("track").getString("id");
                    trackIds.add(trackId);
                }
                return trackIds;
            } else {
                System.out.println("Error getting playlist tracks. Status code: " + response.statusCode() +
                        ", Response: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get an arraylist of an arraylist of song titles, a hash map of artists names, and an arraylist of artist ids from all track ids
    private ArrayList<Object> getTracksTitlesArtists(ArrayList<String> trackIds, String access_token) {
        ArrayList<Object> titlesArtists = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        HashMap<String, Integer> artists = new HashMap<>();
        // an ArrayList that stores all artist ids (without repetition)
        ArrayList<String> artistIds = new ArrayList<>();

        // Need to run through each track
        for (String trackId: trackIds) {
            String trackUrl = "https://api.spotify.com/v1/tracks/" + trackId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(trackUrl))
                    .header("Authorization", "Bearer " + access_token)
                    .GET()
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    String title = jsonResponse.getString("name");
                    titles.add(title);

                    JSONArray artistsArray = jsonResponse.getJSONArray("artists");
                    for (int i = 0; i < artistsArray.length(); i++) {
                        JSONObject artistObject = artistsArray.getJSONObject(i);
                        String artistName = artistObject.getString("name");
                        String artistId = artistObject.getString("id");

                        if (artists.containsKey(artistName)) {
                            // if the artist is already store, do not need to store them in the artist id map again
                            artists.put(artistName, artists.get(artistName) + 1);
                        } else {
                            // if the artist is not already stored, put them in the artist id map as well
                            artists.put(artistName, 1);
                            artistIds.add(artistId);
                        }
                    }

                    titlesArtists.add(titles);
                    titlesArtists.add(artists);
                    titlesArtists.add(artistIds);
                } else {
                    System.out.println("Error getting track details of the tracks. Status code: " + response.statusCode() +
                            ", Response: " + response.body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return titlesArtists;
    }

    private HashMap<String, Integer> getGenres(ArrayList<String> artistIds, String access_token) {
        HashMap<String, Integer> genres = new HashMap<>();

        String artistUrl = "https://api.spotify.com/v1/artists?ids=" + String.join(",", artistIds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(artistUrl))
                .header("Authorization", "Bearer " + access_token)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);

                JSONArray genresArray = jsonResponse.getJSONArray("genres");
                for (int i = 0; i < genresArray.length(); i++) {
                    String genre = genresArray.getString(i);

                    if (genres.containsKey(genre)) {
                        genres.put(genre, genres.get(genre) + 1);
                    } else {
                        genres.put(genre, 1);
                    }
                }
                return genres;
            } else {
                System.out.println("Error getting track details of the artists. Status code: " + response.statusCode() +
                        ", Response: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all the attributes at once to reduce running time
    private ArrayList<Double> getAttributes(ArrayList<String> trackIds, String access_token) {
        String audioFeatureUrl = "https://api.spotify.com/v1/audio-features?ids=" + String.join(",", trackIds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(audioFeatureUrl))
                .header("Authorization", "Bearer " + access_token)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            double acousticnessSum = 0;
            double energySum = 0;
            double instrumentalnessSum = 0;
            double valenceSum = 0;
            int numOfTracks = trackIds.size();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONArray audioFeaturesArray = new JSONArray(responseBody);

                for (int i = 0; i < audioFeaturesArray.length(); i++) {
                    JSONObject audioFeaturesObject = audioFeaturesArray.getJSONObject(i);
                    acousticnessSum += audioFeaturesObject.getDouble("acousticness");
                    energySum += audioFeaturesObject.getDouble("energy");
                    instrumentalnessSum = audioFeaturesObject.getDouble("instrumentalness");
                    valenceSum = audioFeaturesObject.getDouble("valence");
                }
                ArrayList<Double> attributes = new ArrayList<Double>();

                // adding the mean of each attribute score to attributes arrayList
                attributes.add(acousticnessSum/numOfTracks);
                attributes.add(energySum/numOfTracks);
                attributes.add(instrumentalnessSum/numOfTracks);
                attributes.add(valenceSum/numOfTracks);
                return attributes;
            } else {
                System.out.println("Error getting audio features. Status code: " + response.statusCode() +
                        ", Response: " + response.body());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
