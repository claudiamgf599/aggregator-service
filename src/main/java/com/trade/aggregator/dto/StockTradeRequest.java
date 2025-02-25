package com.trade.aggregator.dto;

import com.trade.aggregator.domain.Ticker;
import com.trade.aggregator.domain.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer price,
                                Integer quantity,
                                TradeAction action) {

    public Integer totalPrice() {
        return price * quantity;
    }
}
