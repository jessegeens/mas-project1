package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;
import environment.Perception;
import agent.behaviour.energyconstrained.CommunicateDropoff;

import java.util.*;
import java.util.List;

public class MoveToDestination extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY)));
    }

    @Override
    public void communicate(AgentImp agent) {
        CommunicateDropoff.communicateDropOff(agent);
    }

    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    /**
     * This is the main method of this behaviour.
     * First, a "best move" is calculated. This is the move that makes the agent move to a walkable cell with the lowest
     * distance to the destination of the options. Then, if there is a "best move", the agent moves there.
     * Finally, there is a "loop detection" check, where we check that an agent does not keep moving back and forth.
     * If it is true, a boolean is set in memory that will make the agent move randomly
     */
    private void moveTo(AgentImp agent, Coordinate destination) {
        var perception = agent.getPerception();
        Coordinate agentCoord = new Coordinate(agent.getX(), agent.getY());
        Coordinate currentBestMove = null;
        for (var move : POSSIBLE_MOVES) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (currentBestMove == null) currentBestMove = move;
                else if (isCloser(Coordinate.getSum(agentCoord, move), Coordinate.getSum(agentCoord, currentBestMove), destination))
                    currentBestMove = move;
            }
        }

        if (currentBestMove == null) agent.skip();
        else {
            Coordinate newCoord = new Coordinate(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
            if (agent.getLastArea() != null && newCoord.equalsCoordinate(new Coordinate(agent.getLastArea().getX(), agent.getLastArea().getY()))) {
                agent.addMemoryFragment(DropPacket.LOOP_DETECTION_KEY, "true");
            }
            agent.step(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
        }
    }


    /**
     * @return true iff `first` is closer to `dest` than `second`
     */
    private boolean isCloser(Coordinate first, Coordinate second, Coordinate dest) {
        if (first == null) return false;
        else if (second == null) return true;
        else {
            int distDestToFirst = Perception.distance(dest.getX(), dest.getY(), first.getX(), first.getY());
            int distDestToSecond = Perception.distance(dest.getX(), dest.getY(), second.getX(), second.getY());
            return distDestToFirst < distDestToSecond;
        }
    }
}