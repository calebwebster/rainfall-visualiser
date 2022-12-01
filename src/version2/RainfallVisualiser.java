package version2;

import com.opencsv.exceptions.CsvException;

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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
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
    public static Label dataModelInfo;
    public static Label messageBox;
    public static Label dataNodeInfo;
    public static Scene scene;

    /**
     * Sets up the GUI, creating the bar chart and controls.
     *
     * @param stage stage.
     */
    public void start(Stage stage) {

        stage.setTitle("RainfallVisualiser");
        dataModels = new ArrayList<>();
        // Load first year of data.
        createBarChart();
        // Directory input box and process button.
        Label dirLabel = new Label("Directory:");
        TextField dirInput = new TextField("analysed");
        dirInput.setMaxWidth(170);
        Button processButton = new Button("Process Datasets");
        processButton.setPrefWidth(170);
        processButton.setOnAction(e -> processAnalysedDatasets(dirInput.getText()));
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
        // Setup UI layout.
        BorderPane root = new BorderPane();
        VBox rightPanel = new VBox(10);
        root.setCenter(barChart);
        root.setRight(rightPanel);
        rightPanel.setPadding(new Insets(10, 10, 10, 10));
        rightPanel.getChildren().addAll(
                dirLabel, dirInput, processButton,
                createDataModelSelector(), createBoxWithYearSelectors(),
                dataModelInfo, dataNodeInfo, messageBox
        );
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private static HBox createBoxWithYearSelectors() {
        ChoiceBox<Integer> startYearSelector = new ChoiceBox<>();
        startYearSelector.setId("start-year-selector");
        startYearSelector.setPrefWidth(75);

        ChoiceBox<Integer> endYearSelector = new ChoiceBox<>();
        endYearSelector.setId("end-year-selector");
        endYearSelector.setPrefWidth(75);

        HBox yearSelectorPanel = new HBox(20);
        yearSelectorPanel.getChildren().addAll(startYearSelector, endYearSelector);
        return yearSelectorPanel;
    }

    private static ChoiceBox<RainfallDataset> createDataModelSelector() {
        ChoiceBox<RainfallDataset> datasetSelector = new ChoiceBox<>();
        datasetSelector.setId("dataset-selector");
        datasetSelector.setPrefWidth(170);
        return datasetSelector;
    }

    private static ChoiceBox<Integer> getStartYearSelector() {
        return (ChoiceBox<Integer>) scene.lookup("#start-year-selector");
    }

    private static ChoiceBox<Integer> getEndYearSelector() {
        return (ChoiceBox<Integer>) scene.lookup("#end-year-selector");
    }

    private static ChoiceBox<RainfallDataset> getDatasetSelector() {
        return (ChoiceBox<RainfallDataset>) scene.lookup("#dataset-selector");
    }

    public static void updateYearSelectors(RainfallDataset newDataset) {
        // Remove action to avoid being executed with no items.
        ChoiceBox<Integer> startYearSelector = getStartYearSelector();
        ChoiceBox<Integer> endYearSelector = getEndYearSelector();
        startYearSelector.setOnAction(null);
        endYearSelector.setOnAction(null);
        // Clear all values.
        startYearSelector.getItems().clear();
        endYearSelector.getItems().clear();
        // Add year values from data model.
        for (RainfallRecord r : newDataset.getRecords()) {
            if (!startYearSelector.getItems().contains(r.getYear())) {
                startYearSelector.getItems().add(r.getYear());
                endYearSelector.getItems().add(r.getYear());
            }
        }
        // Set selection to the first year.
        startYearSelector.setValue(startYearSelector.getItems().get(0));
        endYearSelector.setValue(startYearSelector.getItems().get(0));
        // Re-bind action.
        startYearSelector.setOnAction(e -> refreshData());
        endYearSelector.setOnAction(e -> refreshData());
    }

    public static void updateDatasetSelector() {
        ChoiceBox<RainfallDataset> datasetSelector = getDatasetSelector();
        datasetSelector.setOnAction(null);
        datasetSelector.getItems().clear();
        for (RainfallDataset model : dataModels) {
            datasetSelector.getItems().add(model);
        }
        // If no data model is currently selected (only on initial load), set selection to first data model.
        if (currentDataModel == null) {
            datasetSelector.setValue(datasetSelector.getItems().get(0));
        } else {
            datasetSelector.setValue(currentDataModel);
        }
        datasetSelector.setOnAction(e -> changeDataset());
    }

    public static void updateDataModelInfo() {
        dataModelInfo.setText(String.format(
                "Station Name:%n%s%n%nProduct Code:%n%s%n%nStation Number:%n%s",
                currentDataModel.getStationName(),
                currentDataModel.getProductCode(),
                currentDataModel.getStationNumber()));
    }

    public static void sendInfoMessage(String message) {
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

    public static void clearBarChart(BarChart<String, Number> barChart) {
        barChart.getData().clear();
    }

    public static boolean yearRangeIsValid(int startYear, int endYear) {
        return startYear <= endYear;
    }

    public static boolean recordIsWithinYearRange(RainfallRecord record, int startYear, int endYear) {
        return (record.getYear() >= startYear) && (record.getYear() <= endYear);
    }

    public static void loadDataIntoBarChart(BarChart<String, Number> barChart, RainfallDataset dataset, int startYear, int endYear) {
        XYChart.Series<String, Number> totalRainfallSeries = new XYChart.Series<>();
        totalRainfallSeries.setName("Total");

        XYChart.Series<String, Number> minRainfallSeries = new XYChart.Series<>();
        minRainfallSeries.setName("Minimum");

        XYChart.Series<String, Number> maxRainfallSeries = new XYChart.Series<>();
        maxRainfallSeries.setName("Maximum");

        barChart.getData().addAll(totalRainfallSeries, minRainfallSeries, maxRainfallSeries);

        if (!yearRangeIsValid(startYear, endYear)) return;

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
                tooltip.setText(String.format("%s for %s %s:%n%.2fmm", series.getName(), MONTH_NAMES[month - 1], parts[1], (double) data.getYValue()));
                Tooltip.install(dataNode, tooltip);
                // Set onclick event to data node that updates info label.
                dataNode.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        dataNodeInfo.setText(String.format("%s for %s %s:%n%.2fmm", series.getName(), MONTH_NAMES[month - 1], parts[1], (double) data.getYValue()));
                    }
                });
            }
        }
        System.out.printf("Data for %s loaded into bar chart.%n", currentDataModel);
    }

    public static void refreshData() {
        int startYear = getStartYearSelector().getValue();
        int endYear = getEndYearSelector().getValue();
        clearBarChart(barChart);
        loadDataIntoBarChart(barChart, currentDataModel, startYear, endYear);
    }

    /**
     * Loads a new dataset into the bar chart, refreshing it and the year select dropdowns.
     */
    public static void changeDataset() {
        currentDataModel = getDatasetSelector().getValue();
        updateYearSelectors(currentDataModel);
        refreshData();
        updateDataModelInfo();
        dataNodeInfo.setText("Click on a bar to view data.");
    }

    /**
     * Searches a directory and processes csv files, creating rainfall
     * data models and loading them into memory.
     * If processing for the first time, loads data from the first data
     * model into the bar chart.
     * Handles errors by cancelling operation and sending info messages
     * about problem.
     *
     * @param directory name of directory to scan.
     */
    public static void processAnalysedDatasets(String directory) {
        try {
            if (directory.isBlank()) {
                directory = ".";
            }
            int numFilesLoaded = 0;
            for (String filename : FileHandler.listFilesInDirectory(directory)) {
                try {
                    if (filename.endsWith(".csv")) {
                        System.out.println(filename);
                        List<String[]> data = FileHandler.readCSVData(directory + "/" + filename);
                        RainfallDataset dataModel = createModelFromAnalysedDataset(data);
                        System.out.printf("Data model for %s loaded into memory.%n", filename);
                        // Only add data model if it doesn't already exist.
                        if (!dataModelAlreadyExists(dataModel)) {
                            dataModels.add(dataModel);
                            numFilesLoaded++;
                        } else {
                            sendInfoMessage("Identical station file was found.");
                        }
                    }
                } catch (IOException e) {
                    sendInfoMessage("A file could not be found.");
                } catch (CsvException e) {
                    sendInfoMessage("There was an error opening a file.");
                } catch (NumberFormatException e) {
                    sendInfoMessage("There was an error with the data.");
                    e.printStackTrace();
                } catch (InvalidRecordException e) {
                    sendInfoMessage(e.getMessage());
                } catch (NoDataFoundException e) {
                    sendInfoMessage("No data was found.");
                } catch (Exception e) {
                    sendInfoMessage("Invalid .csv file.");
                } finally {
                    sendInfoMessage(String.format("Tried to load %s.", filename));
                }
            }
            sendInfoMessage(String.format("%d files loaded.", numFilesLoaded));
            if (numFilesLoaded != 0) {
                // If new models were loaded, update model selector.
                updateDatasetSelector();
                // Choose first model if none was loaded before.
                if (currentDataModel == null) {
                    currentDataModel = dataModels.get(0);
                    changeDataset();
                }
            }
        } catch (DirectoryNotFoundException e) {
            sendInfoMessage("Directory could not be found.");
        }
    }

    /**
     * Checks if a data model with the same station number as the one
     * passed in already exists in the data model list.
     *
     * @param dataModel data model to check
     * @return true if data model with same station number exists, false otherwise.
     */
    public static boolean dataModelAlreadyExists(RainfallDataset dataModel) {
        for (RainfallDataset dm : dataModels) {
            if (dm.getStationNumber().equals(dataModel.getStationNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes a List of csv data and creates a RainfallDataModel.
     *
     * @param data list of string arrays containing values.
     * @return RainfallDataModel created.
     * @throws InvalidRecordException if an invalid month is detected (< 0 or > 12).
     * @throws NumberFormatException  if an invalid value is detected.
     * @throws NoDataFoundException   if no data is found.
     */
    public static RainfallDataset createModelFromAnalysedDataset(List<String[]> data)
            throws InvalidRecordException, NumberFormatException, NoDataFoundException {

        if (data.size() < 2) {
            throw new NoDataFoundException("No data was found.");
        }

        String stationName = data.get(1)[0];
        String productCode = data.get(1)[1];
        String stationNumber = data.get(1)[2];

        RainfallDataset rainfallData = new RainfallDataset(stationName, productCode, stationNumber);

        String[] line;
        for (int i = 1; i < data.size(); i++) {
            line = data.get(i);
            System.out.println(line[3]);
            int year = Integer.parseInt(line[3]);
            int month = Integer.parseInt(line[4]);
            double totalRainfall = Double.parseDouble(line[5]);
            double minRainfall = Double.parseDouble(line[6]);
            double maxRainfall = Double.parseDouble(line[7]);
            // Advanced error checking to avoid tampering of values.
            if (year < 0) throw new InvalidRecordException("Year " + year + " is invalid.");
            if (month > 12 || month < 0) {
                throw new InvalidRecordException("Month " + month + " is invalid.");
            }
            if (totalRainfall < 0) throw new InvalidRecordException("Rainfall value " + totalRainfall + " is invalid.");
            if (minRainfall < 0) throw new InvalidRecordException("Rainfall value " + minRainfall + " is invalid.");
            if (maxRainfall < 0) throw new InvalidRecordException("Rainfall value " + maxRainfall + " is invalid.");

            rainfallData.addRecord(year, month, totalRainfall, minRainfall, maxRainfall);
        }
        return rainfallData;
    }

}
