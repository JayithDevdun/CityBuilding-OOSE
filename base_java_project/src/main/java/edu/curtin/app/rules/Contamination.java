package edu.curtin.app.rules;

/**contamination that extend the zoning rule*/
public class Contamination extends Zoning {
   public Contamination(){
    super("Contamination");
   }

    @Override
    public String getDetails() {
        return "Contamination: This area is contaminated and requires remediation.";
    }
}
