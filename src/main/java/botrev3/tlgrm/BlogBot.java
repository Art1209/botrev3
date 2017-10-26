package botrev3.tlgrm;

import botrev3.MyTimer;
import lombok.extern.log4j.Log4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.concurrent.*;


@Log4j
public class BlogBot extends TelegramLongPollingBot {

    public static final String TOKEN = "344549406:AAHh4oTg-qwNxfJ3FS8Gc1AT0h-okfzGYV8";
    public static final long ADMIN_CHAT_ID = 426631444l;
    public static final long CHANNEL_CHAT_ID = -1001140251814l;

    MyTimer timer = MyTimer.getTimer(this);
    private ExecutorService exec = Executors.newFixedThreadPool(5);

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            long chat_id = message.getChatId();
            log.info(message.getText());
            if (chat_id==ADMIN_CHAT_ID){
                ChatThread chat = ChatThread.getChatThread(this);
                chat.setMsg(message);
                Boolean success = false;
                int attempts = 0;
                while (success != true && attempts < 6) {
                    try {
                        Future<Boolean> fut = exec.submit(chat);
                        success = fut.get(10000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        attempts++;
                    }
                }
                if (attempts > 4) {
                    exec.shutdown();
                    exec = Executors.newFixedThreadPool(5);
                    sendTextToAdmin("Ошибка обработки, проверьте параметры и повторите попытку");
                }
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

    public void sendPhotoTextToChannel(String s, String imgLink) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(CHANNEL_CHAT_ID)
                .setCaption(s)
                .setPhoto(imgLink);
        try {
            sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
