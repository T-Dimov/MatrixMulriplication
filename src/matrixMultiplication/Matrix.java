package matrixMultiplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

class Matrix
{
	int						firstDim;
	int						secondDim;
	float[][]				matrix;
	private CountDownLatch	latch;
	private int				step;
	private int				rem;
	
	Matrix(int first, int second, int threads, boolean byCell, boolean threadLocal)
	{
		firstDim = first;
		secondDim = second;
		matrix = new float[first][second];
		
		if (threads > firstDim)
		{
			threads = firstDim;
		}
		step = firstDim / threads;
		rem = firstDim % threads;
		
		if (threads > 1)
		{
			ExecutorService pool = Executors.newFixedThreadPool(threads);
			if (byCell)
			{
				latch = new CountDownLatch(firstDim * secondDim);
				for (int i = 0; i < firstDim; i++)
				{
					for (int j = 0; j < secondDim; j++)
					{
						pool.execute(new InitializerThread(i, j, threadLocal));
					}
				}
			}
			else
			{
				latch = new CountDownLatch(threads);
				int rows = step;
				for (int row = 0; row < firstDim; row += rows)
				{
					rows = step + (rem > 0 ? 1 : 0);
					rem--;
					pool.execute(new InitializerThread(row, threadLocal, rows));
				}
			}
			pool.shutdown();
			try
			{
				latch.await();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Random rand = new Random();
			for (int i = 0; i < firstDim; i++)
			{
				for (int j = 0; j < secondDim; j++)
				{
					matrix[i][j] = rand.nextFloat();
				}
			}
		}
	}
	
	Matrix(int first, int second)
	{
		firstDim = first;
		secondDim = second;
		matrix = new float[first][second];
	}
	
	void save(String fileName)
	{
		try
		{
			PrintWriter writer = new PrintWriter(fileName);
			writer.println(firstDim + " " + secondDim);
			for (int i = 0; i < firstDim; i++)
			{
				String line = "";
				for (int j = 0; j < secondDim; j++)
				{
					line += matrix[i][j] + " ";
				}
				writer.println(line.substring(0, line.length()));
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	class InitializerThread implements Runnable
	{
		private int		row;
		private int		column;
		private boolean	threadLocalRandom;
		private int		rows;
		
		public InitializerThread(int i_, int j_, boolean threadLocal)
		{
			row = i_;
			column = j_;
			threadLocalRandom = threadLocal;
			rows = 0;
		}
		
		public InitializerThread(int i_, boolean threadLocal, int rows_)
		{
			row = i_;
			column = 0;
			threadLocalRandom = threadLocal;
			rows = rows_;
		}
		
		@Override
		public void run()
		{
			if (threadLocalRandom)
			{
				if (column > 0)
				{
					matrix[row][column] = ThreadLocalRandom.current().nextFloat();
				}
				else
				{
					for (int i = row; i < row + rows; i++)
					{
						for (int j = 0; j < secondDim; j++)
						{
							matrix[i][j] = ThreadLocalRandom.current().nextFloat();
						}
					}
				}
			}
			else
			{
				Random rand = new Random();
				if (column > 0)
				{
					matrix[row][column] = rand.nextFloat();
				}
				else
				{
					for (int i = row; i < row + rows; i++)
					{
						for (int j = 0; j < secondDim; j++)
						{
							matrix[i][j] = rand.nextFloat();
						}
					}
				}
			}
			latch.countDown();
		}
	}
}
