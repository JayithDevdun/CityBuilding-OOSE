package edu.curtin.app.rules;

import edu.curtin.app.models.Material;

/**Heritage limit that extend the zoning rule*/
public class Heritage extends Zoning {
    private final Material material;

    public Heritage(Material material) {
        super("Heritage");
        this.material = material;
    }

    /**getter for the getmeterial*/
    public Material getMaterial() {
        return material;
    }


    /**just in case get the meterial description*/
    @Override
    public String getDetails(){
        return "Material: " + material.getDescription();
    }
}
