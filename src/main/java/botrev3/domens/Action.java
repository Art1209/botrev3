package botrev3.domens;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Action {

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
