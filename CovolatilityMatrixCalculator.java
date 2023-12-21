package financeproject2;

import java.util.ArrayList;

public class CovolatilityMatrixCalculator 
{

	public static ArrayList<ArrayList<Double>> generateCovolatilityMatrix(ArrayList<ArrayList<Double>> dailyPricesOfStocks, 
		ArrayList<ArrayList<Double>> driftsAndVolatilities, ArrayList<ArrayList<Double>> logReturnsOfStocks)	
	{
		ArrayList<ArrayList<Double>> covolatilityMatrix = constructEmptyCovolatilityMatrix(dailyPricesOfStocks.size());
		
		populateCovolatilityMatrixDiagonal(covolatilityMatrix, driftsAndVolatilities);
		
		populateCovolatilityMatrixNonDiagonal(covolatilityMatrix, driftsAndVolatilities, logReturnsOfStocks);
		
		return covolatilityMatrix;
	}

		private static ArrayList<ArrayList<Double>> constructEmptyCovolatilityMatrix(int size) 
	{
		ArrayList<ArrayList<Double>> covolatilityMatrix = new ArrayList<ArrayList<Double>>();
		
		for(int i = 0; i < size; i++)
		{
			ArrayList<Double> rowOfMatrix = new ArrayList<Double>();
			
			for(int j = 0; j < size; j++)
			{
				rowOfMatrix.add(0.0);
			}
			
			covolatilityMatrix.add(rowOfMatrix);
		}
	
		return covolatilityMatrix;
	}

	private static void populateCovolatilityMatrixDiagonal(ArrayList<ArrayList<Double>> covolatilityMatrix,
			ArrayList<ArrayList<Double>> driftsAndVolatilities) 
	{
		for(int row = 0; row < covolatilityMatrix.size(); row++)
		{
			Double volatilityOfRowthStock = driftsAndVolatilities.get(row).get(1);
			
			covolatilityMatrix.get(row).set(row, volatilityOfRowthStock);
		}
	}
	
	private static void populateCovolatilityMatrixNonDiagonal(ArrayList<ArrayList<Double>> covolatilityMatrix,
			ArrayList<ArrayList<Double>> driftsAndVolatilities, ArrayList<ArrayList<Double>> logReturnsOfStocks) 
	{
		for(int row = 0; row < covolatilityMatrix.size(); row++)
		{
			for(int collumn = row + 1; collumn < covolatilityMatrix.size(); collumn++)
			{
				Double currentCovolatility = computeCovolatility(row, collumn, driftsAndVolatilities, logReturnsOfStocks);
				
				covolatilityMatrix.get(row).set(collumn, currentCovolatility);
				covolatilityMatrix.get(collumn).set(row, currentCovolatility);
			}
		}
	}

	private static Double computeCovolatility(int row, int collumn, 
			ArrayList<ArrayList<Double>> driftsAndVolatilities, ArrayList<ArrayList<Double>> logReturnsOfStocks) 
	{
		Double rowDriftHn = driftsAndVolatilities.get(row).get(0) / logReturnsOfStocks.get(0).size();
		Double collumnDriftHn = driftsAndVolatilities.get(collumn).get(0) / logReturnsOfStocks.get(0).size();
		
		ArrayList<Double> productsOfDeviations = 
				computeProductsOfDeviations(logReturnsOfStocks, row, collumn, rowDriftHn, collumnDriftHn);
		
		Double covolatility = GatherDriftsAndVolatilities.sum(productsOfDeviations);

		return covolatility;
	}

	private static ArrayList<Double> computeProductsOfDeviations(ArrayList<ArrayList<Double>> logReturnsOfStocks,
			int row, int collumn, Double rowDrift, Double collumnDrift) 
	{
		ArrayList<Double> logReturnsOfRowStock = logReturnsOfStocks.get(row);
		ArrayList<Double> logReturnsOfCollumnStock = logReturnsOfStocks.get(collumn);
		
		ArrayList<Double> productsOfDeviations = new ArrayList<Double>();
		
		for(int i = 0; i < logReturnsOfRowStock.size(); i++)
		{
			Double productOfDeviations = (logReturnsOfRowStock.get(i) - rowDrift) * 
					(logReturnsOfCollumnStock.get(i) - collumnDrift);
			
			productsOfDeviations.add(productOfDeviations);
		}
		return productsOfDeviations;
	}

	

	
}
