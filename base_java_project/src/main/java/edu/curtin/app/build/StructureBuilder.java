package edu.curtin.app.build;

import edu.curtin.app.grids.*;
import edu.curtin.app.models.*;
import edu.curtin.app.rules.*;

public class StructureBuilder {
    private final Grid grid;

    /**
     * Construction cost constants*/
    private static final int TIMBER_COST_PER_LEVEL = 10000;
    private static final int STONE_COST_PER_LEVEL = 50000;
    private static final int BRICK_COST_PER_LEVEL = 30000;
    private static final int CONCRETE_COST_PER_LEVEL = 20000;
    private static final int MARSH_ADDITIONAL_COST_PER_LEVEL = 20000;
    private static final int ROCKY_ADDITIONAL_COST = 50000;
    private static final double POLLUTION_COST_FACTOR = 1.5;
    
    public StructureBuilder(Grid grid) {
        this.grid = grid;
    }
    

    /**
     * build the structure method according to rows, colms, levels, foundationType and construction meterial*/
    public Result buildStructure(int rowPos, int colPos, int levels, String foundationType, String constructionMaterial) {

        /**
         * Invalidate grid coordinates*/
        if (!isValidPosition(rowPos, colPos)) {
            return new Result(false, String.format("Invalid grid coordinates. Row must be between 0 and %d, Column must be between 0 and %d.", 
                grid.getRows() - 1, grid.getCols() - 1));
        }

        /**
         * Get the grid square*/
        Square square = grid.getGridSquare(rowPos, colPos);
        
        /**
         * Validate building parameters*/
        if (levels <= 0) {
            return new Result(false, "Number of floors must be positive.");
        }

        if (!isValidFoundationType(foundationType)) {
            return new Result(false, "Foundation must be either 'slab' or 'stilts'.");
        }

        if (!isValidMaterial(constructionMaterial)) {
            return new Result(false, "Construction material must be 'wood', 'stone', 'brick', or 'concrete'.");  
        }

        /**
         * Check terrain compatibility*/
        Result terrainResult = checkTerrainCompatibility(square, foundationType, constructionMaterial);
        if (!terrainResult.canBuild()) {
            return terrainResult;
        }

        /**
         * Check zoning rules*/
        Result zoningResult = validateZoningRules(square, levels, constructionMaterial);
        if (!zoningResult.canBuild()) {
            return zoningResult;
        }

        /**
         * Calculate construction cost*/
        double totalCost = computeBuildingCost(square, levels, constructionMaterial);

        /**
         * If we reach here, the structure can be built*/
        square.setHasStructure(true);
        
        return new Result(true, "Structure can be built.", totalCost);
    }

    /**
     * if the grid cordinates are in the invalid position*/
    private boolean isValidPosition(int rowPos, int colPos) {
        return rowPos >= 0 && rowPos < grid.getRows() && colPos >= 0 && colPos < grid.getCols();
    }

    /**
     * validate foundation type method*/
    private boolean isValidFoundationType(String foundationType) {
        return foundationType.equalsIgnoreCase("slab") || foundationType.equalsIgnoreCase("stilts");
    }

    /**
     * validate material type method*/
    private boolean isValidMaterial(String material) {
        String lowerMaterial = material.toLowerCase();
        return lowerMaterial.equals("wood") || 
               lowerMaterial.equals("stone") || 
               lowerMaterial.equals("brick") || 
               lowerMaterial.equals("concrete");
    }

    private Result checkTerrainCompatibility(Square square, String foundationType, String material) {
        Terrain terrain = square.getTerrain();
        
        /**
         * Rule: A slab foundation cannot be built in a swamp*/
        if (foundationType.equalsIgnoreCase("slab") && terrain == Terrain.SWAMPY) {
            return new Result(false, "A slab foundation cannot be built in a swamp.");
        }

        /**
         * Rule: A wooden structure cannot be built in a swamp*/
        if (material.equalsIgnoreCase("wood") && terrain == Terrain.SWAMPY) {
            return new Result(false, "A wooden structure cannot be built in a swamp.");
        }
        
        return new Result(true, "Terrain compatibility check passed.");
    }


    /**
     * validate zoning rules method*/
    private Result validateZoningRules(Square square, int levels, String material) {
        /**
         * Check each zoning rule*/
        for (Zoning rule : square.getZoning()) {
            if (rule instanceof Heritage) {
                Heritage heritageRule = (Heritage) rule;
                String requiredMaterial = heritageRule.getMaterial().getDescription();
                
                if (!material.equalsIgnoreCase(requiredMaterial)) {
                    /**
                     * If the material is not match the required material*/
                    return new Result(false, 
                        String.format("This square has a heritage requirement of '%s' which doesn't match the proposed material '%s'.",
                            requiredMaterial, material));
                }
            }
            else if (rule instanceof HeightLimit) {
                HeightLimit heightRule = (HeightLimit) rule;
                int maxHeight = heightRule.getHeightLimit();
                
                if (levels > maxHeight) {
                    /**
                     * If the floor level exceed the floor limit*/
                    return new Result(false, 
                        String.format("The structure exceeds the height limit of %d floors.", maxHeight));
                }
            }
        }
        
        /**
         * Rule: Flood risk requires at least two floors*/
        if (hasZoningRuleOfType(square, FloodRisk.class) && levels < 2) {
            return new Result(false, "Structures in flood risk zones must have at least two floors.");
        }
        
        return new Result(true, "Zoning rules check passed.");
    }

    private double computeBuildingCost(Square square, int levels, String material) {
        /**
         * Calculate base cost by material*/
        double baseCost = getMaterialCost(material) * levels;

        /**
         * Apply terrain-specific costs*/
        switch (square.getTerrain()) {
            case SWAMPY:
                baseCost += MARSH_ADDITIONAL_COST_PER_LEVEL * levels;
                break;
            case ROCKY:
                baseCost += ROCKY_ADDITIONAL_COST;
                break;
            default:
                // No additional cost for other terrain types
                break;
        }

        /**
         * Apply zoning-based cost modifiers*/
        double finalCost = baseCost;
        
        /*Contamination multiplier*/
        if (hasZoningRuleOfType(square, Contamination.class)) {
            finalCost *= POLLUTION_COST_FACTOR;
        }

        /**
         * Flood risk multiplier*/
        if (hasZoningRuleOfType(square, FloodRisk.class)) {
            double riskLevel = getFloodRiskValue(square);
            finalCost *= (1 + riskLevel / 50.0);
        }

        return finalCost;
    }

    /**
     * get the material cost method*/
    private int getMaterialCost(String material) {
        String lowerMaterial = material.toLowerCase();
        
        if (lowerMaterial.equals("wood")) {
            return TIMBER_COST_PER_LEVEL;
        }
        else if (lowerMaterial.equals("stone")) {
            return STONE_COST_PER_LEVEL;
        }
        else if (lowerMaterial.equals("brick")) {
            return BRICK_COST_PER_LEVEL;
        }
        else if (lowerMaterial.equals("concrete")) {
            return CONCRETE_COST_PER_LEVEL;
        }
        
        throw new IllegalArgumentException("Unknown material: " + material);
    }
    
    private <T extends Zoning> boolean hasZoningRuleOfType(Square square, Class<T> ruleType) {
        for (Zoning rule : square.getZoning()) {
            if (ruleType.isInstance(rule)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the flood risk value*/
    private double getFloodRiskValue(Square square) {
        for (Zoning rule : square.getZoning()) {
            if (rule instanceof FloodRisk) {
                return ((FloodRisk) rule).getFloodRiskLevel();
            }
        }
        return 0.0; 
    }
}