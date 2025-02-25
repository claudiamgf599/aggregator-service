package com.trade.aggregator.controller;

import com.trade.aggregator.client.StockServiceCliente;
import com.trade.aggregator.dto.PriceUpdate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("stock")
public class StockPriceStreamController {

    private final StockServiceCliente stockServiceCliente;

    public StockPriceStreamController(StockServiceCliente stockServiceCliente) {
        this.stockServiceCliente = stockServiceCliente;
    }

    @GetMapping(value = "/price-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceUpdate> priceUpdateStream() {
        return this.stockServiceCliente.priceUpdatesStream();
    }
}
