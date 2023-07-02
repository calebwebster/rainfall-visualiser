package au.calebwebster.rainfallvisualiser;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RainfallVisualiser extends Application {

    public static final String[] MONTH_NAMES = {
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
    };
    public static BarChart<String, Number> barChart;
    public static ArrayList<RainfallDataset> dataModels;
    public static RainfallDataset currentDataModel;
    public static ChoiceBox<RainfallDataset> datasetPicker;
    public static ChoiceBox<Integer> startYearPicker;
    public static ChoiceBox<Integer> endYearPicker;
    public static CsvException something;
    public static Label dataModelInfo;
    public static Label messageBox;
    public static Label dataNodeInfo;
    public static Scene scene;
    public static Stage stage;

    public void start(Stage stage) {
        
        RainfallVisualiser.stage = stage;

        stage.setTitle("RainfallVisualiser");
        dataModels = new ArrayList<>();
        // Load first year of data.
        createBarChart();
        Button processButton = new Button("Select Datasets");
        processButton.setPrefWidth(170);
        processButton.setOnAction(e -> openDatasets());
        // Info label.
        dataModelInfo = new Label("Enter a directory and click \"Process Datasets\" to get started.");
        dataModelInfo.setWrapText(true);
        dataModelInfo.setPrefWidth(170);
        // Message box.
        messageBox = new Label();
        messageBox.setWrapText(true);
        messageBox.setPrefWidth(170);
        messageBox.setMaxHeight(200);
        // Data node info label.
        dataNodeInfo = new Label("Click on a bar to view data.");
        dataNodeInfo.setWrapText(true);
        dataNodeInfo.setMaxHeight(50);
        dataNodeInfo.setPrefWidth(170);
        // Dataset picker
        datasetPicker = new ChoiceBox<>();
        datasetPicker.setPrefWidth(170);
        // Year picker box
        startYearPicker = new ChoiceBox<>();
        startYearPicker.setPrefWidth(75);
        endYearPicker = new ChoiceBox<>();
        endYearPicker.setPrefWidth(75);
        HBox yearPickerBox = new HBox(20);
        yearPickerBox.getChildren().addAll(startYearPicker, endYearPicker);
        // Setup UI layout.
        BorderPane root = new BorderPane();
        VBox rightPanel = new VBox(10);
        root.setCenter(barChart);
        root.setRight(rightPanel);
        rightPanel.setPadding(new Insets(10, 10, 10, 10));
        rightPanel.getChildren().addAll(
                processButton,
                datasetPicker, yearPickerBox,
                dataModelInfo, dataNodeInfo, messageBox);
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void updateYearSelectors(RainfallDataset newDataset) {
        // Remove action to avoid being executed with no items.
        startYearPicker.setOnAction(null);
        endYearPicker.setOnAction(null);
        // Clear all values.
        startYearPicker.getItems().clear();
        endYearPicker.getItems().clear();
        // Add year values from data model.
        for (RainfallRecord r : newDataset.getRecords()) {
            if (!startYearPicker.getItems().contains(r.getYear())) {
                startYearPicker.getItems().add(r.getYear());
                endYearPicker.getItems().add(r.getYear());
            }
        }
        // Set selection to the first year.
        startYearPicker.setValue(startYearPicker.getItems().get(0));
        endYearPicker.setValue(startYearPicker.getItems().get(0));
        // Re-bind action.
        startYearPicker.setOnAction(e -> refreshData());
        endYearPicker.setOnAction(e -> refreshData());
    }

    public static void updateDatasetSelector() {
        datasetPicker.setOnAction(null);
        datasetPicker.getItems().clear();
        for (RainfallDataset model : dataModels) {
            datasetPicker.getItems().add(model);
        }
        // If no data model is currently selected (only on initial load), set selection
        // to first data model.
        if (currentDataModel == null) {
            datasetPicker.setValue(datasetPicker.getItems().get(0));
        } else {
            datasetPicker.setValue(currentDataModel);
        }
        datasetPicker.setOnAction(e -> changeDataset());
    }

    public static void updateDatasetInfo() {
        dataModelInfo.setText(String.format(
                "Dataset File:%n%s%n%nProduct Code:%n%s%n%nStation Number:%n%s",
                currentDataModel.getFilename(),
                currentDataModel.getProductCode(),
                currentDataModel.getFilename()));
    }

    public static void showInfoMessage(String message) {
        messageBox.setText(message + "\n" + messageBox.getText());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(false);
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);
        barChart.setTitle("Rainfall Statistics");
        xAxis.setLabel("Month");
        yAxis.setLabel("Rainfall (mm)");
    }

    public static void clearBarChart() {
        barChart.getData().clear();
    }

    public static boolean yearRangeIsValid(int startYear, int endYear) {
        return startYear <= endYear;
    }

    public static boolean recordIsWithinYearRange(RainfallRecord record, int startYear, int endYear) {
        return (record.getYear() >= startYear) && (record.getYear() <= endYear);
    }

    public static void loadDataIntoBarChart(RainfallDataset dataset, int startYear,
            int endYear) {
        XYChart.Series<String, Number> totalRainfallSeries = new XYChart.Series<>();
        totalRainfallSeries.setName("Total");

        XYChart.Series<String, Number> minRainfallSeries = new XYChart.Series<>();
        minRainfallSeries.setName("Minimum");

        XYChart.Series<String, Number> maxRainfallSeries = new XYChart.Series<>();
        maxRainfallSeries.setName("Maximum");

        barChart.getData().addAll(totalRainfallSeries, minRainfallSeries, maxRainfallSeries);

        if (!yearRangeIsValid(startYear, endYear))
            return;

        // Add data from records between selected years.
        for (RainfallRecord record : dataset.getRecords()) {
            if (recordIsWithinYearRange(record, startYear, endYear)) {
                String categoryName = record.getMonth() + "/" + record.getYear();
                totalRainfallSeries.getData().add(new XYChart.Data<>(categoryName, record.getTotalRainfall()));
                minRainfallSeries.getData().add(new XYChart.Data<>(categoryName, record.getMinRainfall()));
                maxRainfallSeries.getData().add(new XYChart.Data<>(categoryName, record.getMaxRainfall()));
            }
        }

        // Add tooltips to data nodes.
        for (int i = 0; i < barChart.getData().size(); i++) {
            XYChart.Series<String, Number> series = barChart.getData().get(i);
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node dataNode = data.getNode();
                // Extract month from category name.
                String categoryName = data.getXValue();
                String[] parts = categoryName.split("/");
                int month = Integer.parseInt(parts[0]);
                // Create tooltip.
                Tooltip tooltip = new Tooltip();
                tooltip.setShowDelay(new Duration(0));
                tooltip.setText(String.format("%s for %s %s:%n%.2fmm", series.getName(), MONTH_NAMES[month - 1],
                        parts[1], (double) data.getYValue()));
                Tooltip.install(dataNode, tooltip);
                // Set onclick event to data node that updates info label.
                dataNode.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        dataNodeInfo.setText(String.format("%s for %s %s:%n%.2fmm", series.getName(),
                                MONTH_NAMES[month - 1], parts[1], (double) data.getYValue()));
                    }
                });
            }
        }
    }

    public static void refreshData() {
        int startYear = startYearPicker.getValue();
        int endYear = endYearPicker.getValue();
        clearBarChart();
        loadDataIntoBarChart(currentDataModel, startYear, endYear);
    }

    public static void changeDataset() {
        currentDataModel = datasetPicker.getValue();
        updateYearSelectors(currentDataModel);
        refreshData();
        updateDatasetInfo();
        dataNodeInfo.setText("Click on a bar to view data.");
    }

    public static void openDatasets() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Dataset folder");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("CSV Files", "*.csv"));
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files == null)
            return;
            
        dataModels.clear();

        for (File file : files) {
            RainfallDataset dataModel = RainfallAnalyser.analyseDataset(file);
            if (dataModel == null) {
                showInfoMessage(String.format("Failed to load %s", file.getName()));
                continue;
            }
            showInfoMessage(String.format("Loaded %s", file.getName()));
            dataModels.add(dataModel);
        }

        currentDataModel = dataModels.get(0);
        updateDatasetSelector();

        if (dataModels.size() == 0) {
            clearBarChart();
            return;
        }

        changeDataset();
    }
}
