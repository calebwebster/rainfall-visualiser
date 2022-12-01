package version2;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RainfallAnalyser {
    
    private static final int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    /**
     * This program takes an input and an output directory.
     * It processes all valid .csv files in input dir,
     * calculates stats, and saves output to output dir.
     */
    public static void main(String[] args) {
    
        Scanner input = new Scanner(System.in);
        System.out.print("Input Directory: ");
        String inputDirectory = input.nextLine();
        System.out.print("Output Directory: ");
        String outputDirectory = input.nextLine();
        
        try {
            String[] filesInDirectory = FileHandler.listFilesInDirectory(inputDirectory);
            for (String inputFile : filesInDirectory) {
                if (!inputFile.endsWith(".csv")) continue;
                System.out.println(inputFile);
                try {
                    List<String[]> data = FileHandler.readCSVData(inputDirectory + "/" + inputFile);
                    // Station name is derived from file name and used for the data model.
                    String stationName = inputFile.substring(0, inputFile.length() - 4);  // Strip off ".csv".
                    RainfallDataset dataModel = createModelFromCSVData(data, stationName);
                    List<String[]> calculatedStats = dataModel.generateCsvData();
                    String outputFile = outputDirectory + "/" + stationName + "_analysed" + ".csv";
                    FileHandler.writeCSVData(outputFile, calculatedStats);
                } catch (IOException e) {
                    System.out.println("CSV file was not found.");
                } catch (CsvException e) {
                    System.out.println("There was an error with the CSV file.");
                } catch (NumberFormatException e) {
                    System.out.println("There was an error with the data.");
                } catch (InvalidRecordException e) {
                    System.out.println(e.getMessage());
                } catch (NoDataFoundException e) {
                    System.out.println("No data was found.");
                } catch (Exception e) {
                    System.out.println("Invalid .csv file.");
                }
            }
        } catch (DirectoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Processes csv rainfall data and calculates total,
     * minimum, and maximum monthly rainfall, creating
     * Records and storing them in a RainfallDataModel object.
     * @param data list of string arrays containing csv data.
     * @param stationName name of weather station.
     * @return RainfallDataModel object created.
     * @throws InvalidRecordException if an invalid month is detected (< 0 or > 12).
     * @throws NumberFormatException if an invalid value is detected.
     * @throws NoDataFoundException if no data is found.
     */
    public static RainfallDataset createModelFromCSVData(List<String[]> data, String stationName)
            throws InvalidRecordException, NumberFormatException, NoDataFoundException {
        
        if (data.size() < 2) { throw new NoDataFoundException("No data was found."); }
        
        String productCode = data.get(1)[0];
        String stationNumber = data.get(1)[1];
        
        RainfallDataset model = new RainfallDataset(stationName, productCode, stationNumber);
    
        int currentYear = 0;
        int currentMonth = 0;
        double totalRainfall = 0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = Double.NEGATIVE_INFINITY;
        // Set initial values for min and max rainfall.
        for (int i = 1; i < data.size(); i++) {  // i = 1 to skip csv header.
            String[] line = data.get(i);
            int year = Integer.parseInt(line[2]);
            int month = Integer.parseInt(line[3]);
            int day = Integer.parseInt(line[4]);
            double rainfall = 0;
            if (!line[5].isBlank()) {
                rainfall = Double.parseDouble(line[5]);
            }
            // Advanced error checking.
            if (year < 0) throw new InvalidRecordException("Year " + year + " is invalid.");
            if (month < 0 || month > 12) throw new InvalidRecordException("Month " + month + " is invalid.");
            if (day < 0 || day > daysInMonth[month - 1]) throw new InvalidRecordException("Day " + day + " is invalid.");
            if (rainfall < 0) throw new InvalidRecordException("Rainfall value " + rainfall + " is invalid.");
            // Make values equal to prevent useless record being added at start.
            if (currentYear == 0 && currentMonth == 0) {
                currentYear = year;
                currentMonth = month;
            }
            // When month changes, save the data for the previous month as a record and reset total, min, and max.
            if (month != currentMonth) {
                model.addRecord(currentYear, currentMonth, totalRainfall, minRainfall, maxRainfall);
                currentMonth = month;
                currentYear = year;
                totalRainfall = 0;
                minRainfall = Double.POSITIVE_INFINITY;
                maxRainfall = Double.NEGATIVE_INFINITY;
            }
            totalRainfall += rainfall;
            // Update minimum and maximum daily rainfall.
            if (rainfall < minRainfall) { minRainfall = rainfall; }
            if (rainfall > maxRainfall) { maxRainfall = rainfall; }
        }
        // Add the final record.
        model.addRecord(currentYear, currentMonth, totalRainfall, minRainfall, maxRainfall);
        return model;
    }
}
