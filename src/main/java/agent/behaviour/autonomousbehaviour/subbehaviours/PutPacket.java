package agent.behaviour.autonomousbehaviour.subbehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;

/**
 * This behaviour makes an agent drop off a packet at a coordinate that is set in memory. Afterwards, the agent will
 * search his whole perception for a new packet to pickup by setting `search_all` to true
 */
public class PutPacket extends LTDBehaviour {

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

    @Override
    public void communicate(AgentImp agent) {

    }
}
