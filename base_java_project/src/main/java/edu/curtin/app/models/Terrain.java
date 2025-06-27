package edu.curtin.app.models;

/**enum for terrain type*/
public enum Terrain {
    SWAMPY("Swampy"),
    FLAT("Flat"),
    ROCKY("Rocky");

    private final String description;

    /**set the terrain description*/
    Terrain(String description) {
        this.description = description;
    }

    /**get the terrain description*/
    public String getDescription(){
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}


