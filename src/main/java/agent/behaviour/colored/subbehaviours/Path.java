package agent.behaviour.colored.subbehaviours;

import agent.AgentImp;
import environment.Coordinate;
import environment.world.packet.PacketRep;
import util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * The Path class represents a path that an agent needs to take. A path has a 'final" destination (where the agent needs
 * to go to, a number of coordinates that together form the path, and some packet locations of packets that are currently
 * blocking moving along the path
 */
public class Path {

    public Path(AgentImp agent, Coordinate finalDestination, List<Coordinate> coords, List<Coordinate> packetlocs) {
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

    /**
     * @return true iff there are no packets on the path blocking movement along this path
     */
    public Boolean isWalkablePath() {
        return this.packetLocations.isEmpty();
    }

    /**
     * @return a list of coordinates that form the path
     */
    public List<Coordinate> getPathCoordinate() {
        return new ArrayList<>(this.coordinates);
    }

    /**
     * @return a list of coordinates of where there are currently packets on the path
     */
    public List<Coordinate> getPacketCoordinatesInPath() {
        return new ArrayList<>(this.packetLocations);
    }

    /**
     * @return the agent that has calculated the path
     */
    public AgentImp getCaller() {
        return this.caller;
    }

    /**
     * @return the coordinate and the color of the first packet that is blocking the path
     * if this color is the color of the agent, the agent can remove the packet
     * otherwise, the agent needs to ask help to another agent of the correct color
     */
    public Pair<Coordinate, Color> getPacketInfoOfFirstBlockingPath() {
        List<Coordinate> blockingPackets = getPacketCoordinatesInPath();
        for (Coordinate coordinate: getPathCoordinate()) {
            if (blockingPackets.contains(coordinate)) {
                return new Pair<>(coordinate, getCaller().getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()).getRepOfType(PacketRep.class).getColor());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "{agent: " + getCaller().getName() + ", \n path: " + getPathCoordinate().toString() + ", \n packets: " + getPacketCoordinatesInPath().toString() + "}";
    }
}
