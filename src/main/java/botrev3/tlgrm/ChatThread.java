package botrev3.tlgrm;

import botrev3.AirTableApi;
import botrev3.TextProcessor;
import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import botrev3.domens.Action;
import lombok.extern.log4j.Log4j;
import org.apache.http.client.methods.HttpGet;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class ChatThread{

    public static final String TIME_FORMAT = "dd HH:mm";
    public static final String TIME_REGEX = "[0-3]?[0-9]\\s{0,3}[0-1]?[0-9]\\s{0,3}[0-2]?[0-9]\\s{0,3}[0-6][0-9]";

//    public static final String PRICE_PATTERN = "[0-9]{1,}" ;
    public static final String LINK_PATTERN = "[a-zA-Z0-9]{3,}\\.[a-zA-Z0-9/]{2,}";

    public static final String API_FILE_PATH = "file_path";
    public static final String API_GET_FILE_PATH_LINK = "https://api.telegram.org/bot%s/getFile?file_id=%s";
    public static final String API_GET_FILE_LINK = "https://api.telegram.org/file/bot%s/%s";


    public static final String START_COMMAND_SUCCESS = "New task initialization";
    public static final String CANCEL_COMMAND_SUCCESS = "Task was cancelled, type \"new\" to start new task";
    public static final String[] COMMANDS = {"new", "cancel"};

    private BlogBot bot;
    private Mode mode = Mode.Dead;
    private BlockingQueue<Update> queue= new ArrayBlockingQueue<>(10);
    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private AirTableApi airTableApi = AirTableApi.getApi();


    static ChatThread instance = new ChatThread();
    static ChatThread getChatThread(BlogBot bot) {
        if (instance.bot!=bot){
            instance.bot = bot;
        }
        return instance;
    }


    void setMode(Mode mode) {
        this.mode = mode;
    }

    public void handle(Message message) {
        if (mode == Mode.Dead && message.hasText()) {
            if (message.getText().trim().equalsIgnoreCase("new")) {
                bot.sendTextToAdmin("Input post");
                setMode(Mode.WaitPost);
            } else mode.doSomeWork(this, message);
            return;
        }
        if (mode != Mode.Dead && message.hasText()) {
            if (message.getText().trim().equalsIgnoreCase("cancel")) {
                bot.sendTextToAdmin("task was cancelled");
                setMode(Mode.Dead);
                return;
            }
        }
        if (mode == Mode.WaitPost) {
            mode.doSomeWork(this, message);
            bot.sendTextToAdmin("Input datetime "+TIME_FORMAT);
            setMode(mode.WaitTime);
            return;
        }
        if (mode == Mode.WaitTime) {
            try {
                mode.doSomeWork(this, message);
                bot.sendTextToAdmin("Input price in usd (для метрики)");
                setMode(Mode.WaitPrice);
            } catch (UnsupportedOperationException e) {
                bot.sendTextToAdmin("failed, шаблон текста: " + TIME_FORMAT);
            }
            return;
        }
        if (mode == Mode.WaitPrice) {
            mode.doSomeWork(this, message);
            bot.sendTextToAdmin("Ok");
            setMode(Mode.Dead);
            return;
        }
    }

    enum Mode {
        Dead{
        },
        WaitPost{
            @Override
            void doSomeWork(ChatThread thr, Message msg){
                Action act = Action.getActionForId(null);
                action = act;
                if (msg.hasPhoto()){
                    List<PhotoSize> list = msg.getPhoto();
                    String file_Id = list.get(list.size()-1).getFileId();
                    String file_path =null;
                    String file_link = null;
                    String getFilePath = String.format(API_GET_FILE_PATH_LINK,BlogBot.TOKEN,file_Id);
                    file_path = thr.parser.jsonFindByKey(API_FILE_PATH, HttpEx.execute(new HttpGet(getFilePath)));
                    file_link = String.format(API_GET_FILE_LINK,BlogBot.TOKEN,file_path);
                    act.setImage(file_link);
                }
                if (msg.hasText()){
                    String text = msg.getText();
                    //Pattern pricep = Pattern.compile(PRICE_PATTERN);
                    Pattern linkp = Pattern.compile(LINK_PATTERN);
                    Matcher linkm = linkp.matcher(text);
                    if (linkm.find()){
                        String regexLink = linkm.group();
                        act.setLink(regexLink);
                        act.setDescription(text.replace(regexLink,""));
                        System.out.println(regexLink);
                        System.out.println(text.replace(regexLink,""));
                    }

                }
            }
        },
        WaitTime{
            @Override
            void doSomeWork(ChatThread thr, Message msg){
                if (msg.hasText()){
                    String text = msg.getText().trim();
                    if (text.matches(TIME_REGEX)){
                        action.setTime(text);
                    } else throw new UnsupportedOperationException();

                }
            }
        },
        WaitPrice{
            @Override
            void doSomeWork(ChatThread thr, Message msg){
                String text = "";
                if (msg.hasText()){
                    text = msg.getText().trim();
                }
                text = msg.getText().trim();
                action.setPriceAsString(text);
                action.setLink(proc.changeLink(action.getLink(), Integer.parseInt(text)));
                String id = thr.airTableApi.addAction(action);
                action.setId(id);
                System.out.println(id);
                String logs = "Text: "+action.getDescription() + System.lineSeparator()
                        +"Link: "+action.getLink() + System.lineSeparator()
                        +"Image: "+action.getImage() + System.lineSeparator()
                        +"Price: "+action.getPriceAsString() + System.lineSeparator()
                        +"Time: "+action.getTime() + System.lineSeparator();
                System.out.println(logs);
                thr.bot.sendTextToAdmin(logs);
                log.info(logs);
            }
        };
        private static TextProcessor proc = new TextProcessor();
        private static Action action;
        void doSomeWork(ChatThread thr, Message msg){
            thr.bot.sendTextToAdmin("ECHO: "+msg.getText());
        }
    }
}


