package modelo;

/**
 * Represents a single soil moisture reading record stored in the CSV database.
 * Matches the four CSV columns: id, date, humidityLevel, sensorId.
 */
public class Reading {

    private String id;
    private String date;
    private double humidityLevel;
    private String sensorId;

    /**
     * Constructs a new Reading record.
     * 
     * @param id Unique record identifier (e.g., "REC-001").
     * @param date Timestamp formatted as "YYYY-MM-DD HH:MM".
     * @param humidityLevel Soil moisture percentage (e.g., 42.50).
     * @param sensorId Sensor identifier (e.g., "SEN-PALM-01").
     */
    public Reading(String id, String date, double humidityLevel, String sensorId) {
        this.id = id;
        this.date = date;
        this.humidityLevel = humidityLevel;
        this.sensorId = sensorId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHumidityLevel() {
        return this.humidityLevel;
    }

    public void setHumidityLevel(double humidityLevel) {
        this.humidityLevel = humidityLevel;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Converts this reading object into a standard comma-separated line for CSV persistence.
     * 
     * @return Formatted CSV line: id,date,humidityLevel,sensorId
     */
    public String toCsvLine() {
        return String.format(java.util.Locale.US, "%s,%s,%.2f,%s",
                this.id, this.date, this.humidityLevel, this.sensorId);
    }

    @Override
    public String toString() {
        return String.format("[%s] Date: %s | Sensor: %s | Humidity: %.2f%%",
                this.id, this.date, this.sensorId, this.humidityLevel);
    }
}
