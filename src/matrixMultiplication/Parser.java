package matrixMultiplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class Parser
{
	int		m;
	int		n;
	int		k;
	String	inputFile;
	String	outputFile;
	boolean	quiet;
	int		threads;
	boolean	threadLocalRandom;
	boolean	byCell;
	
	public Parser(String[] arguments)
	{
		inputFile = null;
		outputFile = null;
		quiet = false;
		threads = 1;
		threadLocalRandom = false;
		for (int i = 0; i < arguments.length; i++)
		{
			if (arguments[i].equals("-m"))
			{
				i++;
				m = Integer.parseInt(arguments[i]);
				continue;
			}
			if (arguments[i].equals("-n"))
			{
				i++;
				n = Integer.parseInt(arguments[i]);
				continue;
			}
			if (arguments[i].equals("-k"))
			{
				i++;
				k = Integer.parseInt(arguments[i]);
				continue;
			}
			if (arguments[i].equals("-i"))
			{
				i++;
				inputFile = arguments[i];
				continue;
			}
			if (arguments[i].equals("-o"))
			{
				i++;
				outputFile = arguments[i];
				continue;
			}
			if (arguments[i].equals("-q"))
			{
				quiet = true;
				continue;
			}
			if (arguments[i].equals("-t"))
			{
				i++;
				threads = Integer.parseInt(arguments[i]);
				continue;
			}
			if (arguments[i].equals("-l"))
			{
				threadLocalRandom = true;
				continue;
			}
			if (arguments[i].equals("-c"))
			{
				byCell = true;
				continue;
			}
		}
		// System.out.println(m);
		// System.out.println(n);
		// System.out.println(k);
		// System.out.println(inputFile);
		// System.out.println(outputFile);
		// System.out.println(quiet);
		// System.out.println(threads);
		// System.out.println(threadLocalRandom);
		// System.out.println(byCell);
	}
	
	Matrix[] loadMatrices()
	{
		Matrix[] matrices = new Matrix[2];
		
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile)))
		{
			String line = br.readLine();
			String[] data = line.split(" ");
			m = Integer.parseInt(data[0]);
			n = Integer.parseInt(data[1]);
			k = Integer.parseInt(data[2]);
			
			matrices[0] = new Matrix(m, n);
			for (int i = 0; i < m; i++)
			{
				line = br.readLine();
				data = line.split(" ");
				for (int j = 0; j < n; j++)
				{
					matrices[0].matrix[i][j] = Integer.parseInt(data[j]);
				}
			}
			
			matrices[1] = new Matrix(n, k);
			for (int i = 0; i < n; i++)
			{
				line = br.readLine();
				data = line.split(" ");
				for (int j = 0; j < k; j++)
				{
					matrices[1].matrix[i][j] = Integer.parseInt(data[j]);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return matrices;
	}
}
