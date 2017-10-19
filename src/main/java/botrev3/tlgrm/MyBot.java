package botrev3.tlgrm;

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
public class MyBot extends TelegramLongPollingBot {
    public static final String WATERMARK_LINK = "http://sm.uploads.im/ep2Xi.png";
    public static final String API_DOWNLOAD_IMG_LINK ="http://uploads.im/api?upload=%s&resize_width=400";
    public static final String API_IMG_PATH ="img_url";
    public static final String API_OCR_PATH ="ParsedText";
    public static final String API_OCR_PARSE ="https://api.ocr.space/parse/imageurl?apikey=c9f49f68ca88957&url=%s&language=%s";
    public static final String API_OCR_PARSE_OVERLAY ="https://api.ocr.space/parse/imageurl?apikey=c9f49f68ca88957&url=%s&language=%s&isOverlayRequired=true";
    public static final String LANG_CHANGE_SUCCESS = "язык изменен на %s";
    public static final String MODE_CHANGE_SUCCESS = "режим работы изменен на %s";
    public static final String MATCH_TEMPLATE_CHANGE_SUCCESS = "поисковый запрос изменен на [ %s ]";
    public static final String API_FILE_PATH = "file_path";
    public static final String API_GET_FILE_PATH_LINK = "https://api.telegram.org/bot%s/getFile?file_id=%s";
    public static final String API_GET_FILE_LINK = "https://api.telegram.org/file/bot%s/%s";
    public static final String TOKEN ="449406097:AAFZ4ZN8LGsfdZSZ9SBNJLwYCsNKUVbq5Hs";
    public static final String TARGET_FILE ="result.jpg";
    public static final String ECHO_FORMAT = "ECHO:%s";
    public static final String STANDART_FILE_NAME = "file_";
    public static final String ON_FAIL_MESSAGE = "Неудачная попытка, попробуте еще раз";
    public static final String[] MATCH_TEMPLATE_COMMAND_PREFIX = {"find"};
    public static final String[] LANGS = {"rus", "eng"};
    public static final String[] MODES = {"parse", "sign"};

    private Map<Long,ChatThread> chatThreads = new HashMap<>();
    private ExecutorService exec = Executors.newFixedThreadPool(10);



    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            long chat_id = message.getChatId();
            if (needNewChatThread(chat_id)){
                System.out.println("adding "+chat_id);
                chatThreads.put(chat_id,ChatThread.getChatThread(update, this));
            }
            if (message.hasText()) {
                String message_text = message.getText();
                String back_message_text = commandHandler(message_text, chat_id);

                SendMessage sendMessage = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(back_message_text);
                try {
                    sendMessage(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if (message.hasPhoto()) {
                System.out.println("hasphoto");
                exec.execute(chatThreads.get(chat_id).setUpdate(update));
            }
        }
    }

    private String commandHandler(String message, long id){
        String format;
        // используется массив, при добавлении команд добавить распарсить что пришло!
        if (containsIgnoreCase(MATCH_TEMPLATE_COMMAND_PREFIX,  message.split(" ")[0])) {
            message = message.replace((message.split(" ")[0]),"").trim();
            chatThreads.get(id).setMatchTemplate(message);
            format = MATCH_TEMPLATE_CHANGE_SUCCESS;
        } else if (containsIgnoreCase(MODES, message)){
            chatThreads.get(id).setMode(message.toLowerCase());
            format = MODE_CHANGE_SUCCESS;
        } else if (containsIgnoreCase(LANGS, message)){
            chatThreads.get(id).setLang(message.toLowerCase());
            format = LANG_CHANGE_SUCCESS;
        } else format = ECHO_FORMAT;
        return String.format(format, message);
    }
    private boolean needNewChatThread(long id) {  // вынес в отдельный метод на случай изменения условия хранения данных потока
        return !chatThreads.containsKey(id);
    }

    private boolean containsIgnoreCase(String[] list, String key){
        for (String str:list) {
            if (str.equalsIgnoreCase(key)) return true;
        } return false;
    }

    synchronized boolean deleteFromChatThreads(long id){
        if (chatThreads.containsKey(id)){
            chatThreads.remove(id);
            return true;
        }return false;
    }

    public String getBotUsername() {
        return "EdRoSignerBot";
    }

    @Override
    public String getBotToken() {
        return "449406097:AAFZ4ZN8LGsfdZSZ9SBNJLwYCsNKUVbq5Hs";
    }




}
