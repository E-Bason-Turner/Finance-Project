package financeproject2;

import java.util.ArrayList;
import java.lang.Math;
/**
 * The class gathers the statistics used for the stock simulations in terms of the log-returns of the daily stock changes.
 *
 * @author Zach Archibald and Evan Turner
 * @version 2023-12-12
 */
public class GatherDriftsAndVolatilities 
{
	public static ArrayList<ArrayList<Double>> gatherStatisticsForStocks(ArrayList<ArrayList<Double>> LogReturnsOfStocks,
			int upSampleFactor)
	{
		ArrayList<ArrayList<Double>> driftsAndVolatilitiesOfStocks = new ArrayList<ArrayList<Double>>();
		
		for (int i = 0; i < LogReturnsOfStocks.size(); i++)
		{
			ArrayList<Double> logReturnsOfStock = LogReturnsOfStocks.get(i);
			
			Double driftOfStock = computeDriftOfStock(logReturnsOfStock);
			Double volatilityOfStock = computeVolatilityOfStock(logReturnsOfStock, driftOfStock);

			
			//Collect statistics into one arraylist for easier use in calculations 
			ArrayList<Double> driftsAndVolatilitiesOfStock = new ArrayList<Double>();
			driftsAndVolatilitiesOfStock.add(driftOfStock);
			driftsAndVolatilitiesOfStock.add(volatilityOfStock);
			
			driftsAndVolatilitiesOfStocks.add(driftsAndVolatilitiesOfStock);
		}
		
		return driftsAndVolatilitiesOfStocks;
	}
	
	/**
	 * Calculates log return of stocks for calculations of drift and volatility by subtracting 
	 * consecutive prices and then taking the logarithm of those.
	 * 
	 * @param dailyPricesOfStock
	 * @return- Double representing LogReturnsOfStock
	 */
	public static ArrayList<Double> generateLogReturnsOfStock(ArrayList<Double> dailyPricesOfStock)
	{
		ArrayList<Double> LogReturnsOfStock = new ArrayList<Double>();
		
		for (int i = 0; i < dailyPricesOfStock.size() - 1; i++)
		{
			Double LogReturn = Math.log(dailyPricesOfStock.get(i+1) / dailyPricesOfStock.get(i));
			LogReturnsOfStock.add(LogReturn);
		}
		
		return LogReturnsOfStock;
	}
	
	/**
	 * Calculates drift of the stock from the log returns of stock
	 * 
	 * @param logReturnsOfStock
	 * @return-Double representing yearly drift of a stock
	 */
	private static Double computeDriftOfStock(ArrayList<Double> logReturnsOfStock) 
	{
		Double drift = 250.0 * (sum(logReturnsOfStock)) / logReturnsOfStock.size();
		return drift;
	}

	/**
	 * Calculates volatility of the stock from the log returns of stock and drifts of a stock
	 * 
	 * @param logReturnsOfStock
	 * @return-Double representing yearly volatility of a stock
	 */
	private static Double computeVolatilityOfStock(ArrayList<Double> logReturnsOfStock, Double driftOfStock) 
	{
		Double volatilityOfStock = 250 * variance(logReturnsOfStock, driftOfStock / 250);
		
		return volatilityOfStock;
	}

	/**
	 * Computes the sum of an ArrayList
	 * 
	 * @param data
	 * @return-the sum of an ArrayList
	 */
	public static Double sum(ArrayList<Double> data) 
	{
		Double sum = 0.0;
		for (int i = 0; i < data.size(); i++)
		{
			sum += data.get(i);
		}
		
		return sum;
	}
	
	/**
	 * Computes the variance of an ArrayList as the average of the squared and centered data
	 * 
	 * @param data
	 * @param mean
	 * @return-variance of an ArrayList
	 */
	private static Double variance(ArrayList<Double> data, Double mean) 
	{
		ArrayList<Double> centeredData = center(data, mean);
		
		ArrayList<Double> squaredCenteredData = squareElements(centeredData);
		
		Double variance = sum(squaredCenteredData) / data.size();
		
		return variance;
	}

	/**
	 * Centers the data according to the mean
	 * 
	 * @param data
	 * @param mean
	 * @return-ArrayList containing the centered data
	 */
	private static ArrayList<Double> center(ArrayList<Double> data, Double mean) 
	{
		ArrayList<Double> centeredData = new ArrayList<Double>();
		
		for(int i = 0; i < data.size(); i++)
		{
			centeredData.add(data.get(i) - mean);
		}
		
		return centeredData;
	}

	/**
	 * Squares each element of an ArrayList
	 * 
	 * @param data
	 * @return
	 */
	private static ArrayList<Double> squareElements(ArrayList<Double> data) 
	{
		ArrayList<Double> dataSquared = new ArrayList<Double>();
		for (Double d : data)
		{
			dataSquared.add(Math.pow(d,2));
		}
		
		return dataSquared;
	}
	
	/**
	 * Helper to generate the log returns
	 * 
	 * @param dailyPricesOfStocks
	 * @return
	 */
	public static ArrayList<ArrayList<Double>> generateLogReturnsOfStocks(ArrayList<ArrayList<Double>> dailyPricesOfStocks)
	{
		ArrayList<ArrayList<Double>> logReturnsOfStocks = new ArrayList<ArrayList<Double>>();
		
		for (int i = 0; i < dailyPricesOfStocks.size(); i++)
		{
			ArrayList<Double> dailyPricesOfStock = dailyPricesOfStocks.get(i);
			ArrayList<Double> logReturnsOfStock = generateLogReturnsOfStock(dailyPricesOfStock);
			
			logReturnsOfStocks.add(logReturnsOfStock);
		}
		
		return logReturnsOfStocks;
	}
	
	
	
}