/*
 * Autorius: Tomas Maconko
 * Kursas: 3, grupe: 2
 * Uzduotis: Ilgiausios Collatz'o iteracijos paieska duotajame skaiciu intervale
 */

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define false 0
#define true !false

#define MASTER 0
#define master() (rank == MASTER)

long collatz(long);

int main(int argc, char** argv)
{
    
    if (argc != 5)
    {
        printf("Wrong command line arguments.\r\n");
        printf("Template: <Program> <M> <N> <ChunkSize> <OutputFlag>\r\n");
        return 0;
    }
    
    long m = atol(argv[1]), n = atol(argv[2]) + 1;
    long chunkSize = atoi(argv[3]);
    int outputFlag = atoi(argv[4]);

    int rank = 0, np;
    int busy = 0;
    
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &np);
    MPI_Status status;

    if (master())
    {
	printf("Total number of procs: %d\r\n", np);
	printf("Data: %ld .. %ld\r\n", m, n - 1);
	printf("Chunk: %ld\r\n", chunkSize);
    }

    long maxIterations = 0;

    double start = MPI_Wtime();
    if (master())
    {
        long iterations;
        long i = m, p;
        for (p = 1; p < np && i < n; i += chunkSize, p += 1)
        {
            MPI_Send(&i, 1, MPI_LONG, p, 1, MPI_COMM_WORLD);
            busy++;
        }
        while (i < n)
        {
            MPI_Recv(&iterations, 1, MPI_LONG, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
            if (iterations > maxIterations)
            {
                maxIterations = iterations;
            }
            if (outputFlag != false)
                printf("%ld\r\n", iterations);
            MPI_Send(&i, 1, MPI_LONG, status.MPI_SOURCE, 1, MPI_COMM_WORLD);
            i += chunkSize;
        }
        for (p = 1; p <= busy; p++)
        {
            MPI_Recv(&iterations, 1, MPI_LONG, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
            if (iterations > maxIterations)
            {
                maxIterations = iterations;
            }
            if (outputFlag != false)
                printf("%ld\r\n", iterations);
        }
        long endOfWork = -1;
        for (p = 1; p < np; p++)
        {
            MPI_Send(&endOfWork, 1, MPI_LONG, p, 1, MPI_COMM_WORLD);
        }
    }
    else
    {
        while (true)
        {
            long start_i, end_i;
            MPI_Recv(&start_i, 1, MPI_LONG, MASTER, 1, MPI_COMM_WORLD, &status);
            if (start_i < 0)
                break;
            end_i = start_i + chunkSize;
            long job, longestJob = 0;
            long longestIt = 0, tempIt;
            for (job = start_i; job < end_i; job++)
            {
                tempIt = collatz(job);
                if (tempIt > longestIt)
                {
                    longestIt = tempIt;
                    longestJob = job;
                }
            }
            MPI_Send(&longestIt, 1, MPI_LONG, MASTER, 1, MPI_COMM_WORLD);
        }
    }
    double end = MPI_Wtime();

    if (master())
    {
        printf("Max length: %ld time: %.4f\r\n", maxIterations, (end - start));
    }
    MPI_Finalize();
    return 0;
}

long collatz(long n)
{
    long iterations = 1;
    while (n > 1) 
    {
        n = n % 2 == 0 ? n / 2 : 3 * n + 1;
        iterations++;
    }
    return iterations;
}

