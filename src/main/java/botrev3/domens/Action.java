package botrev3.domens;

import botrev3.tlgrm.ChatThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Log4j
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

    private ActionOld old;

    public String getPriceAsString(){
        return (getPriceX100()/100)+"."+(getPriceX100()%100);
    }

    public void setPriceAsString(String priceAsString) {
        if (priceAsString == null) {
            priceX100 = 100;
            return;
        }
        try {
            priceX100 = 100 * (int) (Double.parseDouble((priceAsString)));
        } catch (NumberFormatException e) {
            log.warn("Wrong price format add Action task");
        }
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


    public void saveOld() {
        if (old == null) old = new ActionOld();
        old.oldId = id;
        old.oldDescription = description;
        old.oldImage = image;
        old.oldLink = link;
        old.oldTime = time;

    }

    public boolean saveReturnChanged() { //if changed "from A to B" or "from null to B"
        boolean ch = false;
        if (old.oldTime == null ? time != null : !old.oldTime.equals(time)) {
            ch = true;
        }
        if (old.oldLink == null ? link != null : !old.oldLink.equals(link)) {
            ch = true;
        }
        if (old.oldImage == null ? image != null : !old.oldImage.equals(image)) {
            ch = true;
        }
        if (old.oldDescription == null ? description != null : !old.oldDescription.equals(description)) {
            ch = true;
        }
        saveOld();
        return ch;
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
        if (obj instanceof Action) {
            Action act2 = (Action) obj;
            String id2 = act2.getId();
            String link2 = act2.getLink();
            String description2 = act2.getDescription();
            String image2 = act2.getImage();
            String time2 = act2.getTime();
            if ((id != null ? id.equals(id2) : id == id2) &&
                    (link != null ? link.equals(link2) : link == link2) &&
                    (description != null ? description.equals(description2) : description == description2) &&
                    (image != null ? image.equals(image2) : image == image2) &&
                    (time != null ? time.equals(time2) : time == time2)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getId() + " " + getDescription() + " " + getTime();
    }

    private static class ActionOld {
        private String oldId;
        private String oldLink;
        private String oldDescription;
        private String oldImage;
        private String oldTime;
    }

}
