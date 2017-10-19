package botrev3.flow;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class TasksKeeper {
    private Timer timer = new Timer();
    private List<CustomTask> tasks = new ArrayList<>();

    private static TasksKeeper keeper;
    private TasksKeeper(){
        restore();
    }
    public static TasksKeeper getTasksKeeper(){
        if (keeper ==null)keeper = new TasksKeeper();
        return keeper;
    }

    public synchronized void register(CustomTask task){
        tasks.add(task);
        timer.schedule(task,new Date(task.getNextLaunchTime()));
        backup();
    }

    public synchronized void unregister(CustomTask task){
        tasks.remove(task);
    }

    private void backup(){
        try {
            FileOutputStream fs = new FileOutputStream(new File("backup"));
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void restore(){
        try {
            FileInputStream fs = new FileInputStream(new File("backup"));
            ObjectInputStream is = new ObjectInputStream(fs);
            tasks = (ArrayList<CustomTask>)is.readObject();
            for (CustomTask task:tasks){
                timer.schedule(task,task.getNextLaunchTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    public String tasksToString(){
        String result = "";
        for (CustomTask task: tasks) {
            result+=task.toString()+System.lineSeparator();
        }
        return result;
    }
}
