package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.CommunicateHelp;
import agent.behaviour.colored.CoordinateQueue;
import agent.behaviour.colored.Dijkstra;

import agent.behaviour.colored.subbehaviours.Path;
import environment.CellPerception;
import environment.Coordinate;
import util.CommunicateDropoff;
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
        CommunicateDropoff.communicateDropOff(agent);
        CommunicateHelp.manageHelp(agent);
    }

    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    private void moveTo(AgentImp agent, Coordinate destination) {
        System.out.println(agent.getID()+" destination = "+destination);
        Path path = Dijkstra.calculateDijkstra(agent, destination, false);
        //System.out.println(path.toString());
        if (!path.isWalkablePath()) {
            Pair<Coordinate, Color> packetInfo = path.getPacketInfoOfFirstBlockingPath();
            if (agent.hasCarry()) {
                System.out.println(agent.getID()+" hasCarry");
                Coordinate randomPutCoordinate = getNeighbourNotOnPath(agent, path);
                agent.addMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY, randomPutCoordinate.toString());
            }
            if (packetInfo.second == agent.getAgentColor()) {
                System.out.println(agent.getID()+" own color");
                //CoordinateQueue.addCoordinate(agent, packetInfo.first.toString());
                agent.addMemoryFragment(AgentImp.DESTINATION_KEY, packetInfo.first.toString());
            }
            else {
                System.out.println(agent.getID()+" not own color");
                String helpmsg = CommunicateHelp.constructHelpMessage(packetInfo.first, packetInfo.second);
                agent.addMemoryFragment(AgentImp.HELP_MESSAGE_KEY, helpmsg);
            }
        }
        if(path.getPathCoordinate().size() == 0){
            System.out.println(agent.getID()+" path size = 0");
            agent.skip(); //TODO: delete...
        } else {
            System.out.println(agent.getID()+" step path");
            agent.step(path.getPathCoordinate().get(0).getX(), path.getPathCoordinate().get(0).getY());
        }
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