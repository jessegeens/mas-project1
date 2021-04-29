package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;

public class PutPacketRandom extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate coordinateToPutOn = getCoordinateToPutPacketOn(agent);
        if (coordinateToPutOn != null){
            agent.putPacket(coordinateToPutOn.getX(), coordinateToPutOn.getY());
        }else{//TODO: geval waarbij het enige vakje dat vrij is het vakje is waar je naartoe wil gaan
            agent.skip();
        }
        agent.removeMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    @Override
    public void communicate(AgentImp agent) {

    }

    private Coordinate getCoordinateToPutPacketOn(AgentImp agent){
        return Coordinate.fromString(agent.getMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY));
    }
}
