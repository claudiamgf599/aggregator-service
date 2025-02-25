package com.trade.aggregator.dto;

import com.trade.aggregator.domain.Ticker;
import com.trade.aggregator.domain.TradeAction;

public record TradeRequest(Ticker ticker,
                           TradeAction action,
                           Integer quantity) {
}
