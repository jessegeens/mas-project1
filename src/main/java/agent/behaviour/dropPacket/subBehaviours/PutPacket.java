package agent.behaviour.dropPacket.subBehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import agent.behaviour.dropPacket.DropPacket;

public class PutPacket extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY));
        try {
            agent.putPacket(destination.getX(), destination.getY());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            // If destination changed, destination will become null and agent should go to move randomly
        }
        agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    @Override
    public void communicate(AgentImp agent) {

    }
}