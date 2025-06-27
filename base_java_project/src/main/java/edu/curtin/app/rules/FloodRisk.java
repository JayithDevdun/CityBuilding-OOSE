package edu.curtin.app.rules;

/**FloodRisk that extend the zoning rule*/
public class FloodRisk extends Zoning {
    private final double floodRiskLevel;


    public FloodRisk(double floodRiskLevel) {
        super("Flood Risk");
        this.floodRiskLevel = floodRiskLevel;
    }

    /**getter for get the flood risk level*/
    public double getFloodRiskLevel() {
        return floodRiskLevel;
    }

    /**get details about the flood risk level*/
    @Override
    public String getDetails() {
        return "Flood Risk Level: " + floodRiskLevel + " meters";
    }
}
