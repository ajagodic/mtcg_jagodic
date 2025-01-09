package at.fhtw.app.controller;

import at.fhtw.app.model.Trade;
import at.fhtw.app.service.TradingService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

import java.util.List;

public class TradingController {
    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    public Response handleRequest(Request request) {
        return null;// Implementiere die API-Logik (Erstellen, Abrufen, Löschen, Akzeptieren von Angeboten)
    }
}
