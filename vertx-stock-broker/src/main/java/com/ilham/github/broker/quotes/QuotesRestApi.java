package com.ilham.github.broker.quotes;

import com.ilham.github.assets.Asset;
import com.ilham.github.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
