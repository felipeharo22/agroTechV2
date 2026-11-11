package modelo;

/**
 * Specialized recommendation for artificial water supply in oil palm lots.
 * Extends the base Recommendation class, adding water dosage per palm tree.
 */
public class IrrigationRecommendation extends Recommendation {

    private double waterLitersPerPalm;

    /**
     * Constructs an IrrigationRecommendation.
     * 
     * @param message Agronomic explanation.
     * @param priority Urgency level ("HIGH", "MEDIUM", "LOW").
     * @param waterLitersPerPalm Recommended water volume per adult palm (typically 140-180 L).
     */
    public IrrigationRecommendation(String message, String priority, double waterLitersPerPalm) {
        super("IRRIGATION", message, priority);
        this.waterLitersPerPalm = waterLitersPerPalm;
    }

    public double getWaterLitersPerPalm() {
        return this.waterLitersPerPalm;
    }

    public void setWaterLitersPerPalm(double waterLitersPerPalm) {
        this.waterLitersPerPalm = waterLitersPerPalm;
    }

    @Override
    public String getDetails() {
        return String.format("[%s] %s: %s | Recommended Dosage: %.0f Liters per palm tree",
                getPriority(), getType(), getMessage(), this.waterLitersPerPalm);
    }
}
