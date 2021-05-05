package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import agent.behaviour.colored.CoordinateQueue;
import environment.Coordinate;

/**
 * The behaviour of an agent when he picks a packet.
 */
public class PickPacket extends LTDBehaviour {

    /**
     * The agent will try to pick a packet of his destination. If the packet is already gone he will
     * skip. His current destination is removed from memory and if the destination was in his help
     * queue this will also be removed from the help queue.
     */
    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(AgentImp.DESTINATION_KEY));
        try {
            agent.pickPacket(destination.getX(), destination.getY());
        } catch (RuntimeException e) {
            System.out.println(agent.getName() + ": " + e.getMessage());
            agent.skip(); // If packet is already gone, destination will become null and agent should go to move randomly
        }
        agent.removeMemoryFragment(AgentImp.DESTINATION_KEY);
        agent.addMemoryFragment(AgentImp.SEARCH_ALL_KEY, "true");
        if (CoordinateQueue.getFirst(agent) != null && CoordinateQueue.getFirst(agent).equals(destination.toString())) {
            CoordinateQueue.remove(agent);
        }
    }

    /**
     * All necessary communications for help with the coloured packets will be executed.
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }
}
