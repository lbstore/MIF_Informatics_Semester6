//MAIN.C
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>

#include "interpretatorius.h"
#include "procesorius.h"

char rezimas;
char programos_antraste[4];
char antraste_formatas[4] = { 'P', 'R', 'O', 'G' }; 
char pavadinimas[20];

int palyginti_komandas(char*, char*, int);
void atidaryti_faila(char*);
void nuskaityti_komandas(int);
long* isskirti_atminti();
void rodyti_atminti();
int tikrinti_atminti(int);

//------------------------------------------------------------------------------
int main(int argc, char *argv[])
{ 
  char simb;
  int pasirinkta = 0;
  
  psl = isskirti_atminti();   
  atidaryti_faila("programa.txt");       
           
  printf("=== VIRTUALI MASINA ===\n");
  printf("\nPasirinkite VM darbo rezima:\n1 - programos vykdymas is karto\n2 - programos vykdymas pazingsniniu rezimu\n");
  while (!pasirinkta) {
    simb = getchar();
    switch (simb){
      case '1': rezimas = 'r'; pasirinkta = 1; break;
      case '2': rezimas = 's'; pasirinkta = 1; break;
      default: printf("Neteisingai pasirinktas VM darbo rezimas.\n");
    }
  }
  
  if (rezimas == 'r') {  //prorama vykdoma is karto
    printf("\n");
    while (sekanti_komanda());
  }
  else {                 //programa vykdoma pazingsniui
    int end = 0;
    printf("\n---Programos vykdymas pazingsniui---\n");
    int step = 1;
    while (!end) {
      printf("\n(%d zingsnis)_________________________________________\n ", step);
      printf("Pasirinkite komandos numeri is saraso:\n");
      printf("\t     1 - sekancios komandos vykdymas: ");
      short PC = get_pc();
      char simb_pc[4] = { (atmintis[psl[(PC/10)-1]][PC%10] & 0xFF000000) / 0x1000000, (atmintis[psl[(PC/10)-1]][PC%10] & 0xFF0000) / 0x10000, 
                          (atmintis[psl[(PC/10)-1]][PC%10] & 0xFF00) / 0x100, (atmintis[psl[(PC/10)-1]][PC%10] & 0xFF) };
      printf("%c%c%c%c\n", simb_pc[0], simb_pc[1], simb_pc[2], simb_pc[3]);
      printf("\t     2 - atminties rodymas\n");
      printf("\t     3 - registru rodymas\n");
      printf("\t     4 - darbo nutraukimas\n");
      printf("______________________________________________________\n\n");
      char c = getchar();
      switch (c) {
        case '1': end = !sekanti_komanda(); step++; break;
        case '3': rodyti_registrus(); break;
        case '2': rodyti_atminti(); break;
        case '4': end = 1; break;
        default: printf("Neteisingai ivestas komandos numeris.\n");
      }
    }
    printf("Programa baige darba.\n");
  }
 system("PAUSE");	
  return 0;
}
//------------------------------------------------------------------------------
//Palygina 2 char masyvus. Grazina 1, jei abu yra vienodi
int palyginti_komandas(char* mas1, char* mas2, int ilgis) {
  int palyginimas = 1, j;
  for (j = 0; j < ilgis; j++) {
    if ((palyginimas) && (mas1[j] != mas2[j])) palyginimas = 0;
  }
  return palyginimas;
}
//------------------------------------------------------------------------------
//Atidaro faila ir is jo skaito komandas
void atidaryti_faila(char* failas)
{
  //char* simb = calloc(sizeof(char), 1);
  //pData = (int*) calloc (i,sizeof(int));
char * simb;

simb = (char*) calloc(sizeof(char), 1);
  int file = open(failas, O_RDONLY, 0); 
   
  if (file == -1) { 
    printf("Failo atidaryti neimanoma.\nVM darbo pabaiga.\n");
    system("PAUSE");
    exit(1);
  } 

  int bytes = read(file, programos_antraste, 4); 
  read(file, simb, 1); 
  *simb = 0;
  //patikrina, ar teisingai nuroyta programos antraste
  if (!palyginti_komandas(programos_antraste, antraste_formatas, 4)) {
    printf("Klaidingai nurodyta programos antraste.\nVM darbo pabaiga.\n");
    system("PAUSE");
    exit(1);
  }
  
  //skaito programos pavadinimas
  int i = 0; 
  while (*simb != '\n') {
    read(file, simb, 1);
    if (*simb != '\n') {
      pavadinimas[i] = *simb;
      i++;
    }
    if (i > 20) { 
      printf("Programoje nurodytas per ilgas pavadinimas.\nVM darbo pabaiga.\n");
      system("PAUSE");
      exit(1);
    }
  }
  
  //is isorines atminties nuskaito komandas i atminti
  nuskaityti_komandas(file); 
  
  //faile nera aprasyta jokiu VM komandu
  if ((PC == 0) && (atmintis[psl[(PC/10)]-1][(PC%10)] == 0)) {
    printf("Programoje nera uzrasytu komandu.\nVM darbo pabaiga.\n");
    system("PAUSE");
    exit(1);    
  }  
  
  close(file);
}
//------------------------------------------------------------------------------
void nuskaityti_komandas(int handle) 
{
  int blokas = 0, zodis = 0;
  char pabaiga_formatas[4] = { '.', 'E', 'N', 'D' };
  char komanda[4];
  
  //skaitomos komandos is failo
  while ((read(handle, komanda, 4) != 0) && !palyginti_komandas(komanda, pabaiga_formatas, 4)) { 
    char s; 
    read(handle, &s, 1);

    char komanda_move[2] = { 'M', 'V' };
    if (palyginti_komandas(komanda, komanda_move, 2)) { 
      blokas = komanda[2] - 48;
      zodis = komanda[3] - 48;
      if ((blokas > 9) || (blokas < 0)) {
        printf("Nekorektiskai parasyta komanda (nurodo bloga adresa atmintyje): %c%c%c%c.\nVM darbo pabaiga.\n", komanda[0], komanda[1], komanda[2], komanda[3]);
        system("PAUSE");
        exit(1);
      }
    }
    else {
      if ((blokas <= 9) && (blokas >= 0)) { 
        atmintis[psl[blokas]-1][zodis] = komanda[0]*0x1000000+komanda[1]*0x10000+komanda[2]*0x100+komanda[3];
        zodis++;
        if (zodis > 9) {
          blokas++;
          zodis = 0;
        }
      }
      else {
        printf("Komanda %c%c%c%c perzenge atminties ribas.\nVM darbo pabaiga.\n", komanda[0], komanda[1], komanda[2], komanda[3]);
        system("PAUSE");
        exit(1);        
      }
    }
  }  
}
//------------------------------------------------------------------------------
long* isskirti_atminti()
{
  int psl_adr = 30;      //Bloko adresas atminyje puslapiams
  int sk = 0;
  int i = 0;
  
  while ((atmintis[psl_adr][0] != 0) && (psl_adr < 35)) psl_adr++;  
  if (atmintis[psl_adr][0] != 0) return 0; //Atmintyje nera laisvos vietos puslapiams
 
  while ((sk < 10) && (i < 30)) {
    if (tikrinti_atminti(i)) {
      atmintis[psl_adr][sk] = i + 1;   //realaus takelio numeris
      sk++;
    }
    i++;
  }
  
  if (sk == 10) {
    return &atmintis[psl_adr][0];
  }
  else return 0;   //Atmintyje nera laisvos vietos isskirti 10 bloku virtualaus adreso
}
//------------------------------------------------------------------------------
//I ekrana isveda atminties turini 
void rodyti_atminti()
{
  int i, j, k;
  
  printf("   |Atmintis:\n");
  printf("Blokas/Zodis:\n");
  printf("     ");
  for (k = 0; k < 10; k++) printf("  %d  ", k);
  printf("\n______________________________________________________\n");
  for (i = 0; i < 35; i++) {
    if (i < 10) printf("%d  | ", i);
    else printf("%d | ", i);
    for (j = 0; j < 10; j++) {
      if (atmintis[i][j] > 10) printf("%c%c%c%c ", (atmintis[i][j] & 0xFF000000) / 0x1000000, (atmintis[i][j] & 0xFF0000) / 0x10000, (atmintis[i][j] & 0xFF00) / 0x100, atmintis[i][j] & 0xFF); 
      else if (atmintis[i][j] == 0) printf("0    ");
           else if (atmintis[i][j] != 10) printf("%d    ", atmintis[i][j]);
                else printf("10   ");
    }
    printf("\n");
  }
  printf("______________________________________________________\n");
}
//------------------------------------------------------------------------------
//Tikrina, ar atminties laukas laisvas
int tikrinti_atminti(int x)
{
  int i, j;
  int laisva = TRUE;
  
  for (i = 30; i < 35; i++) {
    for (j = 0; j < 10; j++) {
      if ((laisva) && (x == atmintis[i][j] - 1)) laisva = FALSE; 
    }
  }
  return laisva;
}
//------------------------------------------------------------------------------
