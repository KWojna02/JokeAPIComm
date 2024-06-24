package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class JokeFetcher {
    private static final String BASE_URL = "https://v2.jokeapi.dev/joke/Any";
    private final OkHttpClient client;
    private final Random random;
    private final Queue<Long> requestTimestamps;
    private int remainingRequests;

    public JokeFetcher() {
        this.client = new OkHttpClient();
        this.random = new Random();
        this.remainingRequests = 120;
        this.requestTimestamps = new LinkedList<>();
    }

    public int getRemainingRequests() {
        updateRequestCount();
        return remainingRequests;
    }

    private void updateRequestCount() {
        long currentTime = System.currentTimeMillis();
        while (!requestTimestamps.isEmpty() && (currentTime - requestTimestamps.peek() > 60000)) {
            requestTimestamps.poll();
            remainingRequests++;
        }
    }

    public Joke getJokeById(int id) throws IOException {
        updateRequestCount();
        if (remainingRequests <= 0) {
            throw new IOException("API request limit reached");
        }
        String url = BASE_URL + "?idRange=" + id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            remainingRequests--;
            requestTimestamps.add(System.currentTimeMillis());

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            return json.has("error") && json.getBoolean("error") ? null : jokeParser(json);
        }
    }

    private Joke jokeParser(JSONObject jokeObject) {
        int id = jokeObject.getInt("id");
        String type = jokeObject.getString("type");
        String joke = type.equals("single")
                ? jokeObject.getString("joke")
                : jokeObject.getString("setup") + "\n" + jokeObject.getString("delivery");

        JSONObject flags = jokeObject.getJSONObject("flags");
        boolean nsfw = flags.getBoolean("nsfw");
        boolean religious = flags.getBoolean("religious");
        boolean political = flags.getBoolean("political");
        boolean racist = flags.getBoolean("racist");
        boolean sexist = flags.getBoolean("sexist");
        boolean explicit = flags.getBoolean("explicit");

        return new Joke(id, joke, type, nsfw, religious, political, racist, sexist, explicit);
    }

    public void fetchAndSaveJokes(DatabaseManager dbManager, int jokeCount) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        while (ids.size() < jokeCount) {
            int id = random.nextInt(0, 319);
            if (!ids.contains(id) && id != 47) {
                ids.add(id);
            }
        }
        for (int id : ids) {
            if (dbManager.jokeExists(id)) {
                System.out.println("Joke with ID " + id + " already exists in the database. Skipping fetch.");
                continue;
            }

            try {
                Joke joke = getJokeById(id);
                if (joke != null) {
                    dbManager.saveJoke(
                            joke.id(),
                            joke.text(),
                            joke.type(),
                            joke.nsfw(),
                            joke.religious(),
                            joke.political(),
                            joke.racist(),
                            joke.sexist(),
                            joke.explicit()
                    );
                    System.out.println("Saved joke with id: " + joke.id());
                } else {
                    System.out.println("No joke found with id: " + id);
                }
            } catch (IOException e) {
                System.out.println("Failed to save joke with id: " + id + ". Error:" + e.getMessage());
            }
        }
        updateRequestCount();
    }

}
