package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import agent.behaviour.energyconstrained.CommunicateDropoff;


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
