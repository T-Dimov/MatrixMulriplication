package matrixMultiplication;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Multiplier
{
	private int				threads;
	private int				step;
	private int				rem;
	private boolean			quiet;
	ExecutorService			pool;
	private boolean			byCell;
	private Matrix			first;
	private Matrix			second;
	private Matrix			result;
	private CountDownLatch	latch;
	
	public Multiplier(Matrix left, Matrix right, int thr, boolean q, boolean cellular)
	{
		first = left;
		second = right;
		result = null;
		
		threads = thr;
		if (threads > first.firstDim)
		{
			threads = first.firstDim;
		}
		step = first.firstDim / threads;
		rem = first.firstDim % threads;
		quiet = q;
		byCell = cellular;
		
		if (threads > 1)
		{
			pool = Executors.newFixedThreadPool(threads);
		}
	}
	
	Matrix multiply()
	{
		result = new Matrix(first.firstDim, second.secondDim);
		
		if (pool != null)
		{
			if (byCell)
			{
				latch = new CountDownLatch(first.firstDim * second.secondDim);
				for (int i = 0; i < first.firstDim; i++)
				{
					for (int j = 0; j < second.secondDim; j++)
					{
						pool.execute(new MultiplierThread(i, j));
					}
				}
			}
			else
			{
				latch = new CountDownLatch(threads);
				int rows = step;
				for (int row = 0; row < first.firstDim; row += rows)
				{
					rows = step + (rem > 0 ? 1 : 0);
					rem--;
					pool.execute(new MultiplierThread(row, rows, true));
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
			if (!quiet)
			{
				System.out.println("Thread-" + 1 + " started.");
			}
			
			for (int i = 0; i < first.firstDim; i++)
			{
				for (int j = 0; j < second.secondDim; j++)
				{
					for (int k = 0; k < first.secondDim; k++)
					{
						result.matrix[i][j] += first.matrix[i][k] * second.matrix[k][j];
					}
				}
			}
			
			if (!quiet)
			{
				System.out.println("Thread-" + 1 + " stopped.");
				
				long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
				
				System.out.println("Thread-" + 1 + "execution time was (millis): " + nanos / 1_000_000);
			}
		}
		
		return result;
	}
	
	class MultiplierThread implements Runnable
	{
		private int		row;
		private int		column;
		private int		rows;
		private long	last;
		
		public MultiplierThread(int i_, int j_)
		{
			row = i_;
			column = j_;
			rows = 1;
			last = 0;
		}
		
		public MultiplierThread(int i_, int rows_, boolean improved)
		{
			row = i_;
			column = 0;
			rows = rows_;
			last = 0;
		}
		
		@Override
		public void run()
		{
			long threadId = Thread.currentThread().getId() % threads + 1;
			
			if (!quiet)
			{
				System.out.println("Thread-" + threadId + " started.");
			}
			
			if (column > 0)
			{
				result.matrix[row][column] = 0;
				for (int k = 0; k < first.secondDim; k++)
				{
					result.matrix[row][column] += first.matrix[row][k] * second.matrix[k][column];
				}
			}
			else
			{
				for (int i = row; i < row + rows; i++)
				{
					for (int j = 0; j < second.secondDim; j++)
					{
						result.matrix[i][j] = 0;
						for (int k = 0; k < first.secondDim; k++)
						{
							result.matrix[i][j] += first.matrix[i][k] * second.matrix[k][j];
						}
					}
				}
			}
			
			if (!quiet)
			{
				System.out.println("Thread-" + threadId + " stopped.");
				
				long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
				last = nanos - last;
				
				System.out.println("Thread-" + threadId + "execution time was (millis): " + last / 1_000_000);
			}
			
			latch.countDown();
		}
	}
}
