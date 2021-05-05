package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import agent.behaviour.energyconstrained.CommunicateDropoff;

/**
 * This behaviour makes an agent move randomly, for example when looking for a packet/drop-off point or when
 * sending request-for-help messages
 */
public class MoveRandom extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        // Find a random neighbouring cell to move to
        Coordinate coordinate = agent.generateRandomMove();
        if (coordinate != null)
            agent.step(coordinate.getX(), coordinate.getY());
        else
            agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        CommunicateDropoff.communicateDropOff(agent);
    }


}
