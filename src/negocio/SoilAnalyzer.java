package negocio;

import modelo.IrrigationRecommendation;
import modelo.Reading;
import modelo.Recommendation;

/**
 * Business logic service that analyzes soil moisture data, determines soil condition,
 * calculates predictive trends, and produces agronomic recommendations for African oil palm.
 */
public class SoilAnalyzer {

    // --- Agronomic Reference Thresholds for the Palm Soil Dataset ---
    /** Below this percentage, soil moisture is insufficient and palms suffer drought stress. */
    public static final double DRY_THRESHOLD = 45.0;

    /** Above this percentage, soil is waterlogged; risk of root hypoxia and nutrient loss. */
    public static final double EXCESS_THRESHOLD = 75.0;

    /**
     * Classifies a soil humidity percentage into a condition category.
     * 
     * @param humidity Soil moisture percentage.
     * @return Classification: "DRY", "OPTIMAL", or "EXCESS".
     */
    public String classifyMoisture(double humidity) {
        if (humidity < DRY_THRESHOLD) {
            return "DRY";
        } else if (humidity > EXCESS_THRESHOLD) {
            return "EXCESS";
        } else {
            return "OPTIMAL";
        }
    }

    /**
     * Calculates the arithmetic average humidity from an array of readings.
     * 
     * @param readings Array of Reading objects.
     * @return Average humidity percentage, or 0.0 if empty.
     */
    public double calculateAverage(Reading[] readings) {
        if (readings == null || readings.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (int i = 0; i < readings.length; i++) {
            sum += readings[i].getHumidityLevel();
        }
        return sum / readings.length;
    }

    /**
     * Calculates the rate of moisture change (slope) across sequential readings.
     * Positive slope indicates wetting; negative slope indicates drying.
     * 
     * @param readings Chronologically ordered readings for one sensor.
     * @return Change in humidity percentage per reading interval.
     */
    public double calculateTrendSlope(Reading[] readings) {
        if (readings == null || readings.length < 2) {
            return 0.0;
        }
        int n = readings.length;
        double first = readings[0].getHumidityLevel();
        double last = readings[n - 1].getHumidityLevel();
        return (last - first) / (n - 1);
    }

    /**
     * Predicts the next expected soil humidity level based on current reading and trend.
     * Formula: Predicted = LastRecorded + Slope, clamped between 0.0% and 100.0%.
     * 
     * @param readings Historical readings for a sensor.
     * @return Forecasted humidity percentage for the next cycle.
     */
    public double predictNextHumidity(Reading[] readings) {
        if (readings == null || readings.length == 0) {
            return 50.0;
        }
        if (readings.length == 1) {
            return readings[0].getHumidityLevel();
        }

        Reading latest = readings[readings.length - 1];
        double slope = calculateTrendSlope(readings);
        double predicted = latest.getHumidityLevel() + slope;

        if (predicted < 0.0) {
            predicted = 0.0;
        } else if (predicted > 100.0) {
            predicted = 100.0;
        }
        return predicted;
    }

    /**
     * Generates an actionable agronomic recommendation based on latest reading and projected trend.
     * Returns an IrrigationRecommendation or base Recommendation.
     * 
     * @param readings Historical readings for a sensor.
     * @return Recommendation object with field guidance.
     */
    public Recommendation generateRecommendation(Reading[] readings) {
        if (readings == null || readings.length == 0) {
            return new Recommendation("SYSTEM", "No sensor records available to evaluate.", "LOW");
        }

        Reading latest = readings[readings.length - 1];
        double currentHumidity = latest.getHumidityLevel();
        double predicted = predictNextHumidity(readings);
        String currentState = classifyMoisture(currentHumidity);

        if (currentState.equals("DRY")) {
            String msg = String.format("Current humidity is critically low (%.2f%%). Irrigate immediately.", currentHumidity);
            return new IrrigationRecommendation(msg, "HIGH", 150.0);
        } else if (currentState.equals("EXCESS")) {
            String msg = String.format("Current humidity is in excess (%.2f%%). Inspect drainage canals; do not irrigate.", currentHumidity);
            return new Recommendation("DRAINAGE", msg, "HIGH");
        } else if (predicted < DRY_THRESHOLD) {
            String msg = String.format("Soil is currently optimal (%.2f%%) but forecast to drop to %.2f%%. Prepare irrigation.", currentHumidity, predicted);
            return new IrrigationRecommendation(msg, "MEDIUM", 120.0);
        } else {
            String msg = String.format("Soil humidity is optimal (%.2f%%). Favorable conditions for nutrient absorption.", currentHumidity);
            return new Recommendation("FERTILIZATION", msg, "MEDIUM");
        }
    }
}
