package edu.curtin.app.grids;

import java.util.ArrayList;
import java.util.List;

import edu.curtin.app.models.Terrain;
import edu.curtin.app.rules.Zoning;

public class Square {
    private final Terrain terrain;
    private final List<Zoning> zoning;
    private boolean hasStructure;

    public Square(Terrain terrain) {
        this.terrain = terrain;
        this.zoning = new ArrayList<>();
        this.hasStructure = false;
    }


    /**method to get the terrain*/
    public Terrain getTerrain() {
        return terrain;
    }

    /**method to get the zoning*/
    public List<Zoning> getZoning() {
        return zoning;
    }

    /**method to add zoning*/
    public void addZoning(Zoning zoning) {
        this.zoning.add(zoning);
    }

    /**method to remove zonin*/
    public void removeZoning(Zoning zoning) {
        this.zoning.remove(zoning);
    }

    /**method to see has structure or not*/
    public boolean hasStructure() {
        return hasStructure;
    }

    /**method to set structure*/
    public void setHasStructure(boolean hasStructure) {
        this.hasStructure = hasStructure;
    }


    /**to string method*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GridSquare[terrain=").append(terrain.getDescription());
        
        if (!zoning.isEmpty()) {
            sb.append(", zoning=");
            for (int i = 0; i < zoning.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(zoning.get(i));
            }
        }
        
        sb.append(", structure=").append(hasStructure ? "Yes" : "No");
        sb.append("]");
        
        return sb.toString();
    }
}
