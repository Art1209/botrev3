package botrev3;

import botrev3.domens.Action;
import botrev3.tlgrm.ChatThread;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aalbutov on 20.10.2017.
 */
public class tester {
    public static void main(String args[]) throws IOException, GeneralSecurityException {
        textProcTest();
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
