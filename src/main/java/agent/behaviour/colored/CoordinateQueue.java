package agent.behaviour.colored;

import agent.AgentImp;

/**
 * A class to handle the help coordinate queue in the agents memory.
 */
public class CoordinateQueue {
    public static String splitSymbol = ";";

    public CoordinateQueue() {
    }

    /**
     * Add a coordinate at the end of the queue or create a new queue with the coordinate
     * if the queue did not exist.
     */
    public static void addCoordinate(AgentImp agent, String coordinate) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) == null) {
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, coordinate + ";");
        } else {
            String oldQueue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            if (!oldQueue.contains(coordinate)) {
                String newQueue = oldQueue + coordinate + ";";
                agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
            }
        }
    }

    /**
     * Insert a coordinate at the beginning of the queue. Necessary for urgent help coordinates like
     * coordinates with your own colour. The agent should fix them first before he can get to the next
     * help message.
     */
    public static void insertCoordinate(AgentImp agent, String coordinate) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) == null) {
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, coordinate + ";");
        } else {
            String oldQueue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            if (oldQueue.contains(coordinate)) {
                oldQueue = oldQueue.replace(coordinate + ";", "");
            }
            String newQueue = coordinate + ";" + oldQueue;
            agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
        }
    }

    /**
     * @return Returns the first coordinate from the queue.
     */
    public static String getFirst(AgentImp agent) {
        String queue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
        if (queue != null) {
            return queue.split(splitSymbol)[0];
        }
        return null;
    }

    /**
     * Remove the first coordinate from the queue.
     */
    public static void remove(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY) != null) {
            String queue = agent.getMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            String newQueue = queue.substring(queue.indexOf(splitSymbol) + 1);
            if (!newQueue.equals("")) {
                agent.addMemoryFragment(AgentImp.HELP_QUEUE_KEY, newQueue);
            } else {
                agent.removeMemoryFragment(AgentImp.HELP_QUEUE_KEY);
            }
        }
    }
}
