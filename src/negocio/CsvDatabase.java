package negocio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import modelo.Reading;

/**
 * Handles all database operations using a plain CSV file as the persistent storage engine.
 * Supports reading, appending, updating, and deleting records according to the format:
 * id,date,humidityLevel,sensorId
 */
public class CsvDatabase {

    private String filePath;

    /**
     * Constructs a CsvDatabase with the specified file path.
     * 
     * @param filePath Path to the CSV file (e.g., "data/readings.csv").
     */
    public CsvDatabase(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Reads all moisture reading records from the CSV file.
     * Uses dynamic array expansion with plain arrays
     * 
     * @return Array of Reading objects currently stored in the CSV.
     */
    public Reading[] readAll() {
        File file = new File(this.filePath);
        if (!file.exists()) {
            return new Reading[0];
        }

        Reading[] buffer = new Reading[50];
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String id = parts[0].trim();
                        String date = parts[1].trim();
                        double humidity = Double.parseDouble(parts[2].trim());
                        String sensorId = parts[3].trim();

                        if (count >= buffer.length) {
                            buffer = expandArray(buffer);
                        }
                        buffer[count++] = new Reading(id, date, humidity, sensorId);
                    } catch (NumberFormatException e) {
                        // Skip corrupted rows safely
                    }
                }
            }
        } catch (IOException e) {
            return new Reading[0];
        }

        return trimArray(buffer, count);
    }

    /**
     * Appends a new Reading record to the end of the CSV database.
     * 
     * @param reading The new record to save.
     * @return True if successfully written, false otherwise.
     */
    public boolean addReading(Reading reading) {
        if (reading == null) {
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.filePath, true))) {
            writer.println(reading.toCsvLine());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Updates the soil humidity level of an existing record identified by its ID.
     * Rewrites the CSV database to persist the modification.
     * 
     * @param id The record identifier (e.g., "REC-001").
     * @param newHumidity The new humidity percentage to record.
     * @return True if record was found and updated, false otherwise.
     */
    public boolean updateReading(String id, double newHumidity) {
        Reading[] records = readAll();
        boolean found = false;

        for (int i = 0; i < records.length; i++) {
            if (records[i].getId().equalsIgnoreCase(id)) {
                records[i].setHumidityLevel(newHumidity);
                found = true;
                break;
            }
        }

        if (found) {
            return writeAll(records);
        }
        return false;
    }

    /**
     * Deletes a record from the CSV database by its ID.
     * 
     * @param id The record identifier to remove.
     * @return True if found and deleted, false otherwise.
     */
    public boolean deleteReading(String id) {
        Reading[] records = readAll();
        int targetIndex = -1;

        for (int i = 0; i < records.length; i++) {
            if (records[i].getId().equalsIgnoreCase(id)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            return false; // Not found
        }

        Reading[] updated = new Reading[records.length - 1];
        int writeIdx = 0;
        for (int i = 0; i < records.length; i++) {
            if (i != targetIndex) {
                updated[writeIdx++] = records[i];
            }
        }
        return writeAll(updated);
    }

    /**
     * Searches for a record by its unique ID.
     */
    public Reading findById(String id) {
        Reading[] records = readAll();
        for (int i = 0; i < records.length; i++) {
            if (records[i].getId().equalsIgnoreCase(id)) {
                return records[i];
            }
        }
        return null;
    }

    /**
     * Filters and returns all readings matching a specific sensor ID.
     */
    public Reading[] findBySensor(String sensorId) {
        Reading[] records = readAll();
        Reading[] buffer = new Reading[records.length];
        int count = 0;

        for (int i = 0; i < records.length; i++) {
            if (records[i].getSensorId().equalsIgnoreCase(sensorId)) {
                buffer[count++] = records[i];
            }
        }
        return trimArray(buffer, count);
    }

    /**
     * Identifies all distinct sensor IDs present across the dataset.
     */
    public String[] getUniqueSensors() {
        Reading[] records = readAll();
        String[] temp = new String[records.length];
        int uniqueCount = 0;

        for (int i = 0; i < records.length; i++) {
            String sid = records[i].getSensorId();
            boolean exists = false;
            for (int j = 0; j < uniqueCount; j++) {
                if (temp[j].equalsIgnoreCase(sid)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                temp[uniqueCount++] = sid;
            }
        }

        String[] result = new String[uniqueCount];
        for (int i = 0; i < uniqueCount; i++) {
            result[i] = temp[i];
        }
        return result;
    }

    /**
     * Generates the next sequential record ID (e.g., "REC-026").
     */
    public String generateNextId() {
        Reading[] records = readAll();
        int max = 0;
        for (int i = 0; i < records.length; i++) {
            String id = records[i].getId();
            if (id.startsWith("REC-")) {
                try {
                    int num = Integer.parseInt(id.substring(4));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-standard IDs
                }
            }
        }
        return String.format("REC-%03d", max + 1);
    }

    /**
     * Rewrites the entire CSV file with the provided array of readings.
     */
    private boolean writeAll(Reading[] records) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.filePath, false))) {
            writer.println("id,date,humidityLevel,sensorId");
            for (int i = 0; i < records.length; i++) {
                writer.println(records[i].toCsvLine());
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Helper to double an array capacity.
     */
    private Reading[] expandArray(Reading[] original) {
        Reading[] larger = new Reading[original.length * 2];
        for (int i = 0; i < original.length; i++) {
            larger[i] = original[i];
        }
        return larger;
    }

    /**
     * Helper to trim an array to exact filled size.
     */
    private Reading[] trimArray(Reading[] buffer, int count) {
        Reading[] trimmed = new Reading[count];
        for (int i = 0; i < count; i++) {
            trimmed[i] = buffer[i];
        }
        return trimmed;
    }

    /**
     * Ensures parent directory exists.
     */
    private void ensureFileExists() {
        File file = new File(this.filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public String getFilePath() {
        return this.filePath;
    }
}
