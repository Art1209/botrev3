package botrev3.bot;

import botrev3.flow.CustomTask;
import botrev3.flow.TasksKeeper;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatThread implements Runnable{

    public static final String TIME_FORMAT = "dd MM HH mm";
    public static final String TIME_REGEX = "[0-3]?[0-9]\\s{0,3}[0-1]?[0-9]\\s{0,3}[0-2]?[0-9]\\s{0,3}[0-6][0-9]";

    public static  final String START_COMMAND = "new";
    public static  final String CANCEL_COMMAND = "cancel";
    public static  final String ENTER_LINK = "enter link of the PRODUCT page";
    public static  final String ENTER_TIME = String.format("enter time in format %s",TIME_FORMAT);
    public static  final String ENTER_BUTTON_NAME = "enter text from the BUY button";

    public static  final String ENTER_LINK_SUCCESS = "success";
    public static  final String ENTER_TIME_SUCCESS = "success, entered time is: %s";
    public static  final String ENTER_BUTTON_NAME_SUCCESS = "success, button is: %s";

    public static  final String REENTER_LINK = "fail, enter link again";
    public static  final String REENTER_TIME = String.format("fail, enter time in format %s again",TIME_FORMAT);
    public static  final String TASK_SUCCESS = "Your task was successfully added";
    public static  final String TOO_MANY_ENTER_BUTTON_NAME = "too many buttons found please try again";

    public static final String START_COMMAND_SUCCESS = "New task initialization";
    public static final String CANCEL_COMMAND_SUCCESS = "Task was cancelled, type \"new\" to start new task";
    public static final String[] COMMANDS = {"new", "cancel"};

    private long chat_id;

    static ExecutorService exec = Executors.newFixedThreadPool(1);

    private TelegramLongPollingBot bot;
    private Mode mode = Mode.Dead;
    private CustomTask.ShopTaskBuilder builder;
    private TasksKeeper tasksKeeper = TasksKeeper.getTasksKeeper();
    private HttpExecuter httpExecuter = HttpExecuter.getHttpExecuter();
    private BlockingQueue<Update> queue= new ArrayBlockingQueue<>(10);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy "+TIME_FORMAT);


    static ChatThread getChatThread(Update update, TelegramLongPollingBot bot) {
        ChatThread thr = new ChatThread(update, bot);
        return thr;
    }
    private ChatThread(Update update, TelegramLongPollingBot bot) {
        this.bot = bot;
        this.chat_id = update.getMessage().getChatId();
    }


    @Override
    public void run() {
        if (!queue.isEmpty())
            try {
                handleTextMessage(queue.take().getMessage().getText());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private void initBuilder() {
        builder = CustomTask.getBuilder();
    }

    void addUpdate(Update update){
        queue.add(update);
        exec.execute(this);
    }

    private void handleTextMessage(String msg){
        boolean isCommand = false;
        for (String str:COMMANDS) {
            if (msg.toLowerCase().contains(str)) {
                isCommand = true;
                break;
            }
        }
        if (isCommand) {
            mode.handleCommand(this, msg.toLowerCase());
        } else mode.doSomeWork(this, msg);
    }

    private void sendMessage(String message) {
        ((BotFace)bot).sendStringMessage(chat_id, message);
    }

    void setMode(Mode mode) {
        this.mode = mode;
    }

    enum Mode {
        Dead{
            @Override
            void handleCommand(ChatThread thr, String str){
                switch (str){
                    case START_COMMAND: {
                        thr.initBuilder();
                        thr.sendMessage(START_COMMAND_SUCCESS);
                        thr.sendMessage(ENTER_LINK);
                        thr.setMode(Mode.WaitLink);
                        break;
                    }
                    default: doSomeWork(thr,str);
                }
            }
        },
        WaitLink{
            @Override
            void doSomeWork(ChatThread thr, String str){
                if (thr.httpExecuter.isLinkValid(str)){
                    thr.sendMessage(ENTER_LINK_SUCCESS);
                    thr.builder.setLink(str);
                    thr.sendMessage(ENTER_TIME);
                    thr.setMode(Mode.WaitTime);
                } else thr.sendMessage(REENTER_LINK);
            }

        },
        WaitTime{
            @Override
            void doSomeWork(ChatThread thr, String str){
                if (str.trim().matches(TIME_REGEX)){
                    Date today = new Date(System.currentTimeMillis());
                    Date target;
                    try {
                        target = thr.format.parse(Year.now().getValue()+" "+str);
                        if (today.after(target)) target = thr.format.parse((Year.now().getValue()+1)+" "+str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        thr.sendMessage(REENTER_TIME);
                        return;
                    }
                    thr.builder.setTime(target.getTime());
                    thr.sendMessage(String.format(ENTER_TIME_SUCCESS, target.toString()));
                    thr.sendMessage(ENTER_BUTTON_NAME);
                    thr.setMode(Mode.WaitButton);
                } else thr.sendMessage(REENTER_TIME);
            }
        },
        WaitButton{
            @Override
            void doSomeWork(ChatThread thr, String str){
                String[] buttons = thr.httpExecuter.getButtonsByName(thr.builder.getBuilderLink(), str);
                switch (buttons.length){
                    case 0: {
                        thr.sendMessage(REENTER_TIME);
                        break;
                    }
                    case 1:{
                        thr.builder.setButton(buttons[0]);
                        thr.sendMessage(String.format(ENTER_BUTTON_NAME_SUCCESS, str));
                        thr.tasksKeeper.register(thr.builder.build());
                        thr.builder=null;
                        thr.sendMessage(TASK_SUCCESS);
                        thr.setMode(Mode.Dead);
                        break;
                    }
                    default:{
                        thr.sendMessage(TOO_MANY_ENTER_BUTTON_NAME);
                        System.out.println(Arrays.toString(buttons)+Year.now().getValue());
                    }
                }
            }
        };


        void handleCommand(ChatThread thr, String str){
            switch (str){
                case CANCEL_COMMAND: {
                    thr.sendMessage(CANCEL_COMMAND_SUCCESS);
                    thr.setMode(Mode.Dead);
                    break;
                }
                case START_COMMAND: {
                    thr.initBuilder();
                    thr.sendMessage(START_COMMAND_SUCCESS);
                    thr.sendMessage(ENTER_LINK);
                    thr.setMode(Mode.WaitLink);
                    break;
                }
            }
        }
        void doSomeWork(ChatThread thr, String str){
            thr.sendMessage("ECHO: "+str);
        }
    }
}


