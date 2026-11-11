package modelo;

/**
 * Base class representing an agronomic recommendation generated for a plantation sensor.
 * Demonstrates basic inheritance as the parent class for specific field actions.
 */
public class Recommendation {

    private String type;
    private String message;
    private String priority;

    /**
     * Constructs a Recommendation.
     * 
     * @param type Category of recommendation (e.g., "IRRIGATION", "DRAINAGE", "FERTILIZATION", "MONITORING").
     * @param message Explanatory advice for plantation managers.
     * @param priority Urgency level ("HIGH", "MEDIUM", "LOW").
     */
    public Recommendation(String type, String message, String priority) {
        this.type = type;
        this.message = message;
        this.priority = priority;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Returns a formatted summary of the recommendation.
     * Child classes can override this method to include specialized data.
     */
    public String getDetails() {
        return "[" + this.priority + "] " + this.type + ": " + this.message;
    }

    @Override
    public String toString() {
        return getDetails();
    }
}
