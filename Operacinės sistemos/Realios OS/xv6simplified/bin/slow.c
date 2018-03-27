#include "types.h"
#include "stat.h"
#include "user.h"
int delay(int max){
  int i,j;
  for(i=0;i<max;i++){
    for(j=0;j<max; j++){
      ;
    }
  }
  return 0;
  
}
int main(int argc, char *argv[]){
  printf(1,"Start\n");
  
//  argv[1] = "2";
  char* param[] ={"skip",argv[1]};
  int pid = fork();
  if(pid == 0){//in child
    exec("forktest",param);
  }else{
    sleep(5);
    listpid();
    sleep(5);
    listpid();
    wait();
    
    printf(1,"End\n");
  }
  
  
  exit();
}