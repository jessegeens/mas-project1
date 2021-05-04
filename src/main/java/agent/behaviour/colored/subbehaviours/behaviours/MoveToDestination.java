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

public class MoveToDestination extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(AgentImp.DESTINATION_KEY)));
    }

    @Override
    public void communicate(AgentImp agent) {
        CommunicateHelp.manageHelp(agent);
    }

    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    private void moveTo(AgentImp agent, Coordinate destination) {
        boolean shouldSkip = false;
        Path path = Dijkstra.calculateDijkstra(agent, destination, false);
        if (!path.isWalkablePath()) {
            Pair<Coordinate, Color> packetInfo = path.getPacketInfoOfFirstBlockingPath();
            if (agent.hasCarry()) {
                System.out.println(agent.getName() + " path: " + path.toString());
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
            System.out.println(agent.getName() + ": avoiding deadlock with " + rand + "rand moves");
            agent.addMemoryFragment(AgentImp.AVOID_DEADLOCK, String.valueOf(rand));
        } else {
            agent.addMemoryFragment(AgentImp.SKIP_DETECTION, new Coordinate(agent.getX(), agent.getY()).toString());
        }
        if (shouldSkip) {
            agent.skip();
        } else if (path.getPathCoordinate().size() == 0) {
            System.out.println(agent.getName() + " has no path :(");
            agent.skip();
        } else {
            agent.step(path.getPathCoordinate().get(0).getX(), path.getPathCoordinate().get(0).getY());
        }
    }

    private boolean atSamePosAsPrev(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.SKIP_DETECTION) == null) return false;
        Coordinate prev = Coordinate.fromString(agent.getMemoryFragment(AgentImp.SKIP_DETECTION));
        Coordinate curr = new Coordinate(agent.getX(), agent.getY());
        System.out.println(agent.getName() + ": curr: " + curr + "; last: " + prev);
        return curr.equalsCoordinate(prev);

    }

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