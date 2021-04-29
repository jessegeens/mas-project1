package agent.behaviour.colored.subbehaviours;

import agent.AgentImp;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.agent.Agent;
import environment.world.packet.PacketRep;
import util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    public Path(AgentImp agent, Coordinate finalDestination, List<Coordinate> coords, List<Coordinate> packetlocs){
        this.caller = agent;
        this.packetLocations = packetlocs;
        this.coordinates = coords;
        this.finalDestination = finalDestination;
    }
    public Path(AgentImp agent,  Coordinate finalDestination, List<Coordinate> coords) {
        this(agent, finalDestination, coords, new ArrayList<>());
    }

    private final List<Coordinate> coordinates;
    private final List<Coordinate> packetLocations;
    private final Coordinate finalDestination;
    private final AgentImp caller;

    public Boolean isWalkablePath(){
        return this.packetLocations.isEmpty();
    }

    public List<Coordinate> getPathCoordinate() {
        return new ArrayList<>(this.coordinates);
    }

    public List<Coordinate> getPacketCoordinatesInPath() {
        return new ArrayList<>(this.packetLocations);
    }

    public CellPerception getEndDestination(AgentImp agent) {
        Coordinate coord = this.coordinates.get(coordinates.size() - 1);
        return agent.getPerception().getCellPerceptionOnAbsPos(coord.getX(), coord.getY());
    }


    public boolean isFinalDestination() {
        return Coordinate.listContainsCoordinate(getPathCoordinate(), this.finalDestination);
    }

    public AgentImp getCaller() {
        return this.caller;
    }

    public Pair<Coordinate, Color> getPacketInfoOfFirstBlockingPath() {
        List<Coordinate> blockingPackets = getPacketCoordinatesInPath();
        for (Coordinate coordinate: getPathCoordinate()) {
            if (blockingPackets.contains(coordinate)) {
                return new Pair<>(coordinate, getCaller().getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()).getRepOfType(PacketRep.class).getColor());
            }
        }
        return null;
    }

    //TODO: hangt af of we path willen opslaan in geheugen...
    public String toString() {
        return "{agent: " + getCaller().getName() + ", \n path: " + getPathCoordinate().toString() + ", \n packets: " + getPacketCoordinatesInPath().toString() + "}";
    }

    public static Path stringToPath(AgentImp agent, String path) {
        return null;
    }
}
