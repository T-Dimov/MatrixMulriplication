package matrixMultiplication;

public class Main
{
	
	public static void main(String[] args)
	{
		long startTime = System.nanoTime();
		
		Parser parser = new Parser(args);
		Matrix first;
		Matrix second;
		if (parser.inputFile == null)
		{
			first = new Matrix(parser.m, parser.n, parser.threads, parser.byCell, parser.threadLocalRandom);
			second = new Matrix(parser.n, parser.k, parser.threads, parser.byCell, parser.threadLocalRandom);
		}
		else
		{
			Matrix[] matrices = parser.loadMatrices();
			first = matrices[0];
			second = matrices[1];
		}
		
		Multiplier multiplier = new Multiplier(first, second, parser.threads, parser.quiet, parser.byCell);
		Matrix result = multiplier.multiply();
		if (parser.outputFile != null)
		{
			result.save(parser.outputFile);
		}
		
		if (!parser.quiet)
		{
			System.out.println("Threads used in current run:" + parser.threads);
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1_000_000;
		System.out.println("Total execution time for current run (millis): " + duration);
	}
}
