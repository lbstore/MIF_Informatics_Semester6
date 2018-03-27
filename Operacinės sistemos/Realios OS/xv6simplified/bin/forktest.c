// Test that fork fails gracefully.
// Tiny executable so that the limit can be filling the proc table.

#include "types.h"
#include "stat.h"
#include "user.h"

#define N  10

int delay(){
  int max = 2000;
  int i,j;
  for(i=0;i<max;i++){
    for(j=0;j<max; j++){
      ;
    }
  }
  return 0;
  
}

void forktest(int n);

void forktest(int n){
  int pid;
  int i = 0;
  printf(1,"fork test\n");
//  printf(1," n = %d",n);
//
  for(; i<n; i++){
    pid = fork();
//    delay();
    if(pid < 0)
      break;
    if(pid == 0)
      exit();
  }
//  printf(1," n = %d",n);
  if(i == n){
    printf(1,"fork claimed to work %d times!\n", n);
    exit();
  }
  
  for(; i > 0; i--){
    if(wait() < 0){
      printf(1, "wait stopped early\n");
      exit();
    }
  }
  
  
  if(wait() != -1){
    printf(1,"wait got too many\n");
    exit();
  }
  
  printf(1,"fork test OK\n");
}

int main(int argc, char *argv[]){
  int size = N;
  if(argc > 1)
    size = atoi(argv[1]);
  forktest(size);
  exit();
}


