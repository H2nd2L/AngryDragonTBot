package angryDragon;

import angryDragon.components.api.DragonBot;
//import angryDragon.components.App;
//import angryDragon.components.repository.RepositoryComponent;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) throws TelegramApiException {
        DragonBot bot = new DragonBot();

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
//        System.out.println("APP STARTING>>>");
//        App app = new App();
//        app.start();
    }
}
