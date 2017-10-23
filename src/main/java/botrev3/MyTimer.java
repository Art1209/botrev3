package botrev3;

import botrev3.domens.Action;
import botrev3.domens.Category;
import botrev3.domens.Shop;
import botrev3.tlgrm.BlogBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aalbutov on 23.10.2017.
 */

@Log4j
public class MyTimer {
    private static Timer lifeCycleTimer = new Timer(); // for update processes
    private static Timer tasktimer = new Timer();  // for Actions
    @Getter @Setter private static BlogBot bot;


    private static AirTableApi airTableApi = AirTableApi.getApi();
    {
        airTableApi.init();
        lifeCycleTimer.schedule(new LocalTask(Updater.ActionUpdater, 10000),10000);
        lifeCycleTimer.schedule(new LocalTask(Updater.ShopUpdater, 30000),10000);
        lifeCycleTimer.schedule(new LocalTask(Updater.CategoryUpdater, 100000),10000);

    }
    private static MyTimer instance = new MyTimer();
    public static MyTimer getTimer(BlogBot bot){
        if (MyTimer.bot!=bot){
           setBot(bot);
        }
        return instance;
    }

    public static void scheduleTask(MyPostWriter myPostWriter){
        if (myPostWriter.getAction()!=null){
            Date postdate = myPostWriter.getAction().getTimeAsDate();
            log.info("postdate: "+postdate.toString() +"  now: "+(new Date()).toString());
            tasktimer.schedule(myPostWriter, myPostWriter.getAction().getTimeAsDate());
        }

    }
    private static class LocalTask extends TimerTask {

        private Updater updater;
        private long delay;

        public LocalTask(Updater updater, long delay) {
            this();
            this.updater = updater;
            this.delay = delay;
        }

        public LocalTask(LocalTask oldTask) {
            this();
            this.updater = oldTask.updater;
            this.delay = oldTask.delay;
        }


        public LocalTask() {
        }

        @Override
        public void run() {
            updater.initUpdate();
            lifeCycleTimer.schedule(new LocalTask(this), delay);
        }
    }

    private enum Updater{
        ActionUpdater {
            @Override
            void initUpdate() {
                log.debug("ActionUpdater");
                List<Action> updatedActions = api.getAllActions();
                if (!updatedActions.equals(actionMemory)){
                    Action.actions = (actionMemory = updatedActions);
                    tasktimer.cancel();
                    tasktimer = new Timer();
                    for (Action action:Action.actions){
                        scheduleTask(new MyPostWriter(action,bot));
                    }
                }

            }
        },
        CategoryUpdater{
            @Override
            void initUpdate() {
                log.debug("CategoryUpdater");
                List<Category> updatedCategories = api.getAllCategories();
                if (!updatedCategories.equals(categoryMemory)){
                    Category.categories = (categoryMemory = updatedCategories);
                }
            }
        },
        ShopUpdater{
            @Override
            void initUpdate() {
                log.debug("ShopUpdater");
                List<Shop> updatedShops = api.getAllShops();
                if (!updatedShops.equals(shopsMemory)){
                    Shop.shops = (shopsMemory = updatedShops);
                }
            }
        };
        abstract void initUpdate();
        private static AirTableApi api = AirTableApi.getApi();
        private static List<Action> actionMemory = null;
        private static List<Shop> shopsMemory = null;
        private static List<Category> categoryMemory = null;
    }

}
