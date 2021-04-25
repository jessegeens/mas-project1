package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;

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
