package edu.curtin.app.city;

import edu.curtin.app.grids.Grid;
import edu.curtin.app.grids.Square;
import edu.curtin.app.models.Material;
import edu.curtin.app.rules.FloodRisk;
import edu.curtin.app.rules.HeightLimit;
import edu.curtin.app.rules.Heritage;

public class Visualizer {
    public static void visualizeGrid(Grid grid) {
        int height = grid.getRows();    /**Get number of rows*/
        int width = grid.getCols();  /**Get number of columns*/
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < height; row++) {
            /**Top border for each row*/
            sb.append("+");
            for (int i = 0; i < width; i++) {
                sb.append("---------------+");
            }
            sb.append("\n");

            /**Terrain row*/
            for (int col = 0; col < width; col++) {
                Square square = grid.getGridSquare(row, col);
                String terrain = padRight(square.getTerrain().getDescription().toLowerCase(), 13);
                sb.append("| ").append(terrain);
            }
            sb.append("|\n");

            /**Zoning rules row*/
            for (int col = 0; col < width; col++) {
                Square square = grid.getGridSquare(row, col);
                String zoningRules = formatZoningRules(square);
                sb.append("| ").append(padRight(zoningRules, 13));
            }
            sb.append("|\n");
        }

        /**Bottom border*/
        sb.append("+");
        for (int i = 0; i < width; i++) {
            sb.append("---------------+");
        }
        sb.append("\n");

        System.out.println(sb.toString());
    }

    private static String formatZoningRules(Square square) {
        StringBuilder sb = new StringBuilder();

        square.getZoning().forEach(rule -> {
            String abbreviation;
            String ruleType = rule.getRuleType();

            switch (ruleType) {
                case "Heritage":
                    if (rule instanceof Heritage) {
                        Heritage heritage = (Heritage) rule;
                        Material material = heritage.getMaterial();
                        abbreviation = "H" + material.getDescription().toLowerCase().charAt(0);
                    } else {
                        abbreviation = "H?";
                    }
                    break;

                case "Height Limit":
                    if (rule instanceof HeightLimit) {
                        HeightLimit heightLimit = (HeightLimit) rule;
                        int limit = heightLimit.getHeightLimit();
                        abbreviation = "L" + limit;
                    } else {
                        abbreviation = "L?";
                    }
                    break;

                case "Flood Risk":
                    if (rule instanceof FloodRisk) {
                        FloodRisk floodRisk = (FloodRisk) rule;
                        double riskLevel = floodRisk.getFloodRiskLevel();
                        abbreviation = "F" + (int) Math.round(riskLevel);
                    } else {
                        abbreviation = "F?";
                    }
                    break;

                case "Contamination":
                    abbreviation = "C";
                    break;

                default:
                    abbreviation = "?" + (ruleType.isEmpty() ? "?" : ruleType.charAt(0));
                    break;
            }

            sb.append(abbreviation).append(" ");
        });

        return sb.toString().trim();
    }

    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    public static void visualizeConstructionMap(boolean[][] constructionMap) {
        StringBuilder visualizer = new StringBuilder();
        int rows = constructionMap.length;
        int cols = constructionMap[0].length;

        visualizer.append(" ");
        for (int j = 0; j < cols; j++) {
            visualizer.append("---");
        }
        visualizer.append("\n");

        for (int i = 0; i < rows; i++) {
            visualizer.append("|");
            for (int j = 0; j < cols; j++) {
                visualizer.append(constructionMap[i][j] ? " X " : "   ");
                visualizer.append("|");
            }
            visualizer.append("\n");
            visualizer.append(" ");
            for (int j = 0; j < cols; j++) {
                visualizer.append("---");
            }
            visualizer.append("\n");
        }

        System.out.println(visualizer.toString());
    }
}