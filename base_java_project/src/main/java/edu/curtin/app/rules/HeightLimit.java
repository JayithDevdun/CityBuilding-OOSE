package edu.curtin.app.rules;

/**Height limit that extend the zoning rule*/
public class HeightLimit extends Zoning {
    private final int heightLimit;

    public HeightLimit(int heightLimit){
        super("Height Limit");
        this.heightLimit = heightLimit;
    }

    /**getter for get height limit*/
    public int getHeightLimit(){
        return heightLimit;
    }

    /**just in case get the details about the height limit*/
    @Override
    public String getDetails() {
        return "Height Limit: " + heightLimit + " meters";
    }  
}
