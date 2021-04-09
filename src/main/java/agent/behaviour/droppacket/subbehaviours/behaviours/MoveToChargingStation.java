package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;

public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        System.out.println("x: " + cell.getX() + "   y: "+ cell.getY());
        agent.step(cell.getX(), cell.getY());
    }



    @Override
    public void communicate(AgentImp agent) {
        //
    }
}
