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
        moveRandomly(agent);
    }

    @Override
    public void communicate(AgentImp agent) {

    }

    private void moveRandomly(AgentImp agent) {
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        var perception = agent.getPerception();

        for (var move : moves) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (agent.getLastArea() != null && agent.getLastArea().getX() == agent.getX() + x && agent.getLastArea().getY() == agent.getY() + y) continue; // Don't undo a move
                agent.step(agent.getX() + x, agent.getY() + y);
                return;
            }
        }
        agent.skip();
    }
}
