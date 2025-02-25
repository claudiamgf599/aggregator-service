package com.trade.aggregator.client;

import com.trade.aggregator.dto.CustomerInformation;
import com.trade.aggregator.dto.StockTradeRequest;
import com.trade.aggregator.dto.StockTradeResponse;
import com.trade.aggregator.exceptions.ApplicationExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class CustomerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceClient.class);
    private final WebClient client;

    public CustomerServiceClient(WebClient client) {
        this.client = client;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return this.client.get()
                .uri("customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerInformation.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId));
    }

    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest request) {
        return this.client.post()
                .uri("customers/{customerId}/trade")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(StockTradeResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId))
                .onErrorResume(WebClientResponseException.BadRequest.class, this::handleException);
    }

    private <T> Mono<T> handleException(WebClientResponseException.BadRequest exception) {
        ProblemDetail responseBodyAs = exception.getResponseBodyAs(ProblemDetail.class);
        String message = Objects.nonNull(responseBodyAs) ? responseBodyAs.getDetail() : exception.getMessage();
        log.error("customer service problem detail: {}", responseBodyAs);
        return ApplicationExceptions.invalidTradeRequest(message);
    }
}
