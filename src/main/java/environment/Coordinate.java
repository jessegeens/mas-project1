package environment;

import util.Pair;

/**
 *  A class to represent a 2-dimensional coordinate.
 */

public class Coordinate extends Pair<Integer, Integer> {

    public Coordinate(int x, int y) {
        super(x, y);
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

    public static Coordinate getSum(Coordinate c1, Coordinate c2){
        int newX = c1.getX() + c2.getX();
        int newY = c1.getY() + c2.getY();
        return new Coordinate(newX, newY);
    }
}