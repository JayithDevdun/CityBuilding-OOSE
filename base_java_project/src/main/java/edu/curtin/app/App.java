package edu.curtin.app;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.curtin.app.build.Builder;
import edu.curtin.app.build.Configuration;
import edu.curtin.app.build.Result;
import edu.curtin.app.build.StructureBuilder;
import edu.curtin.app.city.FileParser;
import edu.curtin.app.city.Visualizer;
import edu.curtin.app.grids.Grid;
import edu.curtin.app.grids.Square;
import edu.curtin.app.models.Terrain;
import edu.curtin.app.rules.Contamination;
import edu.curtin.app.rules.FloodRisk;
import edu.curtin.app.rules.HeightLimit;
import edu.curtin.app.rules.Heritage;
import edu.curtin.app.rules.Zoning;

/**
 * Entry point into the application.
 */
public class App {
    private static Scanner input = new Scanner(System.in);
    private static Builder cityConstructor;
    private static Configuration configurationHandler;
    private static Logger logSystem = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        logSystem.log(Level.INFO, () -> "City Planner Tool starting - Experimental Version");

        System.out.println("Welcome to the City Planner Tool - Experimental Version");
        System.out.println("=======================================================");

        /**
         * Modified to handle the validation result*/
        if (!validateCommandLineArguments(args)) {
            logSystem.log(Level.INFO, () -> "City Planner Tool shutting down due to invalid arguments");
            return; /**Exit main method without System.exit()*/
        }

        String gridFilePath = args[0];
        Grid urbanGrid;

        try {
            /**
             * Load grid from file*/
            logSystem.log(Level.INFO, () -> "Loading grid from: " + gridFilePath);
            System.out.println("Loading grid from: " + gridFilePath);

            urbanGrid = FileParser.parseFromFile(gridFilePath);

            logSystem.log(Level.INFO, () -> "Grid loaded successfully. Size: " + urbanGrid.getRows() + "x" + urbanGrid.getCols());
            System.out.println("Grid loaded successfully!");

            /**
             * Display grid information*/
            showGridOverview(urbanGrid);

            /**
             * Use the new CityGridVisualizer to display the grid*/
            Visualizer.visualizeGrid(urbanGrid);

            /**
             * Initialize the shared CityBuilder and ConfigurationManager*/
            cityConstructor = new Builder(urbanGrid);
            configurationHandler = new Configuration(cityConstructor);

            displayMainMenu(urbanGrid);

        } catch (IOException ioException) {
            logSystem.log(Level.SEVERE, () -> "Error reading grid data file: " + gridFilePath);
            System.err.println("Error reading grid data file: " + ioException.getMessage());
            /**
             * Remove System.exit(1)*/
        } catch (IllegalArgumentException formatException) {
            logSystem.log(Level.SEVERE, () -> "Invalid grid data file format: " + gridFilePath);
            System.err.println("Invalid grid data file format: " + formatException.getMessage());
            /**
             * Remove System.exit(1)*/
        }

        logSystem.log(Level.INFO, () -> "City Planner Tool shutting down");
        System.out.println("Exiting City Planner Tool. Thank you for using our application!");
    }


    private static boolean validateCommandLineArguments(String[] args) {
        if (args.length == 0) {
            logSystem.log(Level.SEVERE, () -> "No grid data file specified");
            System.err.println("Error: No grid data file specified.");
            System.err.println("Usage: java cityplanner.CityPlannerApp <grid-data-file>");
            return false;
        }
        return true;
    }


    private static void showGridOverview(Grid grid) {
        System.out.println("\n=============================");
        System.out.println("         Grid Summary");
        System.out.println("=============================");
        System.out.println("Grid size: " + grid.getRows() + " rows x " + grid.getCols() + " columns");
        System.out.println("\nTerrain distribution:");

        /**
         * Count of each terrain type*/
        int swampyCount = 0, flatCount = 0, rockyCount = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Square currentSquare = grid.getGridSquare(row, col);
                Terrain terrainType = currentSquare.getTerrain();
                switch (terrainType) {
                    case SWAMPY: swampyCount++; break;
                    case FLAT: flatCount++; break;
                    case ROCKY: rockyCount++; break;
                }
            }
        }

        System.out.println("- Swampy: " + swampyCount + " squares");
        System.out.println("- Flat:   " + flatCount + " squares");
        System.out.println("- Rocky:  " + rockyCount + " squares");
        System.out.println("=============================");
    }


    private static void displayMainMenu(Grid grid) {
        boolean isApplicationRunning = true;
        while (isApplicationRunning) {
            System.out.println("=============================");
            System.out.println("   City Planner Menu");
            System.out.println("=============================");
            System.out.println("[1] Build Structure");
            System.out.println("[2] Build City");
            System.out.println("[3] Configure");
            System.out.println("[4] Quit");
            System.out.println("=============================");
            System.out.print("Enter your choice (1-4): ");

            try {
                int menuSelection = Integer.parseInt(input.nextLine().trim());
                switch (menuSelection) {
                    case 1:
                        handleStructureConstruction(grid);
                        break;
                    case 2:
                        handleCityConstruction();
                        break;
                    case 3:
                        handleConfigurationSettings();
                        break;
                    case 4:
                        isApplicationRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (NumberFormatException parseException) {
                logSystem.log(Level.INFO, () -> "Invalid menu input received");
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }
        }
        input.close();
    }


    private static void handleStructureConstruction(Grid cityGrid) {
        System.out.println("\n--- Build Structure ---");
        System.out.println("Let's construct a new building! Please provide the following details:\n");

        int rowPosition = -1;
        int colPosition = -1;
        int floorCount = -1;
        String foundationType = "";
        String buildingMaterial = "";

        System.out.println("1. Location:");
        /**
         * Get valid row index*/
        while (true) {
            try {
                System.out.print("   - Row index: ");
                rowPosition = Integer.parseInt(input.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for row index.");
            }
        }

        /**Get valid column index*/
        while (true) {
            try {
                System.out.print("   - Column index: ");
                colPosition = Integer.parseInt(input.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for column index.");
            }
        }

        System.out.println("\n2. Building specifications:");
        /**
         * Get valid number of floors*/
        while (true) {
            try {
                System.out.print("   - Number of floors: ");
                floorCount = Integer.parseInt(input.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for floors.");
            }
        }

        /**
         * Get valid foundation type*/
        while (true) {
            System.out.print("   - Foundation type (slab/stilts): ");
            foundationType = input.nextLine().trim().toLowerCase();
            if (foundationType.equals("slab") || foundationType.equals("stilts")) {
                break;
            } else {
                System.out.println("Invalid foundation type. Please enter either 'slab' or 'stilts'.");
            }
        }

        /**
         * Get valid construction material*/
        while (true) {
            System.out.print("   - Construction material (wood/stone/brick/concrete): ");
            buildingMaterial = input.nextLine().trim().toLowerCase();
            if (buildingMaterial.equals("wood") || buildingMaterial.equals("stone") ||
                    buildingMaterial.equals("brick") || buildingMaterial.equals("concrete")) {
                break;
            } else {
                System.out.println("Invalid construction material. Please enter one of: wood, stone, brick, concrete.");
            }
        }

        /**
         * Construct the structure*/
        StructureBuilder structureConstructor = new StructureBuilder(cityGrid);
        Result constructionResult = structureConstructor.buildStructure(
                rowPosition, colPosition, floorCount, foundationType, buildingMaterial
        );

        System.out.println("\n=============================");
        System.out.println("       Build Result");
        System.out.println("=============================");
        if (constructionResult.canBuild()) {
            System.out.println("Status: SUCCESS");
            System.out.println("Message: " + constructionResult.getMessage());
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            System.out.println("\nConstruction cost: " + currencyFormat.format(constructionResult.getCost()));

            /**
             * Display the selected square's info*/
            Square targetSquare = cityGrid.getGridSquare(rowPosition, colPosition);
            System.out.println("\nSelected Square Info:");
            System.out.println("- Terrain: " + targetSquare.getTerrain().getDescription());

            /**
             * Check for each type of zoning rule*/
            for (Zoning zoneRule : targetSquare.getZoning()) {
                if (zoneRule instanceof Heritage heritageZone) {
                    System.out.println("- Heritage: " + heritageZone.getMaterial().getDescription());
                } else if (zoneRule instanceof HeightLimit heightZone) {
                    System.out.println("- Height Limit: " + heightZone.getHeightLimit() + " floors");
                } else if (zoneRule instanceof FloodRisk floodZone) {
                    System.out.println("- Flood Risk: " + String.format("%.2f%%", floodZone.getFloodRiskLevel()));
                } else if (zoneRule instanceof Contamination) {
                    System.out.println("- Contamination: Yes");
                }
            }
            System.out.println("- Has Structure: " + (targetSquare.hasStructure() ? "Yes" : "No"));
        } else {
            System.out.println("Status: FAILED");
            System.out.println("Message: " + constructionResult.getMessage());
        }
        System.out.println("=============================");
    }


    private static void handleCityConstruction() {
        System.out.println("\n--- Build City ---");

        /**
         * Use the shared configurationHandler*/
        String buildStrategyName = configurationHandler.getActiveStrategyName();
        System.out.println("You are about to build the city using the [" + buildStrategyName + "] approach.");

        int floorCount = 1;
        String foundationType = "slab";
        String buildingMaterial = "wood";

        /**
         * Only ask for parameters for UNIFORM approach*/
        if (buildStrategyName.equals("uniform")) {
            System.out.println("All structures will have the same specifications.\n");
            System.out.println("Please specify the uniform building parameters:");
            try {
                /**
                 * Get number of floors*/
                System.out.print("- Number of floors: ");
                floorCount = Integer.parseInt(input.nextLine().trim());

                /**
                 * Get foundation type*/
                System.out.print("- Foundation type (slab/stilts): ");
                foundationType = input.nextLine().trim().toLowerCase();

                /**
                 * Get construction material*/
                System.out.print("- Construction material (wood/stone/brick/concrete): ");
                buildingMaterial = input.nextLine().trim().toLowerCase();

            } catch (NumberFormatException e) {
                logSystem.log(Level.SEVERE, () -> "Invalid numeric input in buildCity: " + e.getMessage());
                System.out.println("Error: Please enter valid numbers for floors.");
                return;
            }
        } else {
            /**
             * For other approaches, use default values (they'll be ignored)*/
            System.out.println("This will automatically generate structures based on the selected strategy.");
            logSystem.log(Level.INFO, () -> "Using default parameters for " + buildStrategyName + " approach");
        }

        /**
         * Use the shared cityConstructor*/
        logSystem.info("Starting city build process");
        Builder.UrbanDevelopmentResult buildOutcome = cityConstructor.developCity(
                floorCount, foundationType, buildingMaterial);

        logSystem.log(Level.INFO, () -> "City build completed - Structures built: " + buildOutcome.getCompletedBuildings() +
                ", Total cost: " + buildOutcome.getDevelopmentExpense());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        System.out.println("\n=============================");
        System.out.println("      City Build Outcome");
        System.out.println("=============================");
        System.out.println("Structures built: " + buildOutcome.getCompletedBuildings());
        System.out.println("Total cost: " + currencyFormat.format(buildOutcome.getDevelopmentExpense()));

        /**
         * Visualize the build map using Visualizer class*/
        System.out.println("\nBuild Map:");
        Visualizer.visualizeConstructionMap(buildOutcome.getConstructionMap());
        System.out.println("(X indicates a successfully built structure)");
        System.out.println("=============================");
    }


    private static void handleConfigurationSettings() {
        logSystem.info("Configure operation started");
        System.out.println("\n--- Configure ---");
        System.out.println("Set the city building approach.\n");

        /**
         * Use the shared configurationHandler*/
        String currentBuildStrategy = configurationHandler.getActiveStrategyName();
        logSystem.log(Level.INFO,() -> "Current approach: " + currentBuildStrategy);
        System.out.println("Current approach: " + currentBuildStrategy + "\n");
        System.out.println("Available approaches:");

        displayAvailableBuildStrategies();
        String selectedStrategy = input.nextLine().trim().toLowerCase();

        String strategyName;
        if (selectedStrategy.equals("1") || selectedStrategy.equals("uniform")) {
            strategyName = "uniform";
        } else if (selectedStrategy.equals("2") || selectedStrategy.equals("random")) {
            strategyName = "random";
        } else if (selectedStrategy.equals("3") || selectedStrategy.equals("central")) {
            strategyName = "central";
        } else {
            System.out.println("Invalid selection. Keeping current approach: " + currentBuildStrategy);
            return;
        }

        if (configurationHandler.applyDevelopmentStrategy(strategyName)) {
            System.out.println("City building approach set to: " + strategyName);
        } else {
            System.out.println("Failed to set approach. Keeping current approach: " + currentBuildStrategy);
        }
    }


    private static void displayAvailableBuildStrategies() {
        System.out.println("[1] uniform - Build identical structures on all grid squares");
        System.out.println("[2] random - Build random structures across the grid (default)");
        System.out.println("[3] central - Build taller buildings in the center, shorter ones on the outskirts");
        System.out.print("\nEnter the number or name of the approach to select: ");
    }
}