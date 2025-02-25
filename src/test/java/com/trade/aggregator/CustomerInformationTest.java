package com.trade.aggregator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

class CustomerInformationTest extends AbstractIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerInformationTest.class);

    @Test
    public void customerInformationTest() {
        // given
        mockCustomerInformation("customer-service/customer-information-200.json", 200);

        // then
        getCustomerInformation(HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10000)
                .jsonPath("$.holdings").isNotEmpty();
    }

    @Test
    public void customerNotFoundTest() {
        // given
        mockCustomerInformation("customer-service/customer-information-404.json", 404);

        // then
        getCustomerInformation(HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] is not found")
                .jsonPath("$.title").isNotEmpty();
    }

    @AfterAll
    public static void clearExpectations() {
        mockServer.reset();
    }

    @BeforeEach
    public void resetExpectations() {
        mockServerClient.reset();
    }

    private void mockCustomerInformation(String path, int responseCode) {
        /* // Using a json as string
        String responseBody = """
                {
                    "id":1,
                	"name":"sam"
                }
                """;*/
        String responseBody = this.resourceToString(path);

        mockServerClient
                .when(HttpRequest.request("/customers/1"))
                .respond(
                        HttpResponse.response(responseBody)
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private WebTestClient.BodyContentSpec getCustomerInformation(HttpStatus expectedStatus) {
        return this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }
}
