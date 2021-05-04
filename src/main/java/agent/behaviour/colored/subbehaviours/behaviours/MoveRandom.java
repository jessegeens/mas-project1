package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import environment.Coordinate;

/**
 * The behaviour of an agent when he moves random.
 */
public class MoveRandom extends LTDBehaviour {

    /**
     * The agent will generate a random move and execute this move. If a random move cannot be
     * generated the agent will skip.
     */
    @Override
    public void act(AgentImp agent) {
        Coordinate coordinate = agent.generateRandomMove();
        if (coordinate != null)
            agent.step(coordinate.getX(), coordinate.getY());
        else
            agent.skip();
    }

    /**
     * All necessary communications for help with the coloured packets will be executed.
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }


}
