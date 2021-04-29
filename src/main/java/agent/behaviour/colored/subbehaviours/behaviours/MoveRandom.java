package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import environment.Coordinate;
import environment.Mail;
import util.CommunicateDropoff;

import java.util.ArrayList;

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
        CommunicateDropoff.communicateDropOff(agent);
        CommunicateHelp.manageHelp(agent);
    }


}
