package botrev3.tlgrm.priceChecker;

import java.io.*;

public class TasksSaver {
    File file = new File ("backup");
    void save(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            for (PriceChecker checker: CheckerBot.checkers.values()){
                out.writeObject(checker);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void restore(CheckerBot bot){
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            try {
                while (in.available()>=0){
                    PriceChecker c = (PriceChecker) in.readObject();
                    if (c!=null){
                        c.setBot(bot);
                        c.setSilent(false);
                        CheckerBot.checkers.put(c.getLink(),c);
                    }
                }
            } catch (EOFException e){}
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("io");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("class");
            e.printStackTrace();
        }
        for (PriceChecker checker: CheckerBot.checkers.values()){
            bot.timer.schedule(checker);
        }
    }
}
