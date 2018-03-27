/*
 * Autorius: Tomas Maconko
 * Kursas: 3, grupe: 2
 * Uzduotis: Ilgiausios Collatz'o iteracijos paieska duotajame skaiciu intervale
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

#define false 0
#define true !false


int collatz(long);

int main(int argc, char *argv[])
{

	if (argc != 4)
	{
		printf("Wrong parameters number. Should be 4 parameters.\r\n");
		printf("[ProgramName] [M] [N] [1=Output or 2=NoOutput]\r\n");
		return 0;
	}

	long m = atol(argv[1]);
	long n = atol(argv[2]);

	int outputFlag = atoi(argv[3]);

	long maxIterations = 0;
	long iterations;

	double startTime = (double) clock();

	long i = m;
	for (; i <= n; i++)
	{
		iterations = collatz(i);
		if (iterations > maxIterations)
			maxIterations = iterations;
		if (outputFlag != false)
		    printf("%ld\r\n", iterations);
   	 }

	double endTime = (double) clock();

	printf("Max length: %ld Time: %.4f\r\n", maxIterations, (endTime - startTime) / CLOCKS_PER_SEC);
	return 0;
}

int collatz(long n)
{
	int iterations = 1;
	while (n > 1)
	{
		n = (n % 2 == 0) ? (n / 2) : (3 * n + 1);
		iterations++;
	}
	return iterations;
}
