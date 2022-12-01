package version2;

import java.util.ArrayList;
import java.util.List;

public class RainfallDataset {
    
    private static final String[] HEADINGS = {"stationName", "productCode", "stationNumber", "year", "month", "total", "min", "max"};
    private final String stationName;
    private final String productCode;
    private final String stationNumber;
    private final ArrayList<RainfallRecord> records;
    
    public RainfallDataset(String stationName, String productCode, String stationNumber) {
        this.stationName = stationName;
        this.productCode = productCode;
        this.stationNumber = stationNumber;
        this.records = new ArrayList<>();
    }
    
    /**
     * Creates a new data record and add it to the model.
     * @param year year.
     * @param month month.
     * @param totalRainfall total rainfall.
     * @param minRainfall minimum rainfall.
     * @param maxRainfall maximum rainfall.
     */
    public void addRecord(int year, int month, double totalRainfall, double minRainfall, double maxRainfall) {
        RainfallRecord record = new RainfallRecord(year, month, totalRainfall, minRainfall, maxRainfall);
        records.add(record);
    }
    
    /**
     * Converts model's records to a list of lines that can
     * be written to a csv file.
     * Format: stationName,productCode,stationNumber,year,month,total,min,max.
     * @return List of String[] arrays containing values.
     */
    public List<String[]> generateCsvData() {
        ArrayList<String[]> dataToWrite = new ArrayList<>();
        dataToWrite.add(HEADINGS);
        for (RainfallRecord r : records) {
            dataToWrite.add(new String[] {
                    stationName,
                    productCode,
                    stationNumber,
                    String.format("%d", r.getYear()),
                    String.format("%d", r.getMonth()),
                    String.format("%.2f", r.getTotalRainfall()),
                    String.format("%.2f", r.getMinRainfall()),
                    String.format("%.2f", r.getMaxRainfall()),
            });
        }
        return dataToWrite;
    }
    
    public String getStationName() {
        return stationName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public String getStationNumber() {
        return stationNumber;
    }
    
    public ArrayList<RainfallRecord> getRecords() {
        return records;
    }
    
    public String toString() {
        return stationName;
    }
    
}
