package edu.curtin.app.models;

/**enum structure for the materials*/
public enum Material {
    WOOD("Wood"),
    STONE("Stone"),
    BRICK("Brick"),
    CONCRETE("Concrete");

    private final String description;

    /**set the meterial description*/
    Material(String description){
        this.description = description;
    }

    /**get the meterial description*/
    public String getDescription(){
        return description;
    }
}
