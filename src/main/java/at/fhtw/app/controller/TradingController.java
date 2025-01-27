package at.fhtw.app.controller;

import at.fhtw.app.model.Trade;
import at.fhtw.app.model.User;
import at.fhtw.app.service.TradingService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


public class TradingController implements RestController {
    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        try {
            if (path.equals("/tradings") && method.equals(HttpMethod.GET)) {
                return handleListingTrading(request);
            } else if (path.equals("/tradings") && method.equals(HttpMethod.POST)) {
                return handleAddTrade(request);
            } else if (path.equals("/tradings") && method.equals(HttpMethod.DELETE)) {
                return handleDeletingTrade(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    public Response handleListingTrading(Request request) throws JsonProcessingException {
        Trade trade = new ObjectMapper().readValue(request.getBody(), Trade.class);
        List<Trade> trades;
        try {
            trades = tradingService.getAllTradingOffers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (trades.isEmpty()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"No trade found\"}");
        } else {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Trade found\"}");
        }
    }

    public Response handleDeletingTrade(Request request) throws JsonProcessingException {
        Trade trade = new ObjectMapper().readValue(request.getBody(), Trade.class);
        boolean bool;
        try {
            bool = tradingService.deleteTradingOffer(trade.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (bool) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Trade deleted\"}");
        } else {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }

    }

    public Response handleAddTrade(Request request) throws JsonProcessingException {
        Trade trade = new ObjectMapper().readValue(request.getBody(), Trade.class);
        try {
            tradingService.createTradingOffer(trade);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Trade added\"}");
        } catch (Exception e){
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }
}
