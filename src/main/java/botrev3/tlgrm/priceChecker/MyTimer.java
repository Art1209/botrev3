package botrev3.tlgrm.priceChecker;


import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyTimer implements Runnable{
    private static final MyTimer INSTANCE = new MyTimer();
    private MyTimer(){
    }
    public static MyTimer getMyTimer(){
        return INSTANCE;
    }


    private List<TimerTask> tasks = new ArrayList<>();
    private ExecutorService exec = Executors.newFixedThreadPool(5);
    private ExecutorService selfExec = Executors.newSingleThreadExecutor();

    private LifeCycle state= LifeCycle.Waiting;
    synchronized void schedule(TimerTask task){
        tasks.add(task);
        if (!isRunning())selfExec.execute(this);
    }

    public void run() {
        state = LifeCycle.Running;
        PriceChecker currentTask;
        while (!tasks.isEmpty()){
            currentTask = (PriceChecker)tasks.get(0);
            if (CheckerBot.checkers.containsKey(currentTask.getLink())){
                exec.execute(currentTask);
            }
            tasks.remove(0);
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        state = LifeCycle.Waiting;
    }

    public boolean isRunning(){
        return state== LifeCycle.Running;
    }
    enum LifeCycle {
        Running, Waiting;
    }
}
