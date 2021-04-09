package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;

public class Charge extends LTDBehaviour {


    @Override
    public void act(AgentImp agent) {
        agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        //
    }
}
