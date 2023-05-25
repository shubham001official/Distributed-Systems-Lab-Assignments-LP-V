#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

int main(int argc, char **argv)
{
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int N = 100; // Number of elements in the array
    int *array = NULL;
    int localN;

    if (rank == 0)
    {
        // Generate the array on rank 0
        array = (int *)malloc(N * sizeof(int));
        for (int i = 0; i < N; i++)
        {
            array[i] = i + 1;
        }
    }

    MPI_Bcast(&N, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (N % size == 0)
    {
        localN = N / size; // Number of elements per processor
    }
    else
    {
        if (rank == 0)
        {
            printf("Error: The number of processors is not evenly divisible by the number of elements.\n");
        }
        MPI_Finalize();
        return 0;
    }

    int *localArray = (int *)malloc(localN * sizeof(int));
    MPI_Scatter(array, localN, MPI_INT, localArray, localN, MPI_INT, 0, MPI_COMM_WORLD);

    int partialSum = 0;
    for (int i = 0; i < localN; i++)
    {
        partialSum += localArray[i];
    }

    int *partialSums = NULL;
    if (rank == 0)
    {
        partialSums = (int *)malloc(size * sizeof(int));
    }
    MPI_Gather(&partialSum, 1, MPI_INT, partialSums, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank == 0)
    {
        int totalSum = 0;
        printf("Intermediate sums calculated at different processors:\n");
        for (int i = 0; i < size; i++)
        {
            printf("Processor %d: %d\n", i, partialSums[i]);
            totalSum += partialSums[i];
        }
        printf("Total Sum: %d\n", totalSum);
    }

    MPI_Finalize();
    if (rank == 0)
    {
        free(array);
        free(partialSums);
    }
    free(localArray);

    return 0;
}
