package environment;

import util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 *  A class to represent a 2-dimensional coordinate.
 */

public class Coordinate extends Pair<Integer, Integer> {

    public Coordinate(int x, int y) {
        super(x, y);
    }

    public static Coordinate fromString(String coord) {
        if (coord == null) return null;
        coord = coord.replace("(", "").replace(")", "");
        String[] parts = coord.split(",");
        return new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public int getX() {
        return this.first;
    }

    public int getY() {
        return this.second;
    }

    public String toString() {
        return String.format("(%d,%d)", this.getX(), this.getY());
    }

    public static Coordinate getSum(Coordinate c1, Coordinate c2) {
        int newX = c1.getX() + c2.getX();
        int newY = c1.getY() + c2.getY();
        return new Coordinate(newX, newY);
    }

    public ArrayList<Coordinate> getNeighbours() {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    coordinates.add(new Coordinate(this.getX() + i, this.getY() + j));
                }
            }
        }
        return coordinates;
    }

    public ArrayList<Coordinate> getNeighboursInWorld(int worldWidth, int worldHeight) {
        ArrayList<Coordinate> coordinates = getNeighbours();
        coordinates.removeIf(coordinate -> coordinate.getX() < 0 || coordinate.getX() >= worldWidth
                || coordinate.getY() < 0 || coordinate.getY() >= worldHeight);
        return coordinates;
    }

    public boolean equalsCoordinate(Coordinate other) {
        return other.getY() == this.getY() && other.getX() == this.getX();
    }

    public static boolean listContainsCoordinate(List<Coordinate> list, Coordinate toCheck){
        for (Coordinate elem: list){
            if (elem.equalsCoordinate(toCheck)){
                return true;
            }
        }
        return false;
    }
}