package botrev3;

import botrev3.domens.Action;
import botrev3.tlgrm.BlogBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimerTask;

/**
 * Created by aalbutov on 23.10.2017.
 */

@Log4j
public class MyPostWriter extends TimerTask {

    private static String panda;

    static {
//        try {
        panda = "🐼";
//                    URLEncoder.encode("\\xF0\\x9F\\x90\\xBC", "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    @Getter @Setter
    private Action action;

    @Getter @Setter
    private BlogBot bot;



    public MyPostWriter(Action action, BlogBot bot) {
        this.action = action;
        this.bot = bot;
    }

    public MyPostWriter() {
    }

    @Override
    public void run() {
        String link = action.getLink();
        String desc = action.getDescription();
        if (desc == null) desc = "";
        if (link == null) link = "";
        String text = panda + " " + desc + " " + action.getLink();
        log.info("posting right now: "+text);
        if (action.getImage()!=null&&action.getImage().trim()!=""){
            bot.sendPhotoTextToChannel(text,action.getImage());
        } else {
            bot.sendTextToChannel(text);
        }
    }
}
