package agent.behaviour.colored;

import agent.AgentImp;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.AgentRep;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to facilitate communication to help.
 */
public class CommunicateHelp {

    /**
     * Receive help messages, receive acknowledgments and ask for help if necessary.
     */
    public static void manageHelp(AgentImp agent) {
        receiveHelpMessage(agent);
        receiveAcknowledge(agent);
        askForHelp(agent);
    }

    /**
     * If the agent has a help message with the right color, the help coordinate is added to his
     * help queue. An acknowledgment is sent that the message was received.
     */
    public static void receiveHelpMessage(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()) {
            String msg = mail.getMessage();
            if (msg.length() > 4 && msg.substring(0, 4).equals("help")) {
                if (parseColorFromMessage(msg).equals(agent.getAgentColor().toString())) {
                    CoordinateQueue.addCoordinate(agent, parseCoordinateFromMessage(msg));
                    List<AgentRep> agents = agent.getPerception().findNearbyAgents();
                    for (AgentRep receiver: agents) {
                        if (String.valueOf(receiver.getID()).equals(mail.getFrom())) {
                            acknowledge(agent, receiver);
                        }
                    }
                }
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }

    /**
     * If the agent has a help message he will send it to all agents in his perception.
     */
    public static void askForHelp(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null) {
            List<AgentRep> agents = agent.getPerception().findNearbyAgents();
            for (AgentRep receiver: agents) {
                String msg = agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY);
                agent.sendMessage(receiver, msg);
            }
        }
    }

    /**
     * Send an acknowledgement.
     */
    public static void acknowledge(AgentImp sender, AgentRep receiver) {
        sender.sendMessage(receiver, "acknowledge");
    }

    /**
     * Receive an acknowledgment and remove your help message from the queue.
     */
    public static void receiveAcknowledge(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()) {
            String msg = mail.getMessage();
            if (msg.equals("acknowledge")) {
                agent.removeMemoryFragment(AgentImp.HELP_MESSAGE_KEY);
                toDelete.add(mail);
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

    public static String constructHelpMessage(Coordinate coordinate, Color color) {
        return "help;" + color.toString() + ";" + coordinate.toString();
    }
}
