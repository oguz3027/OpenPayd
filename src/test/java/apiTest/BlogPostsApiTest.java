package apiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlogPostsApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

    @Test
    void testCountingPostsPerUser() {

        Map<Integer, Integer> testCases = Map.of(
                5, 10,
                7, 10,
                9, 10
        );

        Response response = RestAssured.get(BASE_URL);
        assertEquals(200, response.getStatusCode());


        List<Map<String, Object>> posts = response.jsonPath().getList("");

        for (Map.Entry<Integer, Integer> testCase : testCases.entrySet()) {
            int userId = testCase.getKey();
            int expectedCount = testCase.getValue();

            long actualCount = posts.stream()
                    .filter(post -> userId == ((Number) post.get("userId")).intValue())
                    .count();

            assertEquals(expectedCount, actualCount, "Post count mismatch for userId " + userId);
        }
    }

    @Test
    void testUniqueIdsPerPost() {

        Response response = RestAssured.get(BASE_URL);
        assertEquals(200, response.getStatusCode(), "API response status code should be 200");

        List<Map<String, Object>> posts = response.jsonPath().getList("");

        List<Integer> ids = posts.stream()
                .map(post -> ((Number) post.get("id")).intValue())
                .collect(Collectors.toList());

        Set<Integer> uniqueIds = new HashSet<>(ids);
        assertEquals(ids.size(), uniqueIds.size(), "Post IDs are not unique");
    }
}
