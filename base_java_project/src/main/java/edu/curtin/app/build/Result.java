package edu.curtin.app.build;

public class Result {
    private boolean canBuild;
    private String message;
    private double cost;

    public Result(boolean canBuild, String message) {
        this(canBuild, message, 0.0);
    }

    public Result(boolean canBuild, String message, double cost) {
        this.canBuild = canBuild;
        this.message = message;
        this.cost = cost;
    }

    public boolean canBuild() {
        return canBuild;
    }

    public String getMessage() {
        return message;
    }

    public double getCost() {
        return cost;
    }
}