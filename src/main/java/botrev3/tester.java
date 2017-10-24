package botrev3;

import botrev3.domens.Action;
import botrev3.tlgrm.ChatThread;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aalbutov on 20.10.2017.
 */
public class tester {
    public static void main(String args[]) throws IOException, GeneralSecurityException {
        dateFormatterTest();
    }

    public static void dateFormatterTest(){
        System.out.println(new Date());
        Action action = Action.getActionForId("1");
        action.setTime("01 12:24");
        System.out.println(action.getTimeAsDate());
        action.setTime("31 12:24");
        System.out.println(action.getTimeAsDate());
        action.setTime("23 12:24");
        System.out.println(action.getTimeAsDate());
        action.setTime("25 12:24");
        System.out.println(action.getTimeAsDate());
        action.setTime("24 09:00");
        System.out.println(action.getTimeAsDate());
    }

    public static void addActionTest(){
        Action act = Action.getActionForId(null);
        act.setImage("dsfgsdg");
        act.setDescription("dsfgsdg");
        act.setLink("dsfgsdg");
        act.setTime("dsfgsdg");
        act.setPriceX100(12200);
        AirTableApi airTableApi = AirTableApi.getApi();
        System.out.println(airTableApi.addAction(act));
    }

    public static void textProcTest(){
        TextProcessor proc = new TextProcessor();
        AirTableApi airTableApi = AirTableApi.getApi();
        System.out.println(proc.changeLink("grbe.st/AwYQJ", 360));
    }

    public static void linkMatcherTest() {
        String text = "dsf asfd asdfasdf asdfsadf sadffdsa asdf. asdff4asdf.df/3fs sdfs";
        Pattern linkp = Pattern.compile(ChatThread.LINK_PATTERN);
        Matcher linkm = linkp.matcher(text);
        System.out.println(linkm.find());
        System.out.println(linkm.group());
    }
}
