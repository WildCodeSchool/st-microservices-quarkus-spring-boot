package dev.wcs.tutoring.microservice;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class QueryHistoryResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/history")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

}