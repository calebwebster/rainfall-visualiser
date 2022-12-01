package version2;

public class RainfallRecord {
    
    private final int year;
    private final int month;
    private final double totalRainfall;
    private final double minRainfall;
    private final double maxRainfall;
    
    public RainfallRecord(int year, int month, double totalRainfall, double minRainfall, double maxRainfall) {
        this.year = year;
        this.month = month;
        this.totalRainfall = totalRainfall;
        this.minRainfall = minRainfall;
        this.maxRainfall = maxRainfall;
    }
    
    public int getYear() {
        return year;
    }
    
    public int getMonth() {
        return month;
    }
    
    public double getTotalRainfall() {
        return totalRainfall;
    }
    
    public double getMinRainfall() {
        return minRainfall;
    }
    
    public double getMaxRainfall() {
        return maxRainfall;
    }
    
    public String toString() {
        return String.format("Year: %d Month: %d Total: %.2f Min: %.2f Max: %.2f", year, month, totalRainfall, minRainfall, maxRainfall);
    }
    
}