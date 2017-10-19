package botrev3.tlgrm.priceChecker;

import BotEx.tlgrm.priceChecker.checker.GetAmountViaJS;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class CheckerBot extends TelegramLongPollingBot {

    static MyTimer timer = MyTimer.getMyTimer();
    static Map<String,PriceChecker> checkers = new HashMap<>();
    private static TasksSaver tasksSaver = new TasksSaver();

    public static final String TOKEN ="402286704:AAGYjEK4OOZynmmyc9fRXxaQNbuwmAQA22U";
    State state = State.WaitCommand;
    PriceChecker currentChecker;

    public CheckerBot(){
        this(1);
        tasksSaver.restore(this);
    }
    public CheckerBot(int i){
        super();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            String text = message.getText();
            switch (state) {
                case WaitCommand: {
                    if (text.equalsIgnoreCase("new")) {
                        currentChecker = new PriceChecker(this, message.getChatId());
                        sendStringMessage(message.getChatId(), state.approved+"  "+text);
                        state = State.WaitLink;
                        break;
                    }else if (text.equalsIgnoreCase("mute")) {
                        sendStringMessage(message.getChatId(), state.approved+"  "+text);
                        state = State.WaitLinkToMute;
                        break;
                    } else if (text.equalsIgnoreCase("list")) {
                        sendStringMessage(message.getChatId(), checkers.toString());
                        break;
                    } else echo(message);
                    break;
                }
                case WaitLink: {
                    text = verifyLink(text);
                    currentChecker.setLink(text);
                    checkers.put(text.trim(),currentChecker);
                    tasksSaver.save();
                    timer.schedule(currentChecker);
                    sendStringMessage(message.getChatId(), state.approved+"  "+text);
                    state = State.WaitCommand;
                    break;
                }
                case WaitLinkToMute: {
                    currentChecker = checkers.get(text.trim());
                    if (currentChecker==null){
                        text = "не найдено";
                    } else{
                        checkers.remove(currentChecker.getLink());
                        tasksSaver.save();
                    }
                    sendStringMessage(message.getChatId(), state.approved+"  "+text);
                    state = State.WaitCommand;
                    break;
                }
            }


        }
    }

    private String verifyLink(String text) {
        if (text.contains("jd.ru")){
            text = text.replace(text.substring(0,text.indexOf("jd.ru")+6), GetAmountViaJS.PRE_SKU_LINK);
        } else if (text.contains("joybuy.com")){
            text = text.replace(text.substring(0,text.indexOf("joybuy.com")+11), GetAmountViaJS.PRE_SKU_LINK);
        }
        return text;
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

    void echo(Message msg){
        sendStringMessage(msg.getChatId(), "echo: "+msg.getText());
    }

    public String getBotUsername() {
        return "Shopper1Bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    enum State{
        WaitCommand("команда:"),WaitLink("ссылка:"), WaitLinkToMute("стоплист:");
        private State(String name2){
            this.approved = name2;
        }
        String approved;

    }

}
