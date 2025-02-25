package com.trade.aggregator.dto;

import com.trade.aggregator.domain.Ticker;

public record StockPriceResponse(Ticker ticker,
                                 Integer price) {
}
