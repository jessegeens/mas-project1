package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;

public class PutPacketRandom extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate coordinateToPutOn = getCoordinateToPutPacketOn(agent);
        if (coordinateToPutOn != null) {
            agent.putPacket(coordinateToPutOn.getX(), coordinateToPutOn.getY());
        } else {
            //TODO: verifieren
            System.out.println("Agent skipping in PutPacketRandom");
            agent.skip();
        }
        agent.removeMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    @Override
    public void communicate(AgentImp agent) {

    }

    private Coordinate getCoordinateToPutPacketOn(AgentImp agent) {
        return Coordinate.fromString(agent.getMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY));
    }
}
