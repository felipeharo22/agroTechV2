# Oil Palm Soil-Moisture Management System (CSV-Driven)

A clean, beginner-friendly Java console project for university-level Object-Oriented Programming (OOP) courses. It uses a 4-column CSV file as a database to store, read, edit, and analyze capacitive soil-moisture sensor readings across African oil palm (*Elaeis guineensis*) lots.

---

## 1. Simplified Architecture (7 Classes)

The codebase has been refactored to remove all unnecessary overhead and focus strictly on the required CSV database and agronomic decision-making:

```
agroTechV2/
├── data/
│   └── readings.csv                     # Persistent CSV database
├── src/
│   ├── modelo/                          # Domain entities & inheritance
│   │   ├── Reading.java                 # Entity matching the 4 CSV columns
│   │   ├── Recommendation.java          # Base recommendation class (Parent)
│   │   └── IrrigationRecommendation.java# Specialized recommendation (Child)
│   ├── negocio/                         # Business logic & CSV database operations
│   │   ├── CsvDatabase.java             # File I/O: read, append, update, delete
│   │   └── SoilAnalyzer.java            # Classification, trend calculation & recommendations
│   └── interfaz/                        # User interface
│       ├── ConsoleMenu.java             # Terminal menu, Scanner validation & formatting
│       └── Main.java                    # Entry point (public static void main)
└── README.md
```

---

## 2. CSV Database Format (`data/readings.csv`)

The persistent database is stored directly in plain CSV text:

```csv
id,date,humidityLevel,sensorId
REC-001,2026-10-25 08:00,42.50,SEN-PALM-01
REC-002,2026-10-25 09:30,55.10,SEN-PALM-02
REC-003,2026-10-25 11:00,68.40,SEN-PALM-03
...
```

| Field | Type | Description |
|---|---|---|
| `id` | String | Unique record identifier (e.g., `REC-001`). Auto-generated on creation. |
| `date` | String | Timestamp of measurement (e.g., `2026-10-25 08:00`). |
| `humidityLevel` | Double | Volumetric soil moisture percentage (e.g., `42.50`). |
| `sensorId` | String | Sensor identifier deployed in the field (e.g., `SEN-PALM-01`). |

---

## 3. How to Compile and Run

### Command Line
```bash
# 1. Compile all source files
javac -d bin src/modelo/*.java src/negocio/*.java src/interfaz/*.java

# 2. Run the application
java -cp bin interfaz.Main
```

### IDEs (IntelliJ, Eclipse, NetBeans)
- Open the project folder `agroTechV2`.
- Set `src` as the Sources Root.
- Run `src/interfaz/Main.java`.

---

## 4. Key OOP Concepts Demonstrated

1. **Separation of Packages:**
   - `modelo`: Holds entity data (`Reading`, `Recommendation`).
   - `negocio`: Handles CSV persistence (`CsvDatabase`) and mathematics (`SoilAnalyzer`).
   - `interfaz`: Handles all `Scanner` and `System.out` interaction (`ConsoleMenu`, `Main`).
2. **Inheritance & Polymorphism:**
   - `Recommendation` is extended by `IrrigationRecommendation`, overriding `getDetails()` to supply water dosage per palm.
3. **Plain Dynamic Arrays:**
   - Array resizing and filtering are achieved using native Java arrays and standard `for` loops without complex collections.
4. **CSV File CRUD Operations:**
   - **Create:** Appends new lines with sequential ID generation (`REC-026`).
   - **Read:** Loads all lines and parses tokens into `Reading` objects.
   - **Update:** Edits the humidity of any record by ID and rewrites the file.
   - **Delete:** Removes any record by ID and updates the CSV.
