package botrev3.tlgrm;

import botrev3.tlgrm.ChatThread;
import lombok.extern.log4j.Log4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j
public class BlogBot extends TelegramLongPollingBot {

    public static final String TOKEN = "344549406:AAHh4oTg-qwNxfJ3FS8Gc1AT0h-okfzGYV8";
    public static final long ADMIN_CHAT_ID = 426631444l;
    public static final long CHANNEL_CHAT_ID = -1001140251814l;
//    public static final String[] MATCH_TEMPLATE_COMMAND_PREFIX = {"find"};
//    public static final String[] LANGS = {"rus", "eng"};
//    public static final String[] MODES = {"parse", "sign"};

    private Map<Long,ChatThread> chatThreads = new HashMap<>();
    private ExecutorService exec = Executors.newFixedThreadPool(10);



    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            long chat_id = message.getChatId();
            if (chat_id==ADMIN_CHAT_ID){
                ChatThread chat = ChatThread.getChatThread(this);
                chat.handle(message);

            }
        }
    }

    public String getBotUsername() {
        return "NastyaHelperBot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }


    public void sendTextToAdmin(String s) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(ADMIN_CHAT_ID)
                .setText(s);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextToChannel(String s) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(CHANNEL_CHAT_ID)
                .setText(s);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
