package botrev3.domens;

import botrev3.tlgrm.ChatThread;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Action{

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy "+ ChatThread.TIME_FORMAT);
    public static List<Action> actions = new ArrayList<>();

    public static Action getActionForId(String givenId){
        for (Action action:actions){
            if (action.getId().equals(givenId))return action;
        }
        Action newAction = new Action(givenId);
        actions.add(newAction);
        return new Action(givenId);
    }

    private Action(String id) {
        this.id = id;
    }

    private Action() {
    }

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String link;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String image;

    @Getter @Setter
    private String time;

    @Getter @Setter
    private int priceX100;

    public String getPriceAsString(){
        return (getPriceX100()/100)+"."+(getPriceX100()%100);
    }

    public Date getTimeAsDate(){
        Date timeToStart = null;
        Date today = new Date();
        try {
            timeToStart = format.parse(Year.now().getValue()+" "+getTime());
            if (today.getTime()-timeToStart.getTime()>3600000) {
                timeToStart = format.parse((Year.now().getValue())+" "+getTime());
            }
        } catch (ParseException e) {}
        return timeToStart;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getId().equals(obj);
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
