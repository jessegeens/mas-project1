package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveRandom extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate coordinate = agent.generateRandomMove();
        if (coordinate != null)
            agent.step(coordinate.getX(), coordinate.getY());
        else
            agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {

    }



}
