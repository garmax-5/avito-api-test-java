package com.example.avito;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ItemApiTests {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setup() {
        String url = System.getenv("BASE_URL");
        RestAssured.baseURI = (url == null || url.isBlank()) ? "https://qa-internship.avito.com" : url.trim();
    }

    // Вспомогательные методы

    private static int randomSellerId() {
        return ThreadLocalRandom.current().nextInt(111_111, 1_000_000);
    }

    private static Map<String, Object> stats(Object likes, Object viewCount, Object contacts) {
        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("likes", likes);
        statistics.put("viewCount", viewCount);
        statistics.put("contacts", contacts);
        return statistics;
    }

    private static Map<String, Object> createPayload(Object sellerID, Object name, Object price, Object statistics) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (sellerID != null) payload.put("sellerID", sellerID);
        if (name != null) payload.put("name", name);
        if (price != null) payload.put("price", price);
        if (statistics != null) payload.put("statistics", statistics);
        return payload;
    }

    private static Response postCreate(Object sellerID, Object name, Object price, Object statistics) {
        return given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(createPayload(sellerID, name, price, statistics))
                .when()
                .post("/api/1/item")
                .then()
                .extract().response();
    }

    private static JsonNode createValidItem(int sellerId) {
        Response resp = postCreate(sellerId, "testItem", 9900, stats(21, 11, 43));
        assertEquals(200, resp.statusCode(), "Expected 200 OK for valid item creation");
        JsonNode node = resp.as(JsonNode.class);

        String[] requiredFields = {"id", "sellerId", "name", "price", "createdAt", "statistics"};
        for (String field : requiredFields) {
            assertTrue(node.hasNonNull(field));
        }

        assertTrue(node.get("id").isTextual(), "id must be string");
        assertEquals(sellerId, node.get("sellerId").asInt());
        assertEquals("testItem", node.get("name").asText());
        assertEquals(9900, node.get("price").asInt());

        JsonNode st = node.get("statistics");
        assertEquals(21, st.get("likes").asInt());
        assertEquals(11, st.get("viewCount").asInt());
        assertEquals(43, st.get("contacts").asInt());

        return node;
    }

    private static void assertError400Schema(Response resp) {
        assertEquals(400, resp.statusCode(), "Expected 400 Bad Request");
        try {
            JsonNode node = MAPPER.readTree(resp.asString());
            assertTrue(node.hasNonNull("result"));
            assertTrue(node.hasNonNull("status"));
            JsonNode result = node.get("result");
            assertTrue(result.hasNonNull("message"));
            assertTrue(result.hasNonNull("messages"));
        } catch (Exception e) {
            fail("400 response is not valid JSON schema: " + e.getMessage());
        }
    }

    private static void assertError404Schema(Response resp) {
        assertEquals(404, resp.statusCode(), "Expected 404 Not Found");
        try {
            JsonNode node = MAPPER.readTree(resp.asString());
            assertTrue(node.hasNonNull("result"));
            assertTrue(node.hasNonNull("status"));
            assertTrue(node.get("result").isTextual(), "Expected 'result' to be STRING for 404");
        } catch (Exception e) {
            fail("404 response is not valid JSON schema: " + e.getMessage());
        }
    }


    private static void deleteItemQuietly(String id) {
        if (id == null || id.isBlank()) return;
        try {
            given().accept(ContentType.JSON)
                    .when()
                    .delete("/api/2/item/{id}", id)
                    .then()
                    .extract().response();
        } catch (Exception ignored) {
        }
    }

    private static String nonExistingId() {
        return UUID.randomUUID().toString();
    }

    private static String invalidId() {
        return "testInvalidId";
    }

    private static JsonNode getJson(Response resp) {
        try {
            return MAPPER.readTree(resp.asString());
        } catch (Exception e) {
            fail("Response is not valid JSON: " + e.getMessage());
            return null;
        }
    }

    private static int findSellerWithNoItems() {
        for (int i = 0; i < 8; i++) {
            int sellerId = randomSellerId();
            Response resp = given().accept(ContentType.JSON)
                    .when().get("/api/1/{sellerID}/item", sellerId)
                    .then().extract().response();

            if (resp.statusCode() == 200) {
                JsonNode node = getJson(resp);
                if (node.isArray() && node.size() == 0) {
                    return sellerId;
                }
            }
        }
        return randomSellerId();
    }

    // POST /api/1/item

    @Test
    @DisplayName("testcase1-1: POST /api/1/item - valid data -> 200")
    void testcase1_1() {
        int sellerId = randomSellerId();
        JsonNode created = createValidItem(sellerId);
        deleteItemQuietly(created.get("id").asText());
    }

    static Stream<Arguments> missingRequiredFields() {
        int sellerId = randomSellerId();
        return Stream.of(Arguments.of("missing sellerID", createPayload(null, "testItem", 9900, stats(21, 11, 43))),
                Arguments.of("missing name", createPayload(sellerId, null, 9900, stats(21, 11, 43))),
                Arguments.of("missing price", createPayload(sellerId, "testItem", null, stats(21, 11, 43))),
                Arguments.of("missing statistics", createPayload(sellerId, "testItem", 9900, null))
        );
    }

    @ParameterizedTest(name = "testcase1-2 ({0})")
    @MethodSource("missingRequiredFields")
    @DisplayName("testcase1-2: POST /api/1/item - missing required fields -> 400")
    void testcase1_2(String variantName, Map<String, Object> payload) {
        Response resp = given().accept(ContentType.JSON).contentType(ContentType.JSON).body(payload)
                .when().post("/api/1/item")
                .then().extract().response();

        assertError400Schema(resp);
    }

    @Test
    @DisplayName("testcase1-3: POST /api/1/item - sellerID wrong type <string> -> 400")
    void testcase1_3() {
        Response resp = postCreate("test111111", "testItem", 9900, stats(21, 11, 43));
        assertError400Schema(resp);
    }

    static Stream<Arguments> invalidStatisticsFieldTypes() {
        int sellerId = randomSellerId();
        return Stream.of(Arguments.of("likes as string", createPayload(sellerId, "testItem", 9900, stats("twenty one", 11, 43))),
                Arguments.of("viewCount as string", createPayload(sellerId, "testItem", 9900, stats(21, "eleven", 43))),
                Arguments.of("contacts as string", createPayload(sellerId, "testItem", 9900, stats(21, 11, "forty three")))
        );
    }

    @ParameterizedTest(name = "testcase1-4 ({0})")
    @MethodSource("invalidStatisticsFieldTypes")
    @DisplayName("testcase1-4: POST /api/1/item - statistics fields wrong types -> 400")
    void testcase1_4(String variantName, Map<String, Object> payload) {
        Response resp = given().accept(ContentType.JSON).contentType(ContentType.JSON).body(payload)
                .when().post("/api/1/item")
                .then().extract().response();

        assertError400Schema(resp);
    }

    @Test
    @DisplayName("testcase1-5: POST /api/1/item - price wrong type <string> -> 400")
    void testcase1_5() {
        int sellerId = randomSellerId();
        Response resp = postCreate(sellerId, "testItem", "9900", stats(21, 11, 43));
        assertError400Schema(resp);
    }

    @Test
    @DisplayName("testcase1-6: POST /api/1/item - invalid JSON -> 400")
    void testcase1_6() {
        String invalidJson = "{\"sellerID\":123456,\"name\":\"itemX\",\"price\":5000,\"statistics\":{\"likes\":5,\"viewCount\":2,\"contacts\":1}";

        Response resp = given().accept(ContentType.JSON).contentType(ContentType.JSON).body(invalidJson)
                .when().post("/api/1/item")
                .then().extract().response();

        assertError400Schema(resp);
    }

    // GET /api/1/item/{id}

    @Test
    @DisplayName("testcase2-1: GET /api/1/item/{id} - existing id -> 200")
    void testcase2_1() {
        int sellerId = randomSellerId();
        JsonNode created = createValidItem(sellerId);
        String id = created.get("id").asText();

        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/item/{id}", id)
                .then().extract().response();

        assertEquals(200, resp.statusCode(), "Expected 200 OK");
        JsonNode node = getJson(resp);

        assertTrue(node.isArray(), "Expected JSON array response");
        boolean found = false;
        for (JsonNode item : node) {
            if (item.hasNonNull("id") && id.equals(item.get("id").asText())) {
                found = true;
                assertTrue(item.hasNonNull("sellerId"));
                assertTrue(item.hasNonNull("name"));
                assertTrue(item.hasNonNull("price"));
                assertTrue(item.hasNonNull("createdAt"));
                assertTrue(item.hasNonNull("statistics"));
                break;
            }
        }
        assertTrue(found, "Array should contain created item by id");

        deleteItemQuietly(id);
    }

    @Test
    @DisplayName("testcase2-2: GET /api/1/item/{id} - non-existing id -> 404")
    void testcase2_2() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/item/{id}", nonExistingId())
                .then().extract().response();

        assertError404Schema(resp);
    }

    @Test
    @DisplayName("testcase2-3: GET /api/1/item/{id} - invalid id -> 400")
    void testcase2_3() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/item/{id}", invalidId())
                .then().extract().response();

        assertError400Schema(resp);
    }

    // GET /api/1/{sellerID}/item

    @Test
    @DisplayName("testcase3-1: GET /api/1/{sellerID}/item - existing seller -> 200")
    void testcase3_1() {
        int sellerId = randomSellerId();
        JsonNode created = createValidItem(sellerId);
        String id = created.get("id").asText();

        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/{sellerID}/item", sellerId)
                .then().extract().response();

        assertEquals(200, resp.statusCode(), "Expected 200 OK");
        JsonNode node = getJson(resp);
        assertTrue(node.isArray(), "Expected JSON array response");

        boolean found = false;
        for (JsonNode item : node) {
            if (item.hasNonNull("id") && id.equals(item.get("id").asText())) {
                found = true;
                assertEquals(sellerId, item.get("sellerId").asInt());
                break;
            }
        }
        assertTrue(found, "Sellers array should contain created item");
        deleteItemQuietly(id);
    }

    @Test
    @DisplayName("testcase3-2: GET /api/1/{sellerID}/item - non-existing seller -> 200")
    void testcase3_2() {
        int sellerId = findSellerWithNoItems();

        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/{sellerID}/item", sellerId)
                .then().extract().response();

        assertEquals(200, resp.statusCode(), "Expected 200 OK");
        JsonNode node = getJson(resp);
        assertTrue(node.isArray(), "Expected JSON array");
        assertEquals(0, node.size(), "Expected empty array [] for seller with no items");
    }

    @Test
    @DisplayName("testcase3-3: GET /api/1/{sellerID}/item - invalid sellerID -> 400")
    void testcase3_3() {
        Response resp =
                given().accept(ContentType.JSON)
                        .when().get("/api/1/{sellerID}/item", "asd")
                        .then().extract().response();

        assertError400Schema(resp);
    }

    // GET /api/1/statistic/{id}

    @Test
    @DisplayName("testcase4-1: GET /api/1/statistic/{id} - existing id -> 200")
    void testcase4_1() {
        int sellerId = randomSellerId();
        JsonNode created = createValidItem(sellerId);
        String id = created.get("id").asText();

        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/statistic/{id}", id)
                .then().extract().response();

        assertEquals(200, resp.statusCode(), "Expected 200 OK");
        JsonNode node = getJson(resp);
        assertTrue(node.isArray(), "Expected array");
        assertTrue(node.size() >= 1, "Expected at least 1 element");

        JsonNode st = node.get(0);
        assertTrue(st.hasNonNull("likes"));
        assertTrue(st.hasNonNull("viewCount"));
        assertTrue(st.hasNonNull("contacts"));
        assertTrue(st.get("likes").canConvertToInt());
        assertTrue(st.get("viewCount").canConvertToInt());
        assertTrue(st.get("contacts").canConvertToInt());

        deleteItemQuietly(id);
    }

    @Test
    @DisplayName("testcase4-2: GET /api/1/statistic/{id} - non-existing id -> 404")
    void testcase4_2() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/statistic/{id}", nonExistingId())
                .then().extract().response();

        assertError404Schema(resp);
    }

    @Test
    @DisplayName("testcase4-3: GET /api/1/statistic/{id} - invalid id -> 400")
    void testcase4_3() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/1/statistic/{id}", invalidId())
                .then().extract().response();

        assertError400Schema(resp);
    }

    // GET /api/2/statistic/{id}

    @Test
    @DisplayName("testcase5-1: GET /api/2/statistic/{id} - existing id -> 200")
    void testcase5_1() {
        int sellerId = randomSellerId();
        JsonNode created = createValidItem(sellerId);
        String id = created.get("id").asText();

        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/2/statistic/{id}", id)
                .then().extract().response();

        assertEquals(200, resp.statusCode(), "Expected 200 OK");
        JsonNode node = getJson(resp);
        assertTrue(node.isArray(), "Expected array");
        assertTrue(node.size() >= 1, "Expected at least 1 element");

        JsonNode st = node.get(0);
        assertTrue(st.hasNonNull("likes"));
        assertTrue(st.hasNonNull("viewCount"));
        assertTrue(st.hasNonNull("contacts"));
        assertTrue(st.get("likes").canConvertToInt());
        assertTrue(st.get("viewCount").canConvertToInt());
        assertTrue(st.get("contacts").canConvertToInt());

        deleteItemQuietly(id);
    }

    @Test
    @DisplayName("testcase5-2: GET /api/2/statistic/{id} - non-existing id -> 404")
    void testcase5_2() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/2/statistic/{id}", nonExistingId())
                .then().extract().response();

        assertError404Schema(resp);
    }

    @Test
    @DisplayName("testcase5-3: GET /api/2/statistic/{id} - invalid id -> 400")
    void testcase5_3() {
        Response resp = given().accept(ContentType.JSON)
                .when().get("/api/2/statistic/{id}", invalidId())
                .then().extract().response();

        assertError400Schema(resp);
    }
}
