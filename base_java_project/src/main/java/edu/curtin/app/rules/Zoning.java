package edu.curtin.app.rules;

/**zonning class*/
public abstract  class Zoning {
    private final String ruleType;

    /**set the zoning rule type*/
    protected Zoning(String ruleType){
        this.ruleType = ruleType;
    }

    /**getter for get the rule type*/
    public String getRuleType(){
        return ruleType;
    }

    /**get details*/
    public abstract String getDetails();

    /**tostring method for combine rule type nad details*/
    @Override
    public String toString() {
        return ruleType + ": " + getDetails();
    }
}
