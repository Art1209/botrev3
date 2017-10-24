package botrev3.domens;

import botrev3.tlgrm.ChatThread;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Action{

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy MM "+ ChatThread.TIME_FORMAT);
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
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        int year = Year.now().getValue();
        try {
            timeToStart = format.parse(year+" "+monthToString(month) +" " +getTime());
            if ((long)today.getTime()-timeToStart.getTime()>432000000l) { // 5 days in mlsec
                if (month==12){
                    timeToStart = format.parse((year+1)+" 01 "+getTime());
                } else {
                    timeToStart = format.parse(year+" "+monthToString(month+1)+" "+getTime());
                }
            }
            long changeTimeZoneHours = ChatThread.TARGET_TIME_ZONE-ChatThread.TIME_ZONE;
            timeToStart = new Date(timeToStart.getTime()-(changeTimeZoneHours*3600000l)); // 1 our in mlsec
        } catch (ParseException e) {}
        return timeToStart;
    }

    private static String monthToString(int month){
        return (month/10+""+month%10);
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

    public void setPriceAsString(String priceAsString) {
       priceX100 = 100*(int)(Double.parseDouble((priceAsString)));
    }
}
