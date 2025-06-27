package edu.curtin.app.city;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.curtin.app.grids.Grid;
import edu.curtin.app.grids.Square;
import edu.curtin.app.models.Material;
import edu.curtin.app.models.Terrain;
import edu.curtin.app.rules.Contamination;
import edu.curtin.app.rules.FloodRisk;
import edu.curtin.app.rules.HeightLimit;
import edu.curtin.app.rules.Heritage;

public class FileParser {
    
    /**
     * Patterns for parsing*/
    private static final Pattern DIMENSIONS_PATTERN = Pattern.compile("^(\\d+),(\\d+)$");
    private static final Pattern HERITAGE_PATTERN = Pattern.compile("heritage=(wood|stone|brick||concrete)");
    private static final Pattern HEIGHT_LIMIT_PATTERN = Pattern.compile("height-limit=(\\d+)");
    private static final Pattern FLOOD_RISK_PATTERN = Pattern.compile("flood-risk=([0-9]+(\\.[0-9]+)?)");
    private static final String CONTAMINATION = "contamination";
    
    private static Logger logger = Logger.getLogger(FileParser.class.getName());
    public static Grid parseFromFile(String filePath) throws IOException, IllegalArgumentException {
        logger.log(Level.INFO,() -> "Parsing grid file: " + filePath);
        
        Grid grid ;
        int lineNumber = 0;
        int currentRow = 0;
        int expectedSquareCount;
        int parsedSquareCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            /**
             * Read the first line for dimensions*/
            line = reader.readLine();
            lineNumber++;
            
            if (line == null) {
                logger.log(Level.SEVERE,() -> "Empty grid file: " + filePath);
                throw new IllegalArgumentException("Grid file is empty");
            }
            
            /**
             * Parse dimensions*/
            Matcher dimensionMatcher = DIMENSIONS_PATTERN.matcher(line);
            if (!dimensionMatcher.matches()) {
                logger.log(Level.SEVERE,() -> "Invalid dimensions format at line ");
                throw new IllegalArgumentException("Invalid grid dimensions format at line " + lineNumber);
            }
            
            int rows = Integer.parseInt(dimensionMatcher.group(1));
            int columns = Integer.parseInt(dimensionMatcher.group(2));
            
            if (rows <= 0 || columns <= 0) {
                logger.log(Level.SEVERE,()-> "Invalid dimensions at line ");
                throw new IllegalArgumentException("Grid dimensions must be positive integers");
            }
            
            grid = new Grid(rows, columns);
            expectedSquareCount = rows * columns;
            
            /**
             * Read the rest of the lines for grid squares*/
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    logger.log(Level.WARNING,() ->"Skipping empty line at line ");
                    continue; /**
                     Skip empty lines*/
                }
                
                if (parsedSquareCount >= expectedSquareCount) {
                    logger.log(Level.WARNING,()-> "Too many grid squares at line ");
                    throw new IllegalArgumentException("Too many grid squares specified in file");
                }
                
                /**
                 * Calculate current position*/
                int currentColumn = parsedSquareCount % columns;
                if (currentColumn == 0 && parsedSquareCount > 0) {
                    currentRow++;
                }
                
                /**
                 * Parse the grid square*/
                parseGridSquare(line, grid, currentRow, currentColumn, lineNumber);
                parsedSquareCount++;
            }
            
            /**
             * Check if we have enough squares*/
            if (parsedSquareCount < expectedSquareCount) {
                logger.log(Level.WARNING,() -> "Not enough grid squares. " );
                throw new IllegalArgumentException("Not enough grid squares specified in file. Expected: " + 
                                                 expectedSquareCount + ", Got: " + parsedSquareCount);
            }
            
            logger.log(Level.WARNING,()-> "Successfully parsed grid with");
            return grid;
        }
    }

    private static void parseGridSquare(String line, Grid grid, int row, int column, int lineNumber)
                                  throws IllegalArgumentException {
 
        String[] fields = line.split(",");
        if (fields.length == 0) {
            logger.log(Level.INFO,() -> "Empty line at line ");
            throw new IllegalArgumentException("Empty grid square description at line " + lineNumber);
        }
    
        /**
         * Parse terrain type*/
        Terrain terrain = parseTerrainType(fields[0], lineNumber);
    
        /**
         * Create grid square with terrain*/
        Square square = new Square(terrain);
    
        /**
         * Parse zoning rules*/
        for (int i = 1; i < fields.length; i++) {
            String field = fields[i].trim();
            if (field.isEmpty()) {
                continue; /**
                 Skip empty fields*/
            }
        
            try {
                if (field.startsWith("heritage")) {
                    parseHeritageRule(field, square, lineNumber);
                } else if (field.startsWith("height-limit")) {
                    parseHeightLimitRule(field, square, lineNumber);
                } else if (field.startsWith("flood-risk")) {
                    parseFloodRiskRule(field, square, lineNumber);
                } else if (field.equals(CONTAMINATION)) {
                    square.addZoning(new Contamination());
                } else {
                    logger.log(Level.WARNING,() -> "Unknown field at line " + lineNumber + ": " + field);
                    throw new IllegalArgumentException("Unknown field at line " + lineNumber + ": " + field);
                }
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, () -> "Error parsing zoning rule at line " + lineNumber + ": " + field);
                /**
                 * Pass the original exception as the cause to preserve the stack trace*/
                throw new IllegalArgumentException("Error parsing zoning rule at line " + lineNumber + ": " + e.getMessage(), e);
            }
        }
    
        /**
         * Add the square to the grid*/
            grid.setGridSquare(row, column, square);
    }

    private static Terrain parseTerrainType(String terrainStr, int lineNumber) throws IllegalArgumentException {
        terrainStr = terrainStr.trim();
        
        return switch (terrainStr.toLowerCase()) {
            case "flat" -> Terrain.FLAT;
            case "swampy" -> Terrain.SWAMPY;
            case "rocky" -> Terrain.ROCKY;
            default -> {
                logger.log(Level.SEVERE,()-> "Invalid terrain type at line");
                throw new IllegalArgumentException("Invalid terrain type at line " + lineNumber + ": " + terrainStr);
            }
        };
    }
    private static void parseHeritageRule(String field, Square square, int lineNumber) throws IllegalArgumentException {
        Matcher matcher = HERITAGE_PATTERN.matcher(field);
        if (!matcher.matches()) {
            logger.log(Level.SEVERE,()-> "Invalid heritage rule format at line " + lineNumber + ": " + field);
            throw new IllegalArgumentException("Invalid heritage rule format at line " + lineNumber + ": " + field);
        }
        
        String materialStr = matcher.group(1);
        Material material = switch (materialStr) {
            case "wood" -> Material.WOOD;
            case "stone" -> Material.STONE;
            case "brick" -> Material.BRICK;
            case "concrete" -> Material.CONCRETE;
            default -> {
                logger.log(Level.SEVERE,() -> "Invalid heritage material at line " + lineNumber + ": " + materialStr);
                throw new IllegalArgumentException("Invalid heritage material at line " + lineNumber + ": " + materialStr);
            }
        };
        
        square.addZoning(new Heritage(material));
        
    }
    
    private static void parseHeightLimitRule(String field, Square square, int lineNumber) throws IllegalArgumentException {
        Matcher matcher = HEIGHT_LIMIT_PATTERN.matcher(field);
        if (!matcher.matches()) {
            logger.log(Level.SEVERE,() -> "Invalid height limit rule format at line " + lineNumber + ": " + field);
            throw new IllegalArgumentException("Invalid height limit rule format at line " + lineNumber + ": " + field);
        }
        
        int maxFloors = Integer.parseInt(matcher.group(1));
        if (maxFloors <= 0) {
            logger.log(Level.SEVERE,() -> "Invalid height limit value at line " + lineNumber + ": " + maxFloors);
            throw new IllegalArgumentException("Height limit must be a positive integer at line " + lineNumber);
        }
        
        square.addZoning(new HeightLimit(maxFloors));
        
    }

    private static void parseFloodRiskRule(String field, Square square, int lineNumber) throws IllegalArgumentException {
        Matcher matcher = FLOOD_RISK_PATTERN.matcher(field);
        if (!matcher.matches()) {
            logger.log(Level.SEVERE,() -> "Invalid flood risk rule format at line " + lineNumber + ": " + field);
            throw new IllegalArgumentException("Invalid flood risk rule format at line " + lineNumber + ": " + field);
        }
        
        double riskPercentage = Double.parseDouble(matcher.group(1));
        if (riskPercentage < 0.0 || riskPercentage > 100.0) {
            logger.log(Level.SEVERE,()->"Invalid flood risk value at line " + lineNumber + ": " + riskPercentage);
            throw new IllegalArgumentException("Flood risk must be between 0.0 and 100.0 at line " + lineNumber);
        }
        
        square.addZoning(new FloodRisk(riskPercentage));
    }
}