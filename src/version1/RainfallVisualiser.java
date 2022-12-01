package version1;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RainfallVisualiser extends Application {

    public void start(Stage stage) {
        Scanner input = new Scanner(System.in);
        System.out.print("Statistics file (Hey Caleb, use the datasets under the root folder for V1): ");
        String inputFile = input.nextLine();
        try {
            List<String[]> data = readCSVData(inputFile);
            printData(data);
            BarChart<String, Number> barChart = createBarChart();
            addDataToBarChart(data, barChart);
            BorderPane root = new BorderPane();
            root.setCenter(barChart);
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("RainfallVisualiser");
            stage.setScene(scene);
            stage.show();
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

    public static List<String[]> readCSVData(String filename) throws IOException, CsvException {
        Reader reader = Files.newBufferedReader(Paths.get(filename));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();
        csvReader.close();
        return data;
    }

    public static BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Rainfall (mm)");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(false);
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);
        barChart.setTitle("Rainfall Statistics");
        return barChart;
    }

    public static void addDataToBarChart(List<String[]> data, BarChart<String, Number> barChart) {
        XYChart.Series<String, Number> totalRainfallSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> minRainfallSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> maxRainfallSeries = new XYChart.Series<>();
        totalRainfallSeries.setName("Total");
        minRainfallSeries.setName("Minimum");
        maxRainfallSeries.setName("Maximum");

        for (int i = 1; i < data.size(); i++) {
            String[] line = data.get(i);
            int year = Integer.parseInt(line[0]);
            int month = Integer.parseInt(line[1]);
            double totalRainfall = Double.parseDouble(line[2]);
            double minRainfall = Double.parseDouble(line[3]);
            double maxRainfall = Double.parseDouble(line[4]);
            String categoryName = month + "/" + year;
            totalRainfallSeries.getData().add(new XYChart.Data<>(categoryName, totalRainfall));
            minRainfallSeries.getData().add(new XYChart.Data<>(categoryName, minRainfall));
            maxRainfallSeries.getData().add(new XYChart.Data<>(categoryName, maxRainfall));
        }
        barChart.getData().addAll(totalRainfallSeries, minRainfallSeries, maxRainfallSeries);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
