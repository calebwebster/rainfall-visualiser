package au.calebwebster.rainfallvisualiser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class RainfallAnalyser {

    public static RainfallDataset analyseDataset(File file) {

        LinkedList<String[]> csvData = null;

        try {
            csvData = readCSVData(file);
        } catch (IOException e) {
        }

        if (csvData == null)
            return null;

        if (csvData.size() < 2)
            return null;

        String[] header = csvData.removeFirst();

        if (header.length < 8)
            return null;

        String[] firstLine = csvData.getFirst();

        String productCode = firstLine[0];
        String stationNumber = firstLine[1];

        RainfallDataset model = new RainfallDataset(file.getName(), productCode, stationNumber);

        int currentYear = 0;
        int currentMonth = 0;
        double totalRainfall = 0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = Double.NEGATIVE_INFINITY;
        // Set initial values for min and max rainfall.
        for (String[] line: csvData) {
            int year = Integer.parseInt(line[2]);
            int month = Integer.parseInt(line[3]);
            double rainfall = 0;
            try {
                rainfall = Double.parseDouble(line[5]);
            } catch (NumberFormatException e) {

            }
            // Make values equal to prevent useless record being added at start.
            if (currentYear == 0 && currentMonth == 0) {
                currentYear = year;
                currentMonth = month;
            }
            // When month changes, save the data for the previous month as a record and
            // reset total, min, and max.
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
            if (rainfall < minRainfall) {
                minRainfall = rainfall;
            }
            if (rainfall > maxRainfall) {
                maxRainfall = rainfall;
            }
        }
        // Add the final record.
        model.addRecord(currentYear, currentMonth, totalRainfall, minRainfall, maxRainfall);
        return model;
    }

    public static LinkedList<String[]> readCSVData(File file) throws IOException {
        LinkedList<String[]> csvData = null;
        Reader reader = new FileReader(file);
        CSVReader csvReader = new CSVReader(reader);
        try {
            csvData = (LinkedList<String[]>) csvReader.readAll();
        } catch (CsvException e) {
        } finally {
            csvReader.close();
        }
        return csvData;
    }
}