package agent.behaviour.energyconstrained;

import agent.AgentImp;
import environment.Mail;
import environment.world.agent.AgentRep;

import java.util.ArrayList;
import java.util.List;

public class CommunicateDropoff {

    /**
     * This function makes an agent communicate his drop-off locations to other agents, so that they don't have to look
     * for them anymore themselves. First, incoming messages are checked for so-called "destination" messages. The new
     * location is then added to memory.
     * Next, a message is sent to all nearby agents with the coordinates of all drop-off points in the agent's memory
     */
    public static void communicateDropOff(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()) {
            String msg = mail.getMessage();
            if (msg.length() > 4 && msg.substring(0, 4).equals("dest")) {
                String color = parseColorFromMessage(msg);
                if (agent.getMemoryFragment(color) == null)
                    agent.addMemoryFragment(color, parseCoordinateFromMessage(msg));
                toDelete.add(mail);
            }
        }
        List<AgentRep> agents = agent.getPerception().findNearbyAgents();
        if (agent.getMemoryFragment("colors") != null) {
            for (AgentRep receiver: agents) {
                String[] colors = agent.getMemoryFragment("colors").split(";");
                for (String color : colors) {
                    String coord = agent.getMemoryFragment(color);
                    agent.sendMessage(receiver, "dest;" + color + ";" + coord);
                }
            }
        }
        agent.removeMessages(toDelete);
    }

    private static String parseColorFromMessage(String msg) {
        return msg.split(";")[1];
    }

    private static String parseCoordinateFromMessage(String msg) {
        return msg.split(";")[2];
    }

}
