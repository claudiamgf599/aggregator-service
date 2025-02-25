package com.trade.aggregator.dto;

import com.trade.aggregator.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity) {
}
