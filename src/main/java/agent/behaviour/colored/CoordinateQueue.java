package agent.behaviour.colored;

import agent.AgentImp;
import environment.Collector;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateQueue {
    public static String splitSymbol = ";";

    public CoordinateQueue() {
    }

    public void addCoordinate(AgentImp agent, String coordinate) {
        if (agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null) {
            agent.addMemoryFragment(AgentImp.HELP_MESSAGE_KEY, coordinate+";");
        }
        else {
            String newQueue = agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY)+coordinate+";";
            agent.addMemoryFragment(AgentImp.HELP_MESSAGE_KEY, newQueue);
        }
    }

    public static ArrayList<Coordinate> stringToList(String memoryString){
        String[] stringList = memoryString.split(splitSymbol);
        return Arrays.stream(stringList).map(Coordinate::fromString).collect(Collectors.toCollection((ArrayList<Coordinate>::new)));
    }

    public void remove(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null) {
            return;
        }
        else {
            String queue = agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY);
            queue.substring(queue.indexOf(splitSymbol)+1);
            agent.addMemoryFragment(AgentImp.HELP_MESSAGE_KEY, queue);
        }
    }
}
