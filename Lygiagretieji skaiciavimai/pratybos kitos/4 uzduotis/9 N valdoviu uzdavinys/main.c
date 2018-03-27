#include <stdio.h>
#include <time.h>
#include <mpi.h>

int work(int stulpelis, int N, int vietos[]) {
    int kiekRado = 0;
    int i;
    if (stulpelis<=N)
    for (i=1; i<=N; i++) { //einam pro eilutes
        if (isSafe(i, stulpelis, vietos, sizeof(vietos)/sizeof(int)) == 1) {
            if (stulpelis == N) {
                kiekRado++;
            }
            else {
                vietos[stulpelis] = i;
                kiekRado+=work(stulpelis+1, N, vietos);
                vietos[stulpelis] = 0;
            }
        }
            
    }
    return kiekRado;
}

int isSafe(int eilute, int stulpelis, int vietos[], int dydis) {

    int i;
    /*
    for(i=0; i<dydis; i++) {
        printf("%d ", vietos[i]);
    }
    */
    
    for (i=stulpelis-1; i>0; i--  ) {
        if (eilute == vietos[i])
            return 0;
        if (vietos[i]-eilute == stulpelis-i)
            return 0;
        if (eilute-vietos[i] == stulpelis-i)
            return 0;
    }
    return 1;
}

void master(int n, int visoProc) {
    int stime;
    int ftime;
    int i;
    int rezultatu = 0;
    int sk;
    int vietos[n+1];
    MPI_Status statusas;
    //time(&stime);
    stime = clock();
    //printf("Rasta rezultatu: %d\n", work(1, n, vietos));
    for (i=1; i<=n ; i++) { //siunciam darbus
        MPI_Recv(&sk, 1, MPI_INT, MPI_ANY_SOURCE, 201, MPI_COMM_WORLD, &statusas);
        rezultatu += sk;
        MPI_Send(&i, 1, MPI_INT, statusas.MPI_SOURCE, 201, MPI_COMM_WORLD);
    }
    int minusas = -1;
    for (i=1; i<visoProc; i++) { //siunciam darbo pabaigus flag'us kitiems procesoriams
        MPI_Recv(&sk, 1, MPI_INT, MPI_ANY_SOURCE, 201, MPI_COMM_WORLD, &statusas);
        rezultatu += sk;
        MPI_Send(&minusas, 1, MPI_INT, statusas.MPI_SOURCE, 201, MPI_COMM_WORLD);
    }
    printf("Rasta rezultatu: %d\n", rezultatu);
    //time(&ftime);
    ftime = clock();
    printf("Darbo laikas: %f\n ms.", ((float)ftime/CLOCKS_PER_SEC));
}

void slave(int n) {
    int rez = 0 ;
    int kurDedam=0;
    int vietos[n+1];
    MPI_Status statusas;

    while (kurDedam>=0) {
        MPI_Send(&rez, 1, MPI_INT, 0, 201, MPI_COMM_WORLD);
        rez = 0;
        MPI_Recv(&kurDedam, 1, MPI_INT, 0, 201, MPI_COMM_WORLD, &statusas);
        if (kurDedam > 0) {
            vietos[1] = kurDedam;
            rez = work(2, n, vietos);
        }
    }
}

int main(int argc, char * argv[])
{
    int n = atoi(argv[1]); //ignoruojam ispejima
    int vietos[n+1];
    int kuris;
    int visoProc;

   // work(1, n, vietos); 
   // int ftime = clock();
   // printf("Darbo laikas: %f\n", ((float)ftime / CLOCKS_PER_SEC));
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &kuris);
    MPI_Comm_size(MPI_COMM_WORLD, &visoProc);
    if (visoProc > 0) {
        if (kuris == 0) {master(n, visoProc); printf("Procesoriu: %d\n================\n", visoProc);}
        if (kuris != 0) {slave(n); }
    }
    //printf("Procesoriu kiekis: %d\n", visoProc);
//    vietos[1] = 1;
//    work(2, n, vietos);
    MPI_Finalize();
    return 0;
}

