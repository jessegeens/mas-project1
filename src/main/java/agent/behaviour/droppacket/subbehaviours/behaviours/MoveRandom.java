package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.AgentRep;
import util.CommunicateDropoff;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.MemoryParser.parseColorFromMessage;
import static util.MemoryParser.parseCoordinateFromMessage;

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
        ArrayList<Mail> mail = new ArrayList(agent.getMessages());
        CommunicateDropoff.communicateDropOff(agent, mail);
    }


}
