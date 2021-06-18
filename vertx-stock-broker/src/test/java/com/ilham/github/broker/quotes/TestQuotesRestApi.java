package com.ilham.github.broker.quotes;

import com.ilham.github.broker.MainVerticle;
import com.ilham.github.broker.assets.TestAssetsRestApi;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context) {
    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/AMZN")
      .send()
      .onComplete(context.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        context.completeNow();
      }));
  }

  @Test
  void returns_not_found_for_unknown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(context.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals(404, response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
        context.completeNow();
      }));
  }
}
