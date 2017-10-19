package botrev3.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotFace extends TelegramLongPollingBot {

    public static final String TOKEN ="402286704:AAGYjEK4OOZynmmyc9fRXxaQNbuwmAQA22U";


    private Map<Long, ChatThread> chatThreads = new HashMap<>();

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            long chat_id = message.getChatId();
            if (needNewChatThread(chat_id)&&chatThreads.size()<2){
                chatThreads.put(chat_id, ChatThread.getChatThread(update,this));
            }
            if (message.hasText()) {
                chatThreads.get(chat_id).addUpdate(update);
            }
        }
    }



    void sendStringMessage(long chatId, String textMessage){
        SendMessage sendMessage = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText(textMessage);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private boolean needNewChatThread(long id) {  // вынес в отдельный метод на случай изменения условия хранения данных потока
        return !chatThreads.containsKey(id);
    }

    synchronized boolean deleteFromChatThreads(long id){
        if (chatThreads.containsKey(id)){
            chatThreads.remove(id);
            return true;
        }return false;
    }

    public String getBotUsername() {
        return "Shopper1Bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }




}
