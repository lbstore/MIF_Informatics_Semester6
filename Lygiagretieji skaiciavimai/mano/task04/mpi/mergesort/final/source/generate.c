#include <stdio.h>
#include <stdlib.h>
int main(int argc, char** argv) {
  FILE *f = fopen("run","w");
  int procCount = atoi(argv[1]);
  
  fprintf(f,"#!/bin/sh \n");
  fprintf(f,"#SBATCH -p short\n");
  fprintf(f,"#SBATCH -n%d\n",procCount);
  char* cluster = "alpha";
  if(argc == 3){
    cluster = argv[2];
  }
  fprintf(f,"#SBATCH -C %s\n",cluster);
  fprintf(f,"make run np=%d\n",procCount);
  fclose(f);
  
}
