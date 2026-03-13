package org.acme;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class LoadResourceTest {

    @Inject
    MeterRegistry registry;

    @Test
    void testCountEndpoint() {
        given()
                .when().get("/load/count")
                .then()
                .statusCode(200)
                .body(is("Current count is 0"));

        given()
                .when().post("/load/count")
                .then()
                .statusCode(200)
                .body(is("The counter is incremented"));

        given()
                .when().get("/load/count")
                .then()
                .statusCode(200)
                .body(is("Current count is 1"));
    }

    @Test
    void testWaitEndpoint() {
        given()
                .when().get("/load/wait")
                .then()
                .statusCode(200);

        double maxTime = registry.find("load.random_wait_time").timer().max(TimeUnit.SECONDS);
        assertThat(maxTime, is(both(greaterThanOrEqualTo(1.0)).and(lessThanOrEqualTo(5.0))));
    }

}