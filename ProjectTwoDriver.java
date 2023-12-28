package financeproject2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This program parses stock data from a txt file and simulates an SDE system. In doing so, it generates predictions 
 * of the trends of a system of stocks during the same time interval as the original data.
 * The program is equipped to upsample the data by a factor of 5 in order to better simulate the daily fluctuations of stocks.
 *
 * The program prints the stock data into a txt file called SimulatedTrajectories.txt
 * 
 * The class acts as the driver for the program. It first prints the statistics for the stocks to the console and then 
 * prints the simulation system.
 *
 * @author Zach Archibald and Evan Turner
 * @version 2023-12-12
 */
public class ProjectTwoDriver 
{
	public static void main(String[] args) throws IOException
	{
		int upSampleFactor = 5;
		
		File stockData = new File("StockData.txt");
		
		//Create the lists that will hold important elements of the equation
		ArrayList<ArrayList<Double>> dailyPricesOfStocks = formatStockData(stockData);
		
		ArrayList<ArrayList<Double>> logReturnsOfStocks = 
				GatherDriftsAndVolatilities.generateLogReturnsOfStocks(dailyPricesOfStocks);
		
		ArrayList<ArrayList<Double>> driftsAndVolatilities = 
				GatherDriftsAndVolatilities.gatherStatisticsForStocks(logReturnsOfStocks, upSampleFactor);
		
		ArrayList<ArrayList<Double>> covolatilityMatrix =
		CovolatilityMatrixCalculator.generateCovolatilityMatrix(dailyPricesOfStocks, driftsAndVolatilities, logReturnsOfStocks);
		
		//Create the SDE system using stock statistics
		SDESystem SDESystemForStocks = new SDESystem(driftsAndVolatilities, covolatilityMatrix);
		
		//Uncomment the line below and the helper method printSDEComponents() found in SDESystem 
		//to print statistics for the stocks to the console
		//SDESystemForStocks.printSDEComponents();
		
		//Simulate trajectories using the SDE system
		simulateTenTrajectories(SDESystemForStocks, dailyPricesOfStocks, upSampleFactor);
		
		NumericalSDESolution simulation = new NumericalSDESolution(SDESystemForStocks, dailyPricesOfStocks,
				upSampleFactor);
		
		formattedPrint(simulation.getDailyPrices(), upSampleFactor);
	}

	/**
	 * Read through the txt file and retrieve information. The txt file must be formatted in a manner similar to the
	 * csv file that can be downloaded from the majority of finance websites.
	 * 
	 * @param stockData-File containing stock data
	 * @return-Daily prices of the stocks
	 * @throws FileNotFoundException
	 */
	private static ArrayList<ArrayList<Double>> formatStockData(File stockData) throws FileNotFoundException 
	{
		ArrayList<ArrayList<Double>> dailyPricesOfStocks = new ArrayList<ArrayList<Double>>();
		
		Scanner fileScanner = new Scanner(stockData);
		
		while (fileScanner.hasNextLine())
		{
			String currentLine = new String(fileScanner.nextLine());
			
			if (currentLine.length() > 0 && currentLine.charAt(0)=='{') 
			{
				addStockToDailyPricesOfStocks(fileScanner, dailyPricesOfStocks);
			}
		}
		
		fileScanner.close();
		
		return dailyPricesOfStocks;
	}

	/**
	 * Adds one day to the ArrayList dailyPricesOfStocks
	 * 
	 * @param fileScanner-The scanner from formatStockData
	 * @param dailyPricesOfStocks-The ArrayList containing daily prices of stocks
	 */
	private static void addStockToDailyPricesOfStocks(Scanner fileScanner, ArrayList<ArrayList<Double>> dailyPricesOfStocks) 
	{
		ArrayList<Double> dailyPricesOfStock = new ArrayList<Double>();
		
		while (fileScanner.hasNext())
		{
			String currentString = fileScanner.next();
			
			if (currentString.charAt(0) == '}')
					break;
			
			else 
			{
				dailyPricesOfStock.add(Double.parseDouble(currentString));
			}
		}
		
		dailyPricesOfStocks.add(dailyPricesOfStock);
	}
	
	private static void formattedPrint(ArrayList<ArrayList<Double>> dailyPrices, int upSampleFactor) 
	{
		for (int day = 0; day < (dailyPrices.get(0).size()); day++)
		{
			for (int stock = 0; stock < dailyPrices.size(); stock++)
			{
				System.out.print(dailyPrices.get(stock).get(day) + "\t");
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Writes the data for ten trajectories of the system and prints it to the file SimulatedTrajectories.txt
	 * 
	 * @param SDESystem
	 * @param dailyPricesOfStocks
	 * @param upSampleFactor
	 * @throws IOException
	 */
	private static void simulateTenTrajectories(SDESystem SDESystem, 
			ArrayList<ArrayList<Double>> dailyPricesOfStocks, int upSampleFactor) throws IOException 
	{
		BufferedWriter evilShadowSkull = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream("SimulatedTrajectories.txt")));
		
		ArrayList<ArrayList<ArrayList<Double>>> simulatedData = new ArrayList<ArrayList<ArrayList<Double>>>();
		
		//build simlatedData
		for(int simulation = 0; simulation < 10; simulation++)
		{
			NumericalSDESolution simI = new NumericalSDESolution(SDESystem, dailyPricesOfStocks, upSampleFactor);
			
			simulatedData.add(simI.getDailyPrices());
		}
		
		//Gather each stocks data from simulated data and print
		for(int stock = 0; stock < dailyPricesOfStocks.size(); stock++)
		{
			evilShadowSkull.newLine();
			evilShadowSkull.write("Stock " + stock);
			evilShadowSkull.newLine();
			
			for(int day = 0; day < dailyPricesOfStocks.get(0).size(); day++)
			{
				for(int sim = 0; sim < 10; sim++)
				{
					evilShadowSkull.write(simulatedData.get(sim).get(stock).get(day) + "\t");
				}
				evilShadowSkull.write(dailyPricesOfStocks.get(stock).get(day) + "\t");
				evilShadowSkull.newLine();
			}
			
			evilShadowSkull.newLine();

		}
		evilShadowSkull.close();
		File file = new File("SimulatedTrajectories.txt");
		file.createNewFile();
	}
	
}
