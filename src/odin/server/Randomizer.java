package odin.server;

import java.util.Random;

public class Randomizer {

    private final static Random rand = new Random();

    public static int nextInt() {
        return rand.nextInt();
    }

    public static int nextInt(int arg0) {
        return rand.nextInt(arg0);
    }

    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    public static int rand(int lbound, int ubound) {
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
