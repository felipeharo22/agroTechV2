package interfaz;

import java.util.Scanner;
import modelo.Reading;
import modelo.Recommendation;
import negocio.CsvDatabase;
import negocio.SoilAnalyzer;

/**
 * Console user interface for managing the soil moisture CSV database
 * and executing soil classification, predictive analysis, and field recommendations.
 */
public class ConsoleMenu {

    private Scanner scanner;
    private CsvDatabase database;
    private SoilAnalyzer analyzer;

    public ConsoleMenu(CsvDatabase database, SoilAnalyzer analyzer) {
        this.scanner = new Scanner(System.in);
        this.database = database;
        this.analyzer = analyzer;
    }

    /**
     * Main interaction loop.
     */
    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMenuOptions();
            int choice = readIntOption("Enter your choice: ", 0, 6);
            System.out.println();

            switch (choice) {
                case 1:
                    handleListAllReadings();
                    break;
                case 2:
                    handleAddReading();
                    break;
                case 3:
                    handleEditReading();
                    break;
                case 4:
                    handleDeleteReading();
                    break;
                case 5:
                    handleAnalyzeSensor();
                    break;
                case 6:
                    handleSummaryReport();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the Palm Soil-Moisture Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            System.out.println();
        }
    }

    private void printBanner() {
        System.out.println("==================================================================");
        System.out.println("        OIL PALM (Elaeis guineensis) SOIL MOISTURE SYSTEM         ");
        System.out.println("               CSV-Driven Database & Decision Engine              ");
        System.out.println("==================================================================");
    }

    private void printMenuOptions() {
        System.out.println("------------------------- MAIN MENU ------------------------------");
        System.out.println(" 1. View all readings in CSV database");
        System.out.println(" 2. Add a new reading to CSV");
        System.out.println(" 3. Edit/update a reading's humidity in CSV");
        System.out.println(" 4. Delete a reading from CSV");
        System.out.println(" 5. Analyze a sensor (State, Trend, Forecast & Recommendation)");
        System.out.println(" 6. Show summary report for all sensors");
        System.out.println(" 0. Exit");
        System.out.println("------------------------------------------------------------------");
    }

    /**
     * Displays all CSV records in a formatted table.
     */
    private void handleListAllReadings() {
        Reading[] records = this.database.readAll();
        if (records.length == 0) {
            System.out.println(">> No records found in CSV file.");
            return;
        }

        System.out.println("======================== CSV DATABASE RECORDS ========================");
        System.out.printf("%-10s %-18s %-15s %-12s%n", "Record ID", "Date / Time", "Humidity (%)", "Sensor ID");
        System.out.println("----------------------------------------------------------------------");
        for (int i = 0; i < records.length; i++) {
            Reading r = records[i];
            System.out.printf("%-10s %-18s %-15.2f %-12s%n",
                    r.getId(), r.getDate(), r.getHumidityLevel(), r.getSensorId());
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("Total records: %d%n", records.length);
        System.out.println("======================================================================");
    }

    /**
     * Prompts user for reading attributes, generates the next sequential ID, and saves to CSV.
     */
    private void handleAddReading() {
        System.out.println("--- ADD NEW READING TO CSV DATABASE ---");
        String newId = this.database.generateNextId();
        System.out.printf("Generated Record ID: %s%n", newId);

        System.out.print("Enter Date/Time (e.g., 2026-11-01 08:30): ");
        String date = this.scanner.nextLine().trim();
        if (date.isEmpty()) {
            date = "2026-11-01 08:00";
        }

        double humidity = readDoubleOption("Enter Humidity Level (%): ", 0.0, 100.0);

        System.out.print("Enter Sensor ID (e.g., SEN-PALM-01): ");
        String sensorId = this.scanner.nextLine().trim().toUpperCase();
        if (sensorId.isEmpty()) {
            sensorId = "SEN-PALM-01";
        }

        Reading newReading = new Reading(newId, date, humidity, sensorId);
        boolean saved = this.database.addReading(newReading);

        if (saved) {
            System.out.printf(">> Record %s successfully added to CSV database!%n", newId);
        } else {
            System.out.println(">> Error: Could not write record to CSV file.");
        }
    }

    /**
     * Prompts for an existing record ID and updates its humidity value in the CSV.
     */
    private void handleEditReading() {
        System.out.println("--- EDIT READING IN CSV DATABASE ---");
        System.out.print("Enter Record ID to modify (e.g., REC-001): ");
        String id = this.scanner.nextLine().trim();

        Reading existing = this.database.findById(id);
        if (existing == null) {
            System.out.printf(">> Record with ID '%s' not found in CSV database.%n", id);
            return;
        }

        System.out.printf("Current Record: %s%n", existing);
        double newHumidity = readDoubleOption("Enter New Humidity Level (%): ", 0.0, 100.0);

        boolean updated = this.database.updateReading(id, newHumidity);
        if (updated) {
            System.out.printf(">> Record %s successfully updated in CSV to %.2f%%!%n", id, newHumidity);
        } else {
            System.out.println(">> Error: Failed to update CSV file.");
        }
    }

    /**
     * Deletes a record from the CSV file.
     */
    private void handleDeleteReading() {
        System.out.println("--- DELETE READING FROM CSV DATABASE ---");
        System.out.print("Enter Record ID to delete: ");
        String id = this.scanner.nextLine().trim();

        boolean deleted = this.database.deleteReading(id);
        if (deleted) {
            System.out.printf(">> Record %s deleted successfully from CSV database.%n", id);
        } else {
            System.out.printf(">> Record '%s' not found or could not be removed.%n", id);
        }
    }

    /**
     * Analyzes readings for a specific sensor: classification, trend slope, next forecast, recommendation.
     */
    private void handleAnalyzeSensor() {
        String[] sensors = this.database.getUniqueSensors();
        if (sensors.length == 0) {
            System.out.println(">> No sensors found in database.");
            return;
        }

        System.out.print("Available sensors: ");
        for (int i = 0; i < sensors.length; i++) {
            System.out.printf("[%s] ", sensors[i]);
        }
        System.out.println();

        System.out.print("Enter Sensor ID to analyze: ");
        String sensorId = this.scanner.nextLine().trim().toUpperCase();

        Reading[] sensorReadings = this.database.findBySensor(sensorId);
        if (sensorReadings.length == 0) {
            System.out.printf(">> No records found for sensor '%s'.%n", sensorId);
            return;
        }

        Reading latest = sensorReadings[sensorReadings.length - 1];
        String state = this.analyzer.classifyMoisture(latest.getHumidityLevel());
        double avg = this.analyzer.calculateAverage(sensorReadings);
        double slope = this.analyzer.calculateTrendSlope(sensorReadings);
        double predicted = this.analyzer.predictNextHumidity(sensorReadings);
        Recommendation rec = this.analyzer.generateRecommendation(sensorReadings);

        System.out.println("==================== SENSOR ANALYSIS REPORT ====================");
        System.out.printf(" Sensor ID:            %s%n", sensorId);
        System.out.printf(" Total Readings:       %d records%n", sensorReadings.length);
        System.out.printf(" Latest Timestamp:     %s%n", latest.getDate());
        System.out.printf(" Latest Humidity:      %.2f%%%n", latest.getHumidityLevel());
        System.out.printf(" Current Soil State:   [%s]%n", state);
        System.out.printf(" Historical Average:   %.2f%%%n", avg);
        System.out.printf(" Trend Slope:          %.3f%% per interval (%s)%n",
                slope, (slope < 0 ? "DRYING" : "WETTING/STABLE"));
        System.out.printf(" Next Cycle Forecast:  %.2f%% (Projected State: %s)%n",
                predicted, this.analyzer.classifyMoisture(predicted));
        System.out.println("----------------------------------------------------------------");
        System.out.printf(" Agronomic Action:     %s%n", rec.getDetails());
        System.out.println("=================================================================");
    }

    /**
     * Displays a consolidated summary table across all sensors in the plantation.
     */
    private void handleSummaryReport() {
        String[] sensors = this.database.getUniqueSensors();
        if (sensors.length == 0) {
            System.out.println(">> No sensors found in database.");
            return;
        }

        System.out.println("============================= ALL SENSORS SUMMARY =============================");
        System.out.printf("%-14s %-9s %-12s %-10s %-12s %-12s %-12s%n",
                "Sensor ID", "Readings", "Latest (%)", "State", "Trend (%/i)", "Forecast %", "Action");
        System.out.println("-------------------------------------------------------------------------------");

        for (int i = 0; i < sensors.length; i++) {
            String sid = sensors[i];
            Reading[] list = this.database.findBySensor(sid);
            Reading latest = list[list.length - 1];
            String state = this.analyzer.classifyMoisture(latest.getHumidityLevel());
            double slope = this.analyzer.calculateTrendSlope(list);
            double forecast = this.analyzer.predictNextHumidity(list);
            Recommendation rec = this.analyzer.generateRecommendation(list);

            System.out.printf("%-14s %-9d %-12.2f %-10s %-12.3f %-12.2f %-12s%n",
                    sid, list.length, latest.getHumidityLevel(), state, slope, forecast, rec.getType());
        }
        System.out.println("===============================================================================");
    }

    /**
     * Reads a valid integer between min and max.
     */
    private int readIntOption(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = this.scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf(">> Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a valid floating-point number between min and max.
     */
    private double readDoubleOption(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = this.scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf(">> Please enter a value between %.1f and %.1f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid input. Please enter a valid decimal number.");
            }
        }
    }
}
