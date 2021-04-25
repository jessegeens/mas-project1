package agent.behaviour.colored;

import agent.AgentImp;
import environment.Mail;

import java.util.ArrayList;

public class communicateHelp {
    public static void receiveHelpMessage(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()){
            String msg = mail.getMessage();
            if(msg.length() > 4 && msg.substring(0,4).equals("help")){

                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }

    public static void askForHelp(AgentImp agent) {

    }
}
