package at.fhtw;

import at.fhtw.app.controller.*;
import at.fhtw.app.service.DeckService;
import at.fhtw.app.service.PackageService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserController(new UserService()));
        router.addService("/sessions", new LoginController(new UserService()));
        router.addService("/stats", new StatsController(new UserService()));
        router.addService("/transactions", new PackageController(new PackageService()));
        router.addService("/cards", new PackageController(new PackageService()));
        router.addService("/packages", new PackageController(new PackageService()));
        router.addService("/deck", new DeckController(new DeckService()));
        return router;
    }
}
