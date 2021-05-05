package agent.behaviour.autonomousbehaviour.subbehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;


/**
 * This behaviour makes an agent pick up a packet from a coordinate that is set in memory. Afterwards, the agent will
 * search his whole perception for a drop off point by setting `search_all` to true
 */
public class PickPacket extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY));
        try {
            agent.pickPacket(destination.getX(), destination.getY());
        } catch (RuntimeException e) {
            System.out.println(agent.getName() + ": " + e.getMessage());
            agent.skip();
            // If packet is already gone, destination will become null and agent should go to move randomly
        }
        agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    @Override
    public void communicate(AgentImp agent) {

    }
}
