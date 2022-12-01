package version1;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RainfallAnalyser {
    
    private static final String[] HEADINGS = {"year", "month", "total", "min", "max"};
    
    /**
     * This program takes a .csv file as input and processes
     * the rainfall data inside, calculating the monthly total,
     * minimum and maximum rainfall, and writing that data into
     * another .csv file.
     */
    public static void main(String[] args) {
    
        Scanner input = new Scanner(System.in);
        System.out.print("Dataset file (Hey Caleb, use the datasets under the root folder for V1): ");
        String inputFile = input.nextLine();
        try {
            List<String[]> data = readCSVData(inputFile);
            ArrayList<String[]> calculatedStats = calculateCsvStats(data);
            printData(calculatedStats);
            String outputFile = createOutputFilename(inputFile);
            writeCSVData(outputFile, calculatedStats);
        } catch (IOException e) {
            System.out.println("CSV file was not found.");
        } catch (CsvException e) {
            System.out.println("There was an error with the CSV file.");
        } catch (NumberFormatException e) {
            System.out.println("There was an error with the data.");
        } catch (Exception e) {
            System.out.println("Invalid csv file.");
        }
    }

    private static void printData(List<String[]> data) {
        for (String[] line : data) {
            System.out.println(Arrays.toString(line).substring(1, Arrays.toString(line).length() - 1));
        }
    }

    private static String createOutputFilename(String inputFileName) {
        return inputFileName.substring(0, inputFileName.length() - 4) + "_analysed" + ".csv";
    }
    
    /**
     * Reads a csv file and returns a list of the lines it contains.
     * Data is given as a list of string arrays that store the values in each line.
     * Format: product code,station number,year,month,day,rainfall,num of days,quality
     * @param filename name of csv file to read.
     * @return list of strings.
     * @throws IOException if file is not found.
     * @throws CsvException if there is another error with the csv.
     */
    private static List<String[]> readCSVData(String filename) throws IOException, CsvException {
        Reader reader = Files.newBufferedReader(Paths.get(filename));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();
        csvReader.close();
        return data;
    }
    
    /**
     * Processes rainfall csv data and calculates monthly totals, minimums, maximums.
     * Result is given as a list of string arrays that store the values in each line.
     * Format: year,month,total,min,max
     * @param data csv data to process.
     * @return calculated statistics as a list of string arrays.
     */
    private static ArrayList<String[]> calculateCsvStats(List<String[]> data) {
        ArrayList<String[]> statistics = new ArrayList<>();
        statistics.add(HEADINGS);
        int currentYear = 0;
        int currentMonth = 1;
        double totalRainfall = 0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < data.size(); i++) {  // i = 1 to skip csv header.
            String[] line = data.get(i);
            int year = Integer.parseInt(line[2]);
            int month = Integer.parseInt(line[3]);
            if (!isMonthValid(month)) { throw new NumberFormatException("Month " + month + " is invalid"); }
            // If rainfall is not recorded, default is 0.
            double rainfall = line[5].isBlank() ? 0 : Double.parseDouble(line[5]);
            // When month changes, save the data for the previous month to the list.
            if (month != currentMonth) {
                if (currentYear == 0) { currentYear = year; }
                statistics.add(new String[] {
                        String.format("%d", currentYear),
                        String.format("%d", currentMonth),
                        String.format("%.2f", totalRainfall),
                        String.format("%.2f", minRainfall),
                        String.format("%.2f", maxRainfall),
                });
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
        // Add final data record.
        statistics.add(new String[] {
                String.format("%d", currentYear),
                String.format("%d", currentMonth),
                String.format("%.2f", totalRainfall),
                String.format("%.2f", minRainfall),
                String.format("%.2f", maxRainfall),
        });
        return statistics;
    }

    private static boolean isMonthValid(int month) {
        return month <= 12 && month > 0;
    }

    private static void writeCSVData(String filename, List<String[]> lines) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(filename));
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeAll(lines);
        csvWriter.close();
    }
}
