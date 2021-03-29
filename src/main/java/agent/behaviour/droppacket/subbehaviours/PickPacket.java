package agent.behaviour.droppacket.subbehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;

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
