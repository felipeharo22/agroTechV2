package interfaz;

import negocio.CsvDatabase;
import negocio.SoilAnalyzer;

/**
 * Application entry point for the Oil Palm Soil-Moisture Management System.
 * Connects the CSV database and analysis engine to the console user interface.
 */
public class Main {

    public static void main(String[] args) {
        // Point to the CSV database file
        CsvDatabase database = new CsvDatabase("data/readings.csv");

        // Instantiate the agronomic analysis engine
        SoilAnalyzer analyzer = new SoilAnalyzer();

        // Launch the console interface
        ConsoleMenu menu = new ConsoleMenu(database, analyzer);
        menu.start();
    }
}
