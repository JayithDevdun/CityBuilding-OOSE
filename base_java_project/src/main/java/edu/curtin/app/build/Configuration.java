package edu.curtin.app.build;
import edu.curtin.app.build.Builder.DevelopmentStrategy;


public class Configuration {
    private final Builder urbanPlanner;
   
    private static final DevelopmentStrategy DEFAULT_STRATEGY = DevelopmentStrategy.RANDOM; /*set default stratergy to random*/
   
    
    public Configuration(Builder urbanPlanner) {
        this.urbanPlanner = urbanPlanner;
        initializeDefaultSettings();
    }
    

    /**initialize the default stratergy*/
    private void initializeDefaultSettings() {
        this.urbanPlanner.changeStrategy(DEFAULT_STRATEGY);
    }
   

    /**Apply stratergies according to user selection*/
    public boolean applyDevelopmentStrategy(String strategyName) {
        if (isInvalidStrategyName(strategyName)) {
            return false;
        }
        
        boolean isStrategyApplied = true;
        
        if (strategyName.toLowerCase().equals("uniform")) {
            urbanPlanner.changeStrategy(DevelopmentStrategy.UNIFORM);
        } else if (strategyName.toLowerCase().equals("random")) {
            urbanPlanner.changeStrategy(DevelopmentStrategy.RANDOM);
        } else if (strategyName.toLowerCase().equals("central")) {
            urbanPlanner.changeStrategy(DevelopmentStrategy.CENTRAL);
        } else {
            isStrategyApplied = false;
        }
        
        return isStrategyApplied;
    }

    /**If the stratergy name is invalid*/
    private boolean isInvalidStrategyName(String strategyName) {
        return strategyName == null || strategyName.isEmpty();
    }
   

    /**get the active stratergy name*/
    public String getActiveStrategyName() {
        DevelopmentStrategy strategy = urbanPlanner.getActiveStrategy();
        String strategyName;
        
        int strategyType = getStrategyType(strategy);
        
        switch (strategyType) {
            case 0:
                strategyName = "uniform";
                break;
            case 1:
                strategyName = "random";
                break;
            case 2:
                strategyName = "central";
                break;
            default:
                strategyName = "unknown";
                break;
        }
        
        return strategyName;
    }

    /**get the stratergy type*/
    private int getStrategyType(DevelopmentStrategy strategy) {
        int strategyType;
        
        if (strategy == DevelopmentStrategy.UNIFORM) {
            strategyType = 0;
        } else if (strategy == DevelopmentStrategy.RANDOM) {
            strategyType = 1;
        } else if (strategy == DevelopmentStrategy.CENTRAL) {
            strategyType = 2;
        } else {
            strategyType = -1;
        }
        
        return strategyType;
    }
   

    /**get the active stratergy*/
    public DevelopmentStrategy getActiveStrategy() {
        return urbanPlanner.getActiveStrategy();
    }
   

    /**if user wants, restore default settings*/
    public void restoreDefaultSettings() {
        urbanPlanner.changeStrategy(DEFAULT_STRATEGY);
    }
}