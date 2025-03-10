// WeatherAnalyzer.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Weatheranalyze {

    /**
     * Represents weather data for a specific day.
     *
     * @param date          The date of the weather data.
     * @param temperature   The temperature in Celsius.
     * @param humidity      The humidity percentage.
     * @param precipitation The precipitation in millimeters.
     */
    record WeatherRecord(LocalDate date, double temperature, int humidity, double precipitation) {
        /**
         * Categorizes the weather based on temperature.
         *
         * @return The weather category.
         */
        public String weatherCategory() {
            return switch ((int) temperature) {
                case int temp when temp < 10 -> "Cold";
                case int temp when temp >= 10 && temp <= 24 -> "Warm";
                case int temp when temp > 24 -> "Hot";
                default -> "Moderate";
            };
        }
    }

    /**
     * Parses weather data from a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @return A list of WeatherRecord objects.
     * @throws IOException If an I/O error occurs.
     */
    static List<WeatherRecord> parseWeatherData(String filePath) throws IOException {
        List<WeatherRecord> weatherData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip header
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0], formatter);
                double temperature = Double.parseDouble(parts[1]);
                int humidity = Integer.parseInt(parts[2]);
                double precipitation = Double.parseDouble(parts[3]);
                weatherData.add(new WeatherRecord(date, temperature, humidity, precipitation));
            }
        }
        return weatherData;
    }

    /**
     * Calculates the average temperature for a specific month.
     *
     * @param weatherData The list of weather data.
     * @param month       The month to calculate the average temperature for.
     * @return The average temperature.
     */
    static double averageTemperatureForMonth(List<WeatherRecord> weatherData, Month month) {
        return weatherData.stream()
                .filter(record -> record.date().getMonth() == month)
                .mapToDouble(WeatherRecord::temperature)
                .average()
                .orElse(0.0);
    }

    /**
     * Finds days with temperatures above a given threshold.
     *
     * @param weatherData The list of weather data.
     * @param threshold   The temperature threshold.
     * @return A list of WeatherRecord objects with temperatures above the threshold.
     */
    static List<WeatherRecord> daysAboveTemperature(List<WeatherRecord> weatherData, double threshold) {
        return weatherData.stream()
                .filter(record -> record.temperature() > threshold)
                .collect(Collectors.toList());
    }

    /**
     * Counts the number of rainy days (precipitation > 0).
     *
     * @param weatherData The list of weather data.
     * @return The count of rainy days.
     */
    static long countRainyDays(List<WeatherRecord> weatherData) {
        return weatherData.stream()
                .filter(record -> record.precipitation() > 0)
                .count();
    }

    /**
     * Analyzes weather data and prints results.
     *
     * @param filePath The path to the CSV file.
     */
    static void analyze(String filePath) {
        try {
            List<WeatherRecord> weatherData = parseWeatherData(filePath);

            double avgAugustTemp = averageTemperatureForMonth(weatherData, Month.AUGUST);
            List<WeatherRecord> hotDays = daysAboveTemperature(weatherData, 33.0);
            long rainyDays = countRainyDays(weatherData);

            String output = """
                    Weather Analysis Results:
                    --------------------------
                    Average August Temperature: %.2f °C
                    Days above 33 °C: %s
                    Rainy Days: %d
                    """.formatted(avgAugustTemp, hotDays, rainyDays);

            System.out.println(output);

            weatherData.forEach(record ->
                    System.out.println("Date: " + record.date() + ", Category: " + record.weatherCategory()));

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Main method to run the weather analyzer.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        analyze("C:\\Users\\kenne\\IdeaProjects\\Module_4_Homework\\Weather\\src\\main\\resources\\weatherdata.csv");
    }
}