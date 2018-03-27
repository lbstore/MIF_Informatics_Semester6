#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int arraySize;
// Merge Function 
void merge(int *a, int *b, int l, int m, int r) {
	
  int h, i, j, k;
  h = l;
  i = l;
  j = m + 1;
  printf("Merge %d %d %d\n",l,m,r);
  while((h <= m) && (j <= r)) {
    if(a[h] <= a[j]) {
      b[i] = a[h++];
    }
    else {
      b[i] = a[j++];
    }
    i++;
  }
  
  //whats left
  k = h;
  if(m < h){
    m = r;
    k = j;
  }
  while(k <= m){
    b[i++] = a[k++];
  }
  for(k = l; k <= r; k++) {
    a[k] = b[k];
  }
  
  for(i = 0; i < arraySize; i++){
    printf("%d ",a[i]);
  }
  printf("\n");
		
}
void insertionSort (int a[],int start, int end){
  int i;
  for (i = start; i < end; i++){
      int j, v = a[i];
      for (j = i - 1; j >= 0; j--){
        if (a[j] <= v)
          break;
	  a[j + 1] = a[j];
      }
      a[j + 1] = v;
    }
}


// Recursive Merge Function
void mergeSort(int *a, int *b, int l, int r) {
  int m;
  printf("Called %d %d\n",l,r);
  if(r - l <= 16){
    printf("Insertion sort\n");
    insertionSort(a,l,r+1);
  }else{
    m = (l + r)/2;
    
    mergeSort(a, b, l, m);
    mergeSort(a, b, (m + 1), r);
    merge(a, b, l, m, r);
  }
  
} 
int exists(const char *fname){
    FILE *file;
    if (file = fopen(fname, "r")){
        fclose(file);
        return 1;
    }
    return 0;
}

void mergeArrays(int *a, int *b,int *c, int aSize, int bSize){
  int fullSize = aSize + bSize;
  int i,j,k;
  i = 0;
  j = 0;
  
  for(k = 0; k < fullSize; i++){
    if(i < aSize){
      if(j < bSize){
        if (a[i] < b[j]){
          c[k] = a[i++];
        }
        else{
          c[k] = b[j++];
        }
      }
      else{
        c[k] = a[i++];
      }
    }
    else{
      c[k] = b[j++];
    }
  }
}

int main(int argc, char** argv) {
	
  
  // Create and populate the array 
  int n = atoi(argv[1]);
  arraySize = n;
  int *originalArray = malloc(n * sizeof(int));
  int c;
  srand(time(NULL));
    
  for(c = 0; c < n; c++) {
    originalArray[c] = rand() % n;
  }
  int *otherArray = malloc(n * sizeof(int));
  for(int i = 0; i < n; i++){
    printf("%d ",originalArray[i]);
  }
  printf("\n");
  mergeSort(originalArray, otherArray, 0, (n - 1));
  for(int i = 0; i < n; i++){
    printf("%d ",originalArray[i]);
  }
  printf("\n");
}
