package agent.behaviour.dropPacket.subBehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import agent.behaviour.dropPacket.DropPacket;

public class PickPacket extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY));
        try {
            agent.pickPacket(destination.getX(), destination.getY());
        } catch (RuntimeException e) {
            System.out.println(agent.getName() + ": " + e.getMessage());
            // If packet is already gone, destination will become null and agent should go to move randomly
        }
        agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    @Override
    public void communicate(AgentImp agent) {

    }
}
