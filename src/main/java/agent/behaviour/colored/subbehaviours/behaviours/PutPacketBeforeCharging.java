package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;

public class PutPacketBeforeCharging extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cellToPutOn = getCellToPutPacketOn(agent);
        if (cellToPutOn != null){
            agent.putPacket(cellToPutOn.getX(), cellToPutOn.getY());
        }else{//TODO: geval waarbij het enige vakje dat vrij is het vakje is waar je naartoe wil gaan
            System.out.println("PUT LOCATION IS NULL");
            agent.skip();
        }
    }

    private CellPerception getCellToPutPacketOn(AgentImp agent){
        CellPerception bestGradientCell = agent.getCellWithBestGradient();
        for (CellPerception cell: agent.getPerception().getNeighbours()){

            if (cell != null && cell.isWalkable() && cell != bestGradientCell){
              return cell;
            }
        }
        return null;
    }


    @Override
    public void communicate(AgentImp agent) {
        //
    }
}
