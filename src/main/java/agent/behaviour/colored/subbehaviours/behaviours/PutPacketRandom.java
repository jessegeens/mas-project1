package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.autonomousbehaviour.DropPacket;
import agent.behaviour.colored.CommunicateHelp;
import environment.Coordinate;

/**
 * The behaviour of an agent when he puts a packet on a random position and not on a destination.
 */
public class PutPacketRandom extends LTDBehaviour {

    /**
     * The agent will get the random position to put the packet from its memory and tries to put it
     * down there, or skips if this fails. The random put coordinate is removed from memory.
     */
    @Override
    public void act(AgentImp agent) {
        Coordinate coordinateToPutOn = getCoordinateToPutPacketOn(agent);
        if (coordinateToPutOn != null) {
            try {
                agent.putPacket(coordinateToPutOn.getX(), coordinateToPutOn.getY());
            } catch (RuntimeException e) {
                System.out.println(agent.getName() + ": " + e.getMessage());
                agent.skip();
            }
        } else {
            agent.skip();
        }
        agent.removeMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY);
        agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "true");
    }

    /**
     * All necessary communications for help with the coloured packets will be executed.
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }

    /**
     *
     * @return Returns the random coordinate to put the packet on from memory.
     */
    private Coordinate getCoordinateToPutPacketOn(AgentImp agent) {
        return Coordinate.fromString(agent.getMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY));
    }
}
