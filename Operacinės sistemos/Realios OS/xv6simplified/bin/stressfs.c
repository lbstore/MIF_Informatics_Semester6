// Demonstrate that moving the "acquire" in iderw after the loop that
// appends to the idequeue results in a race.

// For this to work, you should also add a spin within iderw's
// idequeue traversal loop.  Adding the following demonstrated a panic
// after about 5 runs of stressfs in QEMU on a 2.1GHz CPU:
//    for (i = 0; i < 40000; i++)
//      asm volatile("");

#include "types.h"
#include "stat.h"
#include "user.h"
#include "fs.h"
#include "fcntl.h"
// 500000 = 71680
int main(int argc, char *argv[]){
  int fd, i;
  int time = 20000;
  int forks = 0;
  char path[] = "stressfs0";
  char data[512];

  printf(1, "stressfs starting\n");
  

  for(i = 0; i < forks; i++)
    if(fork() > 0)
      break;

  printf(1, "write %d\n", i);

  path[8] += i;
  fd = open(path, O_CREATE | O_RDWR);
  for(i = 0; i < time; i++){
    memset(data, i, sizeof(data));
//    printf(fd, "%d\n", i);
    int written = write(fd, data, sizeof(data));
    
    if(written < 0){
      break;
    }
  }
  close(fd);
  printf(1,"written %d",i);
  printf(1, " now read\n");

  fd = open(path, O_RDONLY);
  for (i = 0; i < time; i++){
    int success = read(fd, data, sizeof(data));
//    printf(1,"%s\n",data);
    if(!success){
      break;
    }
  }
  printf(1,"Read %d \n",i);
  close(fd);

  wait();
  
  exit();
}
