package agent.behaviour.colored.subbehaviours;

import agent.AgentImp;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.agent.Agent;

import java.util.List;

public class Path {

    public Path() {
    }

    public Boolean isWalkablePath() {
        return false;
    }

    public List<Coordinate> getPathCoordinate() {
        return null;
    }

    public List<CellPerception> getPacketCoordinatesInPath() {
        return null;
    }

    public CellPerception getEndDestination() {
        return null;
    }

    public boolean isFinalDestination() {
        return false;
    }

    public AgentImp getCaller() {
        return null;
    }

    public String toString() {
        return "";
    }

    public static Path stringToPath(String path) {
        return null;
    }
}
