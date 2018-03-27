//INTERPRETATORIUS.C
#include <stdio.h>
#include <stdlib.h>

#include "interpretatorius.h"
#include "procesorius.h"

#define SKAICIUS 11

char komandos[SKAICIUS][2] = { { 'K', 'R' },    //Issaugo reiksme i registra R
                               { 'S', 'R' },    //Registro R reiksme issaugo atmintyje
                               { 'S', 'U' },    //Sudetis (registras + atmintis) 
                               { 'A', 'T' },    //Atimtis (registras - atmintis)
                               { 'S', 'A' },    //Sandauga (registras * atmintis)
                               { 'P', 'R' },    //Palygina registra R su atminties reiksme
                               { 'G', 'O' },    //valdymo perdavimas PC = 10x+y
                               { 'P', 'T' },    //patikrina, ar registro C reiksme true
                               { 'P', 'D' },    //isveda duomenis
                               { 'R', 'D' },    //Skaito duomenis is isores
                               { 'H', 'A' } };  //Halt - sustojimo komanda
enum { kr = 0, sr, su, at, sa, pr, go, pt, pd, rd, ha };

long sudetis(long);
long atimtis(long);
long sandauga(long);
void skaityti_duomenis(int, int);
void vykdyti_komanda();
void isvesti_duomenis(int, int);
int palyginti_komandas(char*, char*, int);

//------------------------------------------------------------------------------
short get_pc() {
  return PC; 
}
//------------------------------------------------------------------------------
//int palyginti_komandas(char* mas1, char* mas2, int ilgis) {
//  int palyginimas = 1, j;
//  for (j = 0; j < ilgis; j++) {
//    if ((palyginimas) && (mas1[j] != mas2[j])) palyginimas = 0;
//  }
//  return palyginimas;
//}

//Vykdo nuskaityta is atminties komanda pagal joje nurodytus parametrus
void vykdyti_komanda(char* kom)
{
  int i = 0;
  int m = 0;
  //Paygina is atminties nuskaitytas komandas su apibreztomis
  while ((i < SKAICIUS) && !m) { 
    if (palyginti_komandas(kom, komandos[i], 2)) m = 1;
    i++;
  }
  //
  if (i > SKAICIUS-1) { 
    printf("Tokia komanda neapibrezta: %c%c%c%c.\nVM darbo pabaiga.\n", kom[0], kom[1], kom[2], kom[3]);
    system("PAUSE");
    exit(1);    
  }
  
  //char - 48
  int block = kom[2] - 48;
  int field = kom[3] - 48;
  
  switch (--i) { //vykdo komandas, isskyrus HALT
    case kr: R = atmintis[psl[block]-1][field]; break;
    case su: R = sudetis(atmintis[psl[block]-1][field]); break;
    case at: R = atimtis(atmintis[psl[block]-1][field]); break;
    case sa: R = sandauga(atmintis[psl[block]-1][field]); break;
    case go: PC = block*10 + field; break;
    case rd: skaityti_duomenis(block, 0); break;
    case pr: C = (R == atmintis[psl[block]-1][field]); break;
    case pt: if (C) PC = block*10 + field; break;
    case pd: isvesti_duomenis(block, 0); break;
    case sr: atmintis[psl[block]-1][field] = R; break;
  }
}
//------------------------------------------------------------------------------
//Duomenu apsikeitimas su isore (vyksta blokais)
void skaityti_duomenis(int a, int b) {
  //char* simb = calloc(sizeof(char), 41);

char * simb;

simb = (char*) calloc(sizeof(char), 1);

  
  fgets(simb, 40, stdin);
 
  int i;
  for (i = 0; i < 40; i++) { //Pasalinami naujos eilutes simboliai
    if (simb[i] == '\n') {
      simb[i] = 0;
      int j;
      for (j = 0; j < (i%4)-1; j++) simb[i+j] = ' ';  //Uzpildo tarpo simboliais
    }
  }
  
  for (i = 0; i < 10; i++) {  //Nuskaitytus duomenis padeda i atminties bloka
    atmintis[psl[a]-1][b+i] = simb[0+(4*i)]*0x1000000+simb[1+(4*i)]*0x10000+simb[2+(4*i)]*0x100+simb[3+(4*i)];
  } 
}
//------------------------------------------------------------------------------
//Nuskaito komanda is atminties
//ir padidina skaitliuko pc reiksme vienetu
int sekanti_komanda() 
{
  char komanda[4] = { (atmintis[psl[(PC/10)]-1][(PC%10)] & 0xFF000000) / 0x1000000, (atmintis[psl[(PC/10)]-1][(PC%10)] & 0xFF0000) / 0x10000, 
                      (atmintis[psl[(PC/10)]-1][(PC%10)] & 0xFF00) / 0x100, (atmintis[psl[(PC/10)]-1][(PC%10)] & 0xFF) };
  PC++;
  
  //Jeigu ne paskutine komanda, kuri yra stop
  if (!palyginti_komandas(komandos[ha], komanda, 2)) {
    vykdyti_komanda(komanda);
    return 1;
  } 
  else return 0; 
}
//------------------------------------------------------------------------------
//Isveda duomenis i ekrana
void isvesti_duomenis(int a, int b) {
  int i, j;
  for (i = 0; i < 10; i++) { 
    if (atmintis[psl[a]-1][b+i] != 0) {
      char duom[4] = { (atmintis[psl[a]-1][b+i] & 0xFF000000) / 0x1000000, (atmintis[psl[a]-1][b+i] & 0xFF0000) / 0x10000, 
                       (atmintis[psl[a]-1][b+i] & 0xFF00) / 0x100, (atmintis[psl[a]-1][b+i] & 0xFF) };
      
      for (j = 0; j < 4; j++) printf("%c", duom[j]);
    }                         
  }
  printf("\n");
}
//------------------------------------------------------------------------------
//Registras R + atmintis su adresu adr
long sudetis(long adr) {
  
  char reiksm1[4] = { (adr & 0xFF000000) / 0x1000000, (adr & 0xFF0000) / 0x10000, (adr & 0xFF00) / 0x100, adr & 0xFF };
  char reiksm2[4] = { (R & 0xFF000000) / 0x1000000, (R & 0xFF0000) / 0x10000, (R & 0xFF00) / 0x100, R & 0xFF };  
  
  //Gaunami desimtainiai skaiciai
  int reiks1 = (reiksm1[0]-48)*1000 + (reiksm1[1]-48)*100 + (reiksm1[2]-48)*10 + reiksm1[3]-48;
  int reiks2 = (reiksm2[0]-48)*1000 + (reiksm2[1]-48)*100 + (reiksm2[2]-48)*10 + reiksm2[3]-48;
  int suma = reiks1 + reiks2;
  
  if (suma > 9999) {
    printf("Ivestu skaiciu suma virsijo 4 baitus (zodi).\nVM darbo pabaiga.\n");
    system("PAUSE");
    exit(1);
  }
  int sum1 = suma/1000;
  int sum2 = (suma%1000)/100;
  int sum3 = (suma%100)/10;
  int sum4 = suma%10;   
  sum1 = sum1 + 48;
  sum2 = sum2 + 48; 
  sum3 = sum3 + 48; 
  sum4 = sum4 + 48;
  
  return (sum1 * 0x1000000 + sum2 * 0x10000 + sum3 * 0x100 + sum4);
}
//------------------------------------------------------------------------------
//Grazina: registras R - atmintis su adresu adr
long atimtis(long adr) { 
   
  char eil1[4] = { (adr & 0xFF000000) / 0x1000000, (adr & 0xFF0000) / 0x10000, (adr & 0xFF00) / 0x100, adr & 0xFF };
  char eil2[4] = { (R & 0xFF000000) / 0x1000000, (R & 0xFF0000) / 0x10000, (R & 0xFF00) / 0x100, R & 0xFF }; 
  
  int sk1 = (eil1[0]-48)*1000 + (eil1[1]-48)*100 + (eil1[2]-48)*10 + eil1[3]-48;
  int sk2 = (eil2[0]-48)*1000 + (eil2[1]-48)*100 + (eil2[2]-48)*10 + eil2[3]-48;
  int skirtumas = sk1 - sk2;
  
  if (skirtumas < 0) {
    printf("Klaidingai ivesti duomenys (pirmas skaicius mazesnis uz antra).\n");
    system("PAUSE");
    exit(1);
  }
  
  int skirt1 = skirtumas/1000;
  int skirt2 = (skirtumas%1000)/100;
  int skirt3 = (skirtumas%100)/10;
  int skirt4 = skirtumas%10;
  
  skirt1 = skirt1 + 48;
  skirt2 = skirt2 + 48;
  skirt3 = skirt3 + 48;
  skirt4 = skirt4 + 48;
  
  long rez = skirt1 * 0x1000000 + skirt2 * 0x10000 + skirt3 * 0x100 + skirt4;
  return rez;
}
//------------------------------------------------------------------------------
long sandauga(long adr) { 
   
  char eil1[4] = { (adr & 0xFF000000) / 0x1000000, (adr & 0xFF0000) / 0x10000, (adr & 0xFF00) / 0x100, adr & 0xFF };
  char eil2[4] = { (R & 0xFF000000) / 0x1000000, (R & 0xFF0000) / 0x10000, (R & 0xFF00) / 0x100, R & 0xFF }; 
  
  int sk1 = (eil1[0]-48)*1000 + (eil1[1]-48)*100 + (eil1[2]-48)*10 + eil1[3]-48;
  int sk2 = (eil2[0]-48)*1000 + (eil2[1]-48)*100 + (eil2[2]-48)*10 + eil2[3]-48;
  int sandauga = sk1 * sk2;
  
  if ((sk1 < 0) || (sk1 < 0) || (sandauga > 9999)) {
    printf("Klaidingai ivesti skaiciai (neigiami arba ju sandauga > 9999).\n");
    system("PAUSE");
    exit(1);
  }
  else {
    int sand1 = sandauga/1000;
    int sand2 = (sandauga%1000)/100;
    int sand3 = (sandauga%100)/10;
    int sand4 = sandauga%10;
    
    sand1 = sand1 + 48;
    sand2 = sand2 + 48;
    sand3 = sand3 + 48;
    sand4 = sand4 + 48;
    
    long rez = sand1 * 0x1000000 + sand2 * 0x10000 + sand3 * 0x100 + sand4;
    return rez;
  }
}
//------------------------------------------------------------------------------
//Isveda i ekrana registru turini desimtainiu pavidalu
void rodyti_registrus() 
{
  printf("Registru reiksmes: \n");
  if (PC > 10) printf("   PC: %d\n", PC);
  else printf("   PC: 0%d\n", PC);
  printf("   C:  %d\n", C);
  printf("   R:  %c%c%c%c\n", (R & 0xFF000000) / 0x1000000, (R & 0xFF0000) / 0x10000, (R & 0xFF00) / 0x100, R & 0xFF);
}
//-----------------------------------------
