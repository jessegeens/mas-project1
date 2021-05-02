package agent.behaviour.colored;

import agent.AgentImp;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CoordinateQueue {
    public static String splitSymbol = ";";

    public CoordinateQueue() {
    }

    public static void addCoordinate(AgentImp agent, String coordinate) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) == null) {
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, coordinate+";");
        }
        else {
            String oldQueue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            if (!oldQueue.contains(coordinate)) {
                String newQueue = oldQueue + coordinate + ";";
                agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
            }
        }
    }

    public static void insertCoordinate(AgentImp agent, String coordinate) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) == null) {
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, coordinate+";");
        }
        else {
            String oldQueue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            if (oldQueue.contains(coordinate)) {
                oldQueue = oldQueue.replace(coordinate+";", "");
            }
            String newQueue = coordinate + ";"+oldQueue;
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
        }
    }

    public static String getFirst(AgentImp agent) {
        String queue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
        if (queue != null) {
            return queue.split(splitSymbol)[0];
        }
        return null;
    }

    public static ArrayList<Coordinate> stringToList(String memoryString){
        String[] stringList = memoryString.split(splitSymbol);
        return Arrays.stream(stringList).map(Coordinate::fromString).collect(Collectors.toCollection((ArrayList<Coordinate>::new)));
    }

    public static void remove(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) == null) {
            return;
        }
        else {
            String queue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            String newQueue = queue.substring(queue.indexOf(splitSymbol)+1);
            if (!newQueue.equals("")) {
                agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
            }
            else {
                agent.removeMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            }
        }
    }
}
