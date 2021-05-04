package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import agent.behaviour.colored.CoordinateQueue;
import agent.behaviour.colored.Dijkstra;

import agent.behaviour.colored.subbehaviours.Path;
import environment.CellPerception;
import environment.Coordinate;
import util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The behaviour of an agent when he moves to a destination.
 */
public class MoveToDestination extends LTDBehaviour {

    /**
     * The agent will move towards the destination stored in memory.
     */
    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(AgentImp.DESTINATION_KEY)));
    }

    /**
     * All necessary communications for help with the coloured packets will be executed.
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }

    /**
     * A path is calculated from the agents position to the given destination. If the path is blocked
     * by a packet, the agent should put his packet down if he has one. He will ask for help or go
     * to that packet if it has his colour.
     * If the agent is in a deadlock he should start to move randomly.
     * If necessary the agent will skip or move to the next position of the calculated path.
     */
    private void moveTo(AgentImp agent, Coordinate destination) {
        boolean shouldSkip = false;
        Path path = Dijkstra.calculateDijkstra(agent, destination, false);
        if (!path.isWalkablePath()) {
            Pair<Coordinate, Color> packetInfo = path.getPacketInfoOfFirstBlockingPath();
            if (agent.hasCarry()) {
                Coordinate randomPutCoordinate = getNeighbourNotOnPath(agent, path);
                agent.addMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY, randomPutCoordinate.toString());
                shouldSkip = true;
            }
            if (packetInfo.second == agent.getAgentColor()) {
                agent.addMemoryFragment(AgentImp.DESTINATION_KEY, packetInfo.first.toString());
                CoordinateQueue.insertCoordinate(agent, packetInfo.first.toString());
            } else {
                String helpmsg = CommunicateHelp.constructHelpMessage(packetInfo.first, packetInfo.second);
                agent.addMemoryFragment(AgentImp.HELP_MESSAGE_KEY, helpmsg);
            }
        }
        if (atSamePosAsPrev(agent) && !agent.isNeighbour(destination)) {
            int max = 5;
            int rand = (new Random()).nextInt(max) + 1;
            agent.addMemoryFragment(AgentImp.AVOID_DEADLOCK, String.valueOf(rand));
        } else {
            agent.addMemoryFragment(AgentImp.SKIP_DETECTION, new Coordinate(agent.getX(), agent.getY()).toString());
        }
        if (shouldSkip) {
            agent.skip();
        } else if (path.getPathCoordinate().size() == 0) {
            agent.skip();
        } else {
            agent.step(path.getPathCoordinate().get(0).getX(), path.getPathCoordinate().get(0).getY());
        }
    }

    /**
     *
     * @return Returns whether the agent is on the same position as his previous position.
     */
    private boolean atSamePosAsPrev(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.SKIP_DETECTION) == null) return false;
        Coordinate prev = Coordinate.fromString(agent.getMemoryFragment(AgentImp.SKIP_DETECTION));
        Coordinate curr = new Coordinate(agent.getX(), agent.getY());
        return curr.equalsCoordinate(prev);
    }

    /**
     *
     * @return Returns a neighbour position of the agent that is not on the given path. Returns null if such
     * a position cannot be found.
     */
    public Coordinate getNeighbourNotOnPath(AgentImp agent, Path path) {
        CellPerception[] notNullNeighbours = Arrays.stream(agent.getPerception().getNeighbours()).filter(Objects::nonNull).toArray(CellPerception[]::new);
        List<Coordinate> neighbours = Arrays.stream(notNullNeighbours).map((perception) -> new Coordinate(perception.getX(), perception.getY())).collect(Collectors.toList());
        String pathString = path.getPathCoordinate().toString();
        for (Coordinate neighbour: neighbours) {
            if (!pathString.contains(neighbour.toString())) {
                return neighbour;
            }
        }
        return null;
    }

}