package agent.behaviour.dropPacket;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class DropPacketv2 extends LTDBehaviour {

    private final static Logger LOGGER = Logger.getLogger(DropPacketv2.class.getName());
//    private Coordinate destination;
    private final static String destinationKey = "destination";
    private final static String searchAllKey = "searchAll";

//    private boolean doSearchAll = true; // true because first time no previous

    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(destinationKey));
        Coordinate currentCoord = new Coordinate(agent.getX(), agent.getY());
        // packet kunt opnemen of afzetten
        // geen packet en packet opnemen?
        try {
            if (destination != null) {
                if (isNeighbour(agent, destination)) {
                    pickOrPutPacket(agent);
                    return;
                } else {
                    setStep(agent, destination);
                    return;
                }
            } else { // destination is null
                setStep(agent);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            agent.removeMemoryFragment(destinationKey);
            agent.addMemoryFragment(searchAllKey, "true");
            agent.skip();
        }
    }

    private void pickOrPutPacket(AgentImp agent){

        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(destinationKey));
        if (agent.hasCarry()){
            agent.putPacket(destination.getX(), destination.getY());
        }else {
            agent.pickPacket(destination.getX(), destination.getY());
        }
        agent.removeMemoryFragment(destinationKey);
        agent.addMemoryFragment(searchAllKey, "true");
    }

    private boolean isNeighbour(AgentImp agent, Coordinate c){
        var perception = agent.getPerception();
        List<CellPerception> neighbours = Arrays.asList(perception.getNeighboursInOrder());
        for(CellPerception neighbour : neighbours) {
            if (neighbour == null) continue; // neighbours can be null when you're at the border of the world
            if (neighbour.getX() == c.getX() && neighbour.getY() == c.getY()) {
                return true;
            }
        }
        return false;
    }


    private void setStep(AgentImp agent) {
        var perception = agent.getPerception();
        List<CellPerception> toSearch;
        if (searchAll(agent)){
            toSearch = searchAll(perception, perception.getWidth(), perception.getHeight());
            agent.addMemoryFragment(searchAllKey, "false");

        }
        else
            toSearch = searchRange(agent, perception.getCellPerceptionOnAbsPos(agent.getX(), agent.getY()), perception.getWidth(), perception.getHeight());
        Coordinate destination = findDestination(agent, toSearch);
        if (destination != null) {
            agent.addMemoryFragment(destinationKey, destination.toString());
            setStep(agent, destination);
        }
        else {
            agent.removeMemoryFragment(destinationKey);
            moveRandomly(agent);
        }
    }

    private void setStep(AgentImp agent, @NotNull Coordinate destination){
        moveTo(destination, agent);
    }


    private Coordinate findDestination(AgentImp agent, List<CellPerception> cells){
//        var perception = agent.getPerception();
        for(CellPerception cell : cells){
            if(cell==null) {
                continue;
            }
            if(containsDestination(agent, cell))
                return new Coordinate(cell.getX(), cell.getY());
        }
        return null;

    }

    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if(agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())){
            return true;
        }else if (!agent.hasCarry() && cell.containsPacket()){
            return true;
        }
        return false;
    }

    private List<CellPerception> searchAll(Perception perception, int width, int height) {
        List<CellPerception> perceptions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perceptions.add(perception.getCellAt(x, y));
            }
        }
        return perceptions;
    }

    // Previous has to be not null
    private List<CellPerception> searchRange(@NotNull AgentImp agent, CellPerception curr, int width, int height){

        List<CellPerception> perceptions = new ArrayList<>();

        int x_range = (width-1)/2;
        int y_range = (height-1)/2;

        //horizontal
        if (agent.getLastArea() == null) {
        }
        int x_diff = curr.getX() - agent.getLastArea().getX();
        int h = curr.getY() - (height - 1) / 2;
        if (x_diff != 0) {
            for (int i = 0; i < height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(curr.getX() + x_diff * x_range, h + i));
            }
        }

        // vertical
        int y_diff = curr.getY() - agent.getLastArea().getY();
        int w = curr.getX() - (width - 1) / 2;
        if (y_diff != 0) {
            for (int i = 0; i < width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(w+i, curr.getY() + y_diff * y_range));
            }
        }

        if (x_diff != 0 && y_diff != 0) {
            perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(curr.getX()+x_diff*x_range, curr.getY()+y_diff*y_range));
        }
        return perceptions;
    }


    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    private void moveTo(Coordinate destination, AgentImp agent){
        var perception = agent.getPerception();
        Coordinate agentCoord = new Coordinate(agent.getX(), agent.getY());
        Coordinate currentBestMove = null;
        for (var move : POSSIBLE_MOVES) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (currentBestMove == null){
                    currentBestMove = move;
                }
                else if (isCloser(Coordinate.getSum(agentCoord, move), Coordinate.getSum(agentCoord, currentBestMove), destination))
                    currentBestMove = move;
            }
        }

        if (currentBestMove == null) agent.skip(); //TODO: Zou dit mogen voorvallen???
        else agent.step(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
    }

    private boolean isCloser(Coordinate first, Coordinate second, Coordinate dest){
        if (first == null) {
            return false;
        }else if (second == null){
            return true;
        }else{
            int distDestToFirst = Perception.distance(dest.getX(), dest.getY(), first.getX(), first.getY());
            int distDestToSecond = Perception.distance(dest.getX(), dest.getY(), second.getX(), second.getY());
            return distDestToFirst < distDestToSecond;
        }
    }
    // Move randomly
    private void moveRandomly(AgentImp agent){
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
                if(agent.getLastArea() != null && agent.getLastArea().getX() == agent.getX() + x && agent.getLastArea().getY() == agent.getY() + y) continue; // Don't undo a move
                agent.step(agent.getX() + x, agent.getY() + y);
                return;
            }
        }
        agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        // No communication
    }

    private Boolean searchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(searchAllKey);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}
