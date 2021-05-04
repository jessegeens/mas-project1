package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;

/**
 * The behaviour of an agent when he puts a packet down.
 */
public class PutPacket extends LTDBehaviour {

    /**
     * Puts a packet down on the destination in memory. If this fails the agent will skip.
     * The destination is removed from memory.
     */
    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY));
        try {
            agent.putPacket(destination.getX(), destination.getY());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            agent.skip();
            // If destination changed, destination will become null and agent should go to move randomly
        }
        agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    /**
     * All necessary communications for help with the coloured packets will be executed.
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }
}
