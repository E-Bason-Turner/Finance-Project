package financeproject2;

import java.util.ArrayList;


import Jama.EigenvalueDecomposition;
import Jama.Matrix;
/**
 * The class contains the code for setting up the stock simulations. It uses the Jama library in order to run the matrix
 * operations needed in the SDE system. 
 *
 * @author Zach Archibald and Evan Turner
 * @version 2023-12-12
 */
public class SDESystem 
{
	ArrayList<Double> driftsOfStocks;
	ArrayList<Double> volatilitiesOfStocks;
	ArrayList<ArrayList<Double>> covolatilityMatrix;
	ArrayList<ArrayList<Double>> covolatilityMatrixSqrt;
	
	/**
	 * Uses stock statistics in order to set up the SDE system
	 * 
	 * @param driftsAndVolatilities
	 * @param covolatilityMatrix
	 */
	public SDESystem(ArrayList<ArrayList<Double>> driftsAndVolatilities, ArrayList<ArrayList<Double>> covolatilityMatrix) 
	{
		this.driftsOfStocks = new ArrayList<Double>();
		this.volatilitiesOfStocks = new ArrayList<Double>();
		for (int i = 0; i < driftsAndVolatilities.size(); i++)
		{
			this.getDriftsOfStocks().add(driftsAndVolatilities.get(i).get(0));
			this.getVolatilitiesOfStocks().add(driftsAndVolatilities.get(i).get(1));
		}
		
		this.covolatilityMatrix = covolatilityMatrix;
		
		this.covolatilityMatrixSqrt = MatrixSquareRoot(covolatilityMatrix);
	}
	
	/**
	 * Computes the square root of a matrix. This is found through EigenValue decomposition
	 * 
	 * @param ArrayListMatrix
	 * @return-matrix square root
	 */
	public static ArrayList<ArrayList<Double>> MatrixSquareRoot(ArrayList<ArrayList<Double>> ArrayListMatrix)
	{
		double[][] basicArrayMatrix = convertToBasicArrayMatrix(ArrayListMatrix);
		
		Matrix matrix = new Matrix(basicArrayMatrix);
		
		EigenvalueDecomposition evd = new EigenvalueDecomposition(matrix);
		
		Matrix V = evd.getV();
		Matrix D = evd.getD();
		Matrix VT = V.transpose();
		
		Matrix sqrtD = evd.getD();
		for (int i = 0; i < D.getRowDimension(); i++)
		{
			double Di = D.get(i, i);
			sqrtD.set(i, i, Math.sqrt(Di));
		}
		
		Matrix basicArrayMatrixSquareRoot = V.times(sqrtD).times(VT);
		
		ArrayList<ArrayList<Double>> matrixSquareRoot = convertToArrayListMatrix(basicArrayMatrixSquareRoot);
		
		return matrixSquareRoot;
	}

	/**
	 * Converts an ArrayList to a matrix
	 * 
	 * @param arrayListMatrix
	 * @return-A matrix 
	 */
	public static double[][] convertToBasicArrayMatrix(ArrayList<ArrayList<Double>> arrayListMatrix) 
	{
		double[][] basicArrayMatrix = new double[arrayListMatrix.size()][arrayListMatrix.get(0).size()];
		
		for(int row = 0; row < arrayListMatrix.size(); row++)
		{
			for(int collumn = 0; collumn < arrayListMatrix.get(0).size(); collumn++)
			{
				basicArrayMatrix[row][collumn] = arrayListMatrix.get(row).get(collumn);
			}
		}
		
		return basicArrayMatrix;
	}
	
	/**
	 * Converts an ArrayList to a basic array
	 * 
	 * @param arrayList
	 * @return
	 */
	public static double[] convertToBasicArray(ArrayList<Double> arrayList) 
	{
		double[] basicArray = new double[arrayList.size()];
		
		for(int i = 0; i < arrayList.size(); i++)
		{
			basicArray[i] = arrayList.get(i);
		}
		
		return basicArray;
	}
	
	/**
	 * Converts a matrix into an ArrayList of Matrices
	 * 
	 * @param basicArrayMatrix
	 * @return-The ArrayList of Matrices
	 */
	public static ArrayList<ArrayList<Double>> convertToArrayListMatrix(Matrix basicArrayMatrix) 
	{
		ArrayList<ArrayList<Double>> arrayListMatrix = new ArrayList<ArrayList<Double>>();
		
		for(int row = 0; row < basicArrayMatrix.getRowDimension(); row++)
		{
			arrayListMatrix.add(new ArrayList<Double>());
			for(int column = 0; column < basicArrayMatrix.getColumnDimension(); column++)
			{
				arrayListMatrix.get(row).add(basicArrayMatrix.get(row, column));
			}
		}
		
		return arrayListMatrix;
	}
	
	/**
	 * Converts a matrix to an ArrayList
	 * 
	 * @param basicArrayMatrix
	 * @return-The ArrayList
	 */
	public static ArrayList<Double> convertToArrayList(Matrix basicArrayMatrix) 
	{
		ArrayList<Double> arrayList = new ArrayList<Double>();
		
		for(int i = 0; i < basicArrayMatrix.getColumnDimension(); i++)
		{
			arrayList.add(basicArrayMatrix.get(0,i));
		}
		
		return arrayList;
		
	}

	/**
	 * Getter for drifts
	 * 
	 * @return
	 */
	public ArrayList<Double> getDriftsOfStocks() 
	{
		return driftsOfStocks;
	}

	/**
	 * Getter for volatilities
	 * 
	 * @return
	 */
	public ArrayList<Double> getVolatilitiesOfStocks() 
	{
		return volatilitiesOfStocks;
	}

	/**
	 * Getter for the covolatilities
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Double>> getCovolatilityMatrixSqrt() 
	{
		return covolatilityMatrixSqrt;
	}
	
//	/**
//	 * Prints all of the statistics to the console
//	 * 
//	 */
//	public void printSDEComponents() 
//	{
//		System.out.println("Drifts:");
//		for (int i = 0; i < driftsOfStocks.size(); i++) 
//		{
//			System.out.println(driftsOfStocks.get(i));
//		}
//		
//		System.out.println("Square Volatilities:");
//		for (int i = 0; i < volatilitiesOfStocks.size(); i++) 
//		{
//			System.out.println(volatilitiesOfStocks.get(i));
//		}
//		
//		System.out.println("Covolatility Matrix Square Root:");
//		for (int i = 0; i < covolatilityMatrixSqrt.size(); i++) 
//		{
//			System.out.println(covolatilityMatrixSqrt.get(i));
//		}
//	}
}
