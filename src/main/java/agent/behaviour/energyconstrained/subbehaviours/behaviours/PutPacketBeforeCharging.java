package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;

/**
 * This behaviour makes an agent put his packet down before moving towards a charging station,
 * in order to save energy spent and to make sure no agent dies with the packet still in his hands
 */
public class PutPacketBeforeCharging extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        // Put the packet down
        CellPerception cellToPutOn = getCellToPutPacketOn(agent);
        if (cellToPutOn != null) {
            agent.putPacket(cellToPutOn.getX(), cellToPutOn.getY());
        } else {
            System.out.println("PUT LOCATION IS NULL");
            agent.skip();
        }
    }

    private CellPerception getCellToPutPacketOn(AgentImp agent) {
        CellPerception bestGradientCell = agent.getCellWithBestGradient();
        for (CellPerception cell: agent.getPerception().getNeighbours()) {
            if (cell != null && cell.isWalkable() && cell != bestGradientCell)
              return cell;
        }
        return null;
    }


    @Override
    public void communicate(AgentImp agent) {
        //
    }
}
