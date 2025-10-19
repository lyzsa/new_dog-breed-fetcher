package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    public String run(String breed) throws IOException {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        ArrayList<String> subBreeds = new ArrayList<>();
        try {
            String response = run(breed);
            JSONObject obj = new JSONObject(response);

            if(!obj.getString("status").equals("success")) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray array = obj.getJSONArray("message");
            for(int i = 0; i < array.length(); i++) {
                subBreeds.add(array.getString(i));
            }
        return subBreeds;

        } catch (BreedNotFoundException e) {
            throw new BreedNotFoundException(breed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}