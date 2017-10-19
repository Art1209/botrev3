package botrev3.flow;

import ShopperBot.HttpExecuter;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.Serializable;
import java.util.TimerTask;

public class CustomTask extends TimerTask implements Serializable {

    private static int counter;
    private static final long FIRST_START_DELAY= 50000;
    private static final long EACH_OPERATION_DELAY= 10000;
    private static TasksKeeper tasksKeeper = TasksKeeper.getTasksKeeper();
    private static HttpExecuter httpExecuter = HttpExecuter.getHttpExecuter();

    private int id = ++counter;
    private String link;
    private long time;
    private long nextLaunchTime;
    private String buttonName;
    private State state = State.Start;
    private transient Shop shop;
    private transient WebClient client = new WebClient();

    CustomTask(){}
    CustomTask(String link, String buttonName){
        this.buttonName = buttonName;
        this.link = link;
    }

    public static ShopTaskBuilder getBuilder(){
        return new ShopTaskBuilder();
    }


    @Override
    public void run() {
        switch (state){
            case Start: {
                initShop();
                shop.login(this);
                state = State.LoggedIn;
                setNextLaunchTime(getNextLaunchTime()+EACH_OPERATION_DELAY);
                tasksKeeper.register(this);
                break;
            }
            case LoggedIn: {
                shop.doBeforeClick(this);
                state = State.Ready;
                long ping = httpExecuter.pingCounter(getLink())/2;
                setNextLaunchTime(getTime()-ping);
                tasksKeeper.register(this);
                break;
            }
            case Ready: {
                shop.click(this);
                state = State.Clicked;
                setNextLaunchTime(getNextLaunchTime()+EACH_OPERATION_DELAY);
                tasksKeeper.register(this);
                break;
            }
            case Clicked: {
                shop.doAfterClick(this);
                state = State.ExtraState;
                setNextLaunchTime(getNextLaunchTime()+EACH_OPERATION_DELAY);
                tasksKeeper.register(this);
                break;
            }
            case ExtraState: {
                shop.extraFlow(this);
                state = State.Done;
                break;
            }
            default:{}
        }


        shop.login(this);
        shop.doBeforeClick(this);
        shop.click(this);
        shop.doAfterClick(this);
        shop.extraFlow(this);


        System.out.println("CustomTask ON_AIR");
        tasksKeeper.unregister(this);
    }

    private void initShop() {
        for (Shop shop:Shop.values()){
            if (link.contains(shop.getHost())){
                this.shop = shop;
                break;
            } else this.shop = Shop.NullShop;
        }
    }


    void setLink(String link) {
        this.link = link;
    }

    private void setTime(long time) {
        this.time = time;
    }

    void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    void setNextLaunchTime(long time){
        this.nextLaunchTime = time;
    }

    public String getLink() {
        return link;
    }

    public int getId() {
        return id;
    }

    private long getTime() {
        return time;
    }

    public long getNextLaunchTime() {
        return nextLaunchTime;
    }

    public String getButtonName() {
        return buttonName;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return  obj instanceof CustomTask  && ((CustomTask) obj).getId() == this.getId();
    }

    @Override
    public String toString() {
        return getId()+" " + getTime() + getLink();
    }


    public static class ShopTaskBuilder{
        private CustomTask task = new CustomTask(null,null);

        String builderLink;
        long builderTime;

        public CustomTask build(){
            if (task.getButtonName()==null||task.getTime()==0||task.getLink()==null){
                System.out.println("ShopTaskBuilder cannot build wrong task");
                return null;
            } else return task;
        }
        public ShopTaskBuilder setLink(String link){
            builderLink = link;
            task.setLink(link);
            return this;
        }
        public ShopTaskBuilder setButton(String button){
            task.setButtonName(button);
            return this;
        }
        public ShopTaskBuilder setTime(long time){
            task.setNextLaunchTime(time-FIRST_START_DELAY);
            builderTime = time;
            task.setTime(time);
            return this;
        }

        public String getBuilderLink() {
            return builderLink;
        }
    }

    enum State implements Serializable{
        Start, LoggedIn, Ready, Clicked, ExtraState, Done;
    }
}
