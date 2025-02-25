package com.trade.aggregator.client;

import com.trade.aggregator.domain.Ticker;
import com.trade.aggregator.dto.PriceUpdate;
import com.trade.aggregator.dto.StockPriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

public class StockServiceCliente {

    private static final Logger log = LoggerFactory.getLogger(StockServiceCliente.class);
    private final WebClient client;
    private Flux<PriceUpdate> flux;

    public StockServiceCliente(WebClient client) {
        this.client = client;
    }

    public Mono<StockPriceResponse> getStockPrice(Ticker ticker) {
        return this.client.get()
                .uri("/stock/{ticker}", ticker)
                .retrieve()
                .bodyToMono(StockPriceResponse.class);
    }

    public Flux<PriceUpdate> priceUpdatesStream() {
        if (Objects.isNull(this.flux)) {
            this.flux = this.getPriceUpdates();
        }
        return this.flux;
    }

    // Se hace un broadcast a todos los consumers (users), es privado porque no hay necesidad que lo llamen
    private Flux<PriceUpdate> getPriceUpdates() {
        return this.client.get()
                .uri("/stock/price-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(PriceUpdate.class)
                .retryWhen(retry())
                .cache(1); // se da el ultimo valor emitido
    }

    // Si pasa un reinicio durante ese tiempo el servicio estaría retornando señal de error, por esto se implementa un retry
    private Retry retry() {
        return Retry.fixedDelay(100, Duration.ofSeconds(1))
                .doBeforeRetry(rs -> log.error("stock service price stream call failed. retrying: {}", rs.failure().getMessage()));
    }
}
