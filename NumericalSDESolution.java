package financeproject2;

import java.util.ArrayList;
import java.util.Random;

import Jama.Matrix;
/**
 * This class utilizes the gathered data to run the SDE simulation.
 * 
 * @author Zach Archibald and Evan Turner
 * @version 2023-12-12
 */
public class NumericalSDESolution 
{
	// Create variables 
	
	Random gaussianSampler;
	
	SDESystem SDESystemForStocks;
	
	ArrayList<ArrayList<Double>> dailyPrices;
	
	/**
	 * Runs the simulation returning daily price simulations for each of the stocks.
	 * 
	 * @param SDESystemForStocks-Set up of the system contained in class SDESystem
	 * @param dailyPricesOfStocks-Data read from the input txt file of daily stock prices
	 * @param upSampleFactor-Upsample factor taken as an integer 
	 */
	public NumericalSDESolution(SDESystem SDESystemForStocks, ArrayList<ArrayList<Double>> dailyPricesOfStocks,
			int upSampleFactor) 
	{
		this.SDESystemForStocks = SDESystemForStocks;
		
		this.gaussianSampler = new Random();
		
		this.dailyPrices = new ArrayList<ArrayList<Double>>();
		
		//Adds the right number of ArrayLists into the dailyPricesOfStocks structure
		for (int i = 0; i < dailyPricesOfStocks.size(); i++)
		{
			this.dailyPrices.add(new ArrayList<Double>());
			this.dailyPrices.get(i).add(dailyPricesOfStocks.get(i).get(0));
		}
	
		//Simulates each daily price
		for (int i = 1; i < dailyPricesOfStocks.get(0).size() * upSampleFactor; i++)
		{
			ArrayList<Double> nextValues = simulateDay(i, upSampleFactor);
			
			addToDailyPrices(nextValues);
		}
	}
	
	/**
	 * Helper method to add an ArrayList representing a single days price of each stock
	 * 
	 * @param nextValues-A single days price of each stock
	 */
	private void addToDailyPrices(ArrayList<Double> nextValues)
	{
		for (int i = 0; i < nextValues.size(); i++)
		{
			dailyPrices.get(i).add(nextValues.get(i));
		}
	}
	
	/**
	 * Uses the Euler-Maruyama method to approximate a day of stock data using the previous day
	 * 
	 * @param day-The next day in the system
	 * @param upSampleFactor
	 * @return-A list of one day's stock prices
	 */
	
	private ArrayList<Double> simulateDay(int day, int upSampleFactor) 
	{
		
		ArrayList<Double> yesterdaysStockPrices = new ArrayList<Double>();
		for (int i = 0; i < dailyPrices.size(); i++)
		{
			yesterdaysStockPrices.add(this.dailyPrices.get(i).get(day-1));
		}
		
		
		ArrayList<Double> todaysStockPrices = eulerMaruyamaIteration(yesterdaysStockPrices, upSampleFactor);
			
		return todaysStockPrices;
	}
	
	/**
	 * Runs the Euler-Maruyama method
	 * 
	 * @param Sj-ArrayList containing information for the previous step in the simulation
	 * @param upSampleFactor
	 * @return-ArrayList containing information for the current step in the simulation
	 */
	private ArrayList<Double> eulerMaruyamaIteration(ArrayList<Double> Sj, int upSampleFactor) 
	{
		ArrayList<Double> Sjplus1 = new ArrayList<Double>();
		
		//Gathers statistics for the data
		ArrayList<Double> drifts = SDESystemForStocks.getDriftsOfStocks();
		
		ArrayList<Double> squareVolatilities = SDESystemForStocks.getDriftsOfStocks();
		
		ArrayList<ArrayList<Double>> covolatilityMatrixSqrt = SDESystemForStocks.getCovolatilityMatrixSqrt();
		
		//Simulates a step of Brownian motion across the stocks passing the number of stocks in Sj as a parameter
		ArrayList<Double> nDimensionalGaussianSample = generateNDimensionalGaussionSample(Sj.size());
		
		Sjplus1 = vectorAdd(vectorAdd(Sj, scaleVector(hadamardProduct(vectorAdd(drifts, scaleVector(squareVolatilities, 0.5)), Sj), 1.0/(250 * upSampleFactor))), 
				hadamardProduct(scaleVector(Sj, 1.0 / Math.sqrt((250 * upSampleFactor))), matrixVectorProduct(covolatilityMatrixSqrt, nDimensionalGaussianSample)));
		
		return Sjplus1;
	}
	
	/**
	 * Simulates a step of Brownian motion using functionality built into Java.
	 * 
	 * @param dimensionality-should match the number of stocks in the original data
	 * @return
	 */
	private ArrayList<Double> generateNDimensionalGaussionSample(int dimensionality) 
	{
		ArrayList<Double> nDimensionalGaussianSample = new ArrayList<Double>();
		
		for (int i = 0; i < dimensionality; i++)
		{
			nDimensionalGaussianSample.add(this.gaussianSampler.nextGaussian());
		}
		
		return nDimensionalGaussianSample;
	}

	/**
	 * Adds vectors elementwise
	 * 
	 * @param leftVector
	 * @param rightVector
	 * @return-Vector representing the sum of vectors
	 */
	private ArrayList<Double> vectorAdd(ArrayList<Double> leftVector, ArrayList<Double> rightVector) 
	{
		ArrayList<Double> sum = new ArrayList<Double>();
		
		for(int i = 0; i < leftVector.size(); i++)
		{
			sum.add(leftVector.get(i) + rightVector.get(i));
		}
		
		return sum;
	}

	/**
	 * Scales vectors elementwise
	 * 
	 * @param vector
	 * @param scaleFactor
	 * @return-Vector representing the scaled vector
	 */
	private ArrayList<Double> scaleVector(ArrayList<Double> vector, Double scaleFactor) 
	{
		ArrayList<Double> scaled = new ArrayList<Double>();
		
		for(int i = 0; i < vector.size(); i++)
		{
			scaled.add(vector.get(i) * scaleFactor);
		}
		
		return scaled;
	}
	
	/**
	 * Scales each element of a matrix by a factor
	 * 
	 * @param vector
	 * @param scaleFactor
	 * @return-scaled matrix
	 */
	private ArrayList<ArrayList<Double>> scaleMatrix(ArrayList<ArrayList<Double>> vector, Double scaleFactor) 
	{
		ArrayList<ArrayList<Double>> scaled = new ArrayList<ArrayList<Double>>();
		
		for(int j = 0; j < vector.size(); j++)
		{
			scaled.add(new ArrayList<Double>());
			for(int i = 0; i < vector.get(j).size(); i++)
			{
				scaled.get(j).add(vector.get(j).get(i) * scaleFactor);
			}
		}
		
		return scaled;
	}

	/**
	 * Computes the Hadamard product of two vectors
	 * 
	 * @param leftVector
	 * @param rightVector
	 * @return
	 */
	public ArrayList<Double> hadamardProduct(ArrayList<Double> leftVector, ArrayList<Double> rightVector)
	{
		ArrayList<Double> hadamardProduct = new ArrayList<Double>();
		
		for(int i = 0; i < leftVector.size(); i++)
		{
			hadamardProduct.add(leftVector.get(i) * rightVector.get(i));
		}
		
		return hadamardProduct;
	}
	
	/**
	 * Computes the matrix product using Jama after converting the ArrayLists to matrices
	 * 
	 * @param m1-Matrix one
	 * @param m2-Matrix two
	 * @return-The product converted back to an ArrayList
	 */
	private ArrayList<ArrayList<Double>> matrixProduct(ArrayList<ArrayList<Double>> m1,
			ArrayList<ArrayList<Double>> m2) 
	{
		double[][] basicM1 = SDESystem.convertToBasicArrayMatrix(m1);
		double[][] basicM2 = SDESystem.convertToBasicArrayMatrix(m2);
		
		//Matrix operations use the Jama library
		Matrix matrixM1 = new Matrix(basicM1);
		Matrix matrixM2 = new Matrix(basicM2);
		
		Matrix product = matrixM1.times(matrixM2);
		
		ArrayList<ArrayList<Double>> productAsArrayList = SDESystem.convertToArrayListMatrix(product);
		
		return productAsArrayList;
	}
	
	/**
	 * Takes the vector product of two matrices
	 * 
	 * @param m1
	 * @param m2
	 * @return-
	 */
	private ArrayList<Double> matrixVectorProduct(ArrayList<ArrayList<Double>> m1,
	ArrayList<Double> m2)
	{
		double[][] basicM1 = SDESystem.convertToBasicArrayMatrix(m1);
		double[][] basicM2 = new double[][] {SDESystem.convertToBasicArray(m2)};
		
		Matrix matrixM1 = new Matrix(basicM1);
		Matrix matrixM2 = new Matrix(basicM2);
		
		Matrix product = matrixM2.times(matrixM1);
		
		ArrayList<Double> productAsArrayList = SDESystem.convertToArrayList(product);
		
		return productAsArrayList;
	}

	/**
	 * Getter method for daily price list
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Double>> getDailyPrices() 
	{
		return dailyPrices;
	}
}
