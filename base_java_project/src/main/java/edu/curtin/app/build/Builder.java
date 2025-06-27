package edu.curtin.app.build;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import edu.curtin.app.grids.Grid;
import edu.curtin.app.grids.Square;

public class Builder {
    private final Grid urbanGrid;
    private final StructureBuilder constructionManager;
    private final Random randomizer = new Random();
    
    /**
     * Strategy to use for city development*/
    public enum DevelopmentStrategy {
        UNIFORM,
        RANDOM,
        CENTRAL
    }

    private DevelopmentStrategy activeStrategy = DevelopmentStrategy.RANDOM;

    public Builder(Grid urbanGrid) {
        this.urbanGrid = urbanGrid;
        this.constructionManager = new StructureBuilder(urbanGrid);
    }

    /**
     * for change the stratergy*/
    public void changeStrategy(DevelopmentStrategy strategy) {
        this.activeStrategy = strategy;
    }

    /**
     * get the active stratergy*/
    public DevelopmentStrategy getActiveStrategy() {
        return activeStrategy;
    }

    public UrbanDevelopmentResult developCity(int height, String foundationType, String constructionMaterial) {
        int completedStructures = 0;
        double developmentCost = 0.0;
        boolean[][] constructionMap = new boolean[urbanGrid.getRows()][urbanGrid.getCols()];

        Grid temporaryGrid = createTemporaryGrid(); /**create temporary grid*/
        StructureBuilder tempConstructionManager = new StructureBuilder(temporaryGrid);

        int rowCount = urbanGrid.getRows(); /**get row count*/
        int colCount = urbanGrid.getCols(); /**get column count*/

        int row = 0;
        while (row < rowCount) {
            int col = 0;
            while (col < colCount) {
                /**
                 * determine the construction parameters*/
                BuildParameters params = determineConstructionParameters(row, col, height, foundationType, constructionMaterial);

                try {
                    /**build the structure on a temparary grid*/
                    Result outcome = tempConstructionManager.buildStructure(
                        row, col,
                        params.floors,
                        params.foundation,
                        params.material
                    );

                    /**
                     * if structure can be build on temp grid also build it on actual grid*/
                    if (outcome.canBuild()) {
                        completedStructures++;
                        developmentCost += outcome.getCost();
                        constructionMap[row][col] = true;

                        /*Also build on the actual grid if successful*/
                        constructionManager.buildStructure(
                            row, col,
                            params.floors,
                            params.foundation,
                            params.material
                        );
                    } else {
                        constructionMap[row][col] = false; /**if can't build the structure, construction map assign to false*/
                    }
                } catch (Exception e) {
                    constructionMap[row][col] = false;
                    logConstructionFailure(row, col, e); /**If something went wrong pass an exception*/
                }

                col++;
            }
            row++;
        }

        return new UrbanDevelopmentResult(completedStructures, developmentCost, constructionMap);
    }

    /**
     * create temporary grid method*/
    private Grid createTemporaryGrid() {
        Grid tempGrid = new Grid(urbanGrid.getRows(), urbanGrid.getCols());

        for (int i = 0; i < urbanGrid.getRows(); i++) {
            for (int j = 0; j < urbanGrid.getCols(); j++) {
                Square originalSquare = urbanGrid.getGridSquare(i, j);
                Square newSquare = new Square(originalSquare.getTerrain());

                for (var rule : originalSquare.getZoning()) {
                    newSquare.addZoning(rule);
                }

                tempGrid.setGridSquare(i, j, newSquare);
            }
        }

        return tempGrid;
    }

    private void logConstructionFailure(int row, int col, Exception e) {
        System.out.println("Construction failed at location (" + row + "," + col + "): " + e.getMessage());
    }

    /**
     * method for determine the parameters */
    private BuildParameters determineConstructionParameters(int row, int col, int defaultHeight,
                                                          String defaultFoundation, String defaultMaterial) {
        int buildHeight;
        String buildFoundation;
        String buildMaterial;

        /**
         * if the strategy is Uniform*/
        if (activeStrategy == DevelopmentStrategy.UNIFORM) {
            buildHeight = defaultHeight;
            buildFoundation = defaultFoundation;
            buildMaterial = defaultMaterial;

            /**
             * if the strategy is Random*/
        } else if (activeStrategy == DevelopmentStrategy.RANDOM) {
            buildHeight = randomizer.nextInt(20) + 1;
            buildFoundation = randomizer.nextBoolean() ? "slab" : "stilts";

            String[] materials = {"wood", "stone", "brick", "concrete"};
            buildMaterial = materials[randomizer.nextInt(materials.length)];

            /**
             * if stratergy is central*/
        } else if (activeStrategy == DevelopmentStrategy.CENTRAL) {
            double centerRow = urbanGrid.getRows();
            double centerCol = urbanGrid.getCols();
            double distanceFromCenter = calculateDistance(row, col, centerRow, centerCol);

            /**
             * Calculate height based on distance*/
            buildHeight = (int) Math.round(1 + 20 / (distanceFromCenter + 1));

            /**
             * Foundation is always slab for this approach*/
            buildFoundation = "slab";

            /**
             * Material based on distance*/
            if (distanceFromCenter <= 2) {
                buildMaterial = "concrete";
            } else if (distanceFromCenter <= 4) {
                buildMaterial = "brick";
            } else if (distanceFromCenter <= 6) {
                buildMaterial = "stone";
            } else {
                buildMaterial = "wood";
            }
        } else {
            /**
             * Default fallback*/
            buildHeight = defaultHeight;
            buildFoundation = defaultFoundation;
            buildMaterial = defaultMaterial;
        }

        return new BuildParameters(buildHeight, buildFoundation, buildMaterial);
    }

    /**
     * method for calculate distance*/
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 -((x2-1)/2), 2) + Math.pow(y1 -((y2-1)/2), 2));
    }

    /**
     * Helper class to store building parameters*/
    private static class BuildParameters {
        private final int floors;
        private final String foundation;
        private final String material;

        public BuildParameters(int floors, String foundation, String material) {
            this.floors = floors;
            this.foundation = foundation;
            this.material = material;
        }
    }

    /**
     * Renamed from CityBuildResult to UrbanDevelopmentResult*/
    public static class UrbanDevelopmentResult {
        private final int completedBuildings;
        private final double developmentExpense;
        private final boolean[][] constructionMap;

        public UrbanDevelopmentResult(int completedBuildings, double developmentExpense, boolean[][] constructionMap) {
            this.completedBuildings = completedBuildings;
            this.developmentExpense = developmentExpense;
            this.constructionMap = constructionMap;
        }
        
        public int getCompletedBuildings() {
            return completedBuildings;
        }
        
        public double getDevelopmentExpense() {
            return developmentExpense;
        }
        
        public boolean[][] getConstructionMap() {
            return constructionMap;
        }

        @Override
        public String toString() {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            StringBuilder report = new StringBuilder();

            report.append("Urban Development Summary:\n");
            report.append("- Completed structures: ").append(completedBuildings).append("\n");
            report.append("- Total development cost: ").append(currencyFormat.format(developmentExpense)).append("\n");

            return report.toString();
        }
    }
}