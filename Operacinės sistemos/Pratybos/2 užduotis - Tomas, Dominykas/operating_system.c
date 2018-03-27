// -----------------------------------------------------------------------
//
//
//	Tomas Petras Rupšys ir Dominykas Šiožinis
//	Operacinių Sistemų II-ojo atsiskaitymo realizacija
//
//
// -----------------------------------------------------------------------
#include <cstdio>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// C boolean apibrėžimas
#define TRUE 1
#define FALSE 0

// Darbo pabaigos tekstas
#define END_OF_WORK "\n\nDarbas baigtas..."

// Klaidų tekstai
#define FILE_FORMAT_ERROR_LENGTH "Klaida! Failo ilgis neatitinka reikalavimu!"
#define FILE_FORMAT_ERROR_BEGINNING "Klaida! Failo pradzios zyme - neteisinga!"
#define FILE_FORMAT_ERROR_ENDING "Klaida! Failo pabaigos zyme - neteisinga!"
#define FILE_FORMAT_ERROR_INCORRECT_LINES_LENGTH "Klaida! Programos maksimalus spausdinamų eiluciu kiekis - neteisingas!"
#define FILE_FORMAT_ERROR_INCORRECT_NAME "Klaida! Programos pavadinimas nenurodytas!"
#define FILE_FORMAT_WORD_LENGTH_INCORRECT "Klaida! Programoje yra netinkamas kiekis simboliu!"
#define INCORRECT_PARAMETERS "Klaida! Neteisingi parametrai!"
#define FILE_DOES_NOT_EXIST "Klaida! Failas neegzistuoja!"
#define MEMORY_ERROR_DATA_FAILURE "Klaida! Nepavyko irasyti i atminti duotuju duomenu"
#define MEMORY_ERROR_CODE_FAILURE "Klaida! Nepavyko irasyti kodo eiluciu i atminti!"
#define INCORRECT_COMMAND_READ_FROM_MEMORY "Klaida! I atminti isivele neatpazistama komanda!"

// Atminties spausdinimo tekstai
#define MEMORY_STATUS_TEXT_FIRST_LINE "\n\nAtminties bukle:\n"
#define MEMORY_STATUS_TEXT_WORD_LINE "-as zodis"
#define PAGE_TABLE_TEXT_FIRST_LINE "\n\nPuslapiavimo lentele\nVM :: RM\n"
#define REGISTER_VALUES_FIRST_LINE "Baigta vykdyti (vartotojo rezime)"
#define REGISTER_VALUES_PTR "PTR"
#define REGISTER_VALUES_MODE "MODE"
#define REGISTER_VALUES_PI "PI"
#define REGISTER_VALUES_SI "SI"
#define REGISTER_VALUES_TI "TI"
#define REGISTER_VALUES_CH1 "CH1"
#define REGISTER_VALUES_CH2 "CH2"
#define REGISTER_VALUES_IOI "IOI"
#define REGISTER_VALUES_CHPRTS "CHPRTS"
#define REGISTER_VALUES_CHPRTN "CHPRTN"
#define REGISTER_VALUES_CHREAD "CHREAD"
#define REGISTER_VALUES_SP "SP (virtualus)"
#define REGISTER_VALUES_PC "PC (virtualus)"
#define HEADLINE_CHANNEL_READ "Pradeda veikti kanalų įrenginys - vykdo READ"
#define HEADLINE_CHANNEL_PRTS "Pradeda veikti kanalų įrenginys - vykdo PRTS"
#define HEADLINE_CHANNEL_PRTN "Pradeda veikti kanalų įrenginys - vykdo PRTN"

// Programos failų formato reikalavimai
#define FILE_FORMAT_BEGINNING "$BEG"
#define FILE_FORMAT_ENDING "$END"
#define FILE_FORMAT_MAX_LENGTH 444
#define FILE_FORMAT_PROGRAM_NAME_LENGTH 36
#define FILE_FORMAT_PROGRAM_NAME_END_SYMBOL '$'
#define FILE_FORMAT_PROGRAM_NAME_FROM 4
#define FILE_FORMAT_WORD_LENGTH 4

// OS Atminties specifikacija
#define OS_DESIGN_BLOCKS_FOR_VM 16
#define OS_DESIGN_WORDS_AMOUNT 512
#define OS_DESIGN_BLOCKS_AMOUNT 32
#define OS_DESIGN_WORDS_IN_BLOCK 16
#define OS_DESIGN_BYTES_PER_WORD 4
#define OS_DESIGN_EMPTY_WORD_BYTE_SYMBOL '*'
#define OS_DESIGN_RESERVED_WORD_SYMBOL '-'
#define OS_DESIGN_DATA_BLOCK_FROM 0
#define OS_DESIGN_DATA_BLOCK_TO 6
#define OS_DESIGN_CODE_BLOCK_FROM 7
#define OS_DESIGN_CODE_BLOCK_TO 13
#define OS_DESIGN_STACK_BLOCK_FROM 14
#define OS_DESGIN_STACK_BLOCK_TO 15
#define OS_DESIGN_DEFAULT_SP_VALUE 223
#define OS_DESIGN_DEFAULT_PC_VALUE 112
#define OS_DESIGN_MODE_USER 0
#define OS_DESIGN_MODE_SUPERVISOR 1
#define OS_DESIGN_DEFAULT_PI_VALUE 0
#define OS_DESIGN_DEFAULT_PI_INVALID_ADDRESS 1
#define OS_DESIGN_DEFAULT_PI_INVALID_OPERATION 2
#define OS_DESIGN_DEFAULT_PI_INVALID_ASSIGNMENT 3
#define OS_DESIGN_DEFAULT_SI_VALUE 0
#define OS_DESIGN_DEFAULT_SI_STOP 1
#define OS_DESIGN_DEFAULT_SI_READ 2
#define OS_DESIGN_DEFAULT_SI_PRTS 3
#define OS_DESIGN_DEFAULT_SI_PRTN 4
#define OS_DESIGN_DEFAULT_TI_VALUE 100
#define OS_DESIGN_CHANNEL_FREE 0
#define OS_DESIGN_CHANNEL_IN_USE 1
#define OS_DESIGN_IOI_DEFAULT 0
#define OS_DESIGN_IOI_DONE_READ 1
#define OS_DESIGN_IOI_DONE_PRTN 2
#define OS_DESIGN_IOI_DONE_PRTS 3
#define OS_DESIGN_CH_CMD_DO 1
#define OS_DESIGN_CH_CMD_DEFAULT 0

// Atminties išvesties failo duomenys
#define MEMORY_STATUS_FILE_NAME "memory_status.txt"

// OS funkcijų string pavadinimai
#define LD "LD"     // LDxy
#define PT "PT"		// PTxy
#define ADDN "ADDN" // ADDN
#define SUBN "SUBN" // SUBN
#define READ "READ" // READ
#define PRTS "PRTS" // PRTS
#define PRTN "PRTN" // PRTN
#define STOP "STOP" // STOP

// Atmintis
char memory[OS_DESIGN_WORDS_AMOUNT][OS_DESIGN_BYTES_PER_WORD];

// Realios mašinos registrai
char ptr[4];
int mode;
int pi;
int si;
int ti;
int ch1;
int ch2;
int ioi;

// Kanalų įrenginio registrai
int chPrtn;
int chPrts;
int chRead;

// Tarpiniai kintamieji
int chToPrtn;
char *chToPrts;
char *chToRead;

// Virtualios mašinos registrai
int sp;
int pc;

/**
 * Įdiegia pradines kanalų registrų reikšmes
 */
void initialize_ch_cmd() {
  chPrtn = OS_DESIGN_CH_CMD_DEFAULT;
  chPrts = OS_DESIGN_CH_CMD_DEFAULT; 
  chRead = OS_DESIGN_CH_CMD_DEFAULT; 
}

/**
 * Įdiegia pradinę IOI reikšmę.
 */
void initialize_ioi() {
 ioi = OS_DESIGN_IOI_DEFAULT;
}
 
/**
 * Įdiegia pradinę MODE registro reikšmę.
 */
void initialize_mode() {
  mode = OS_DESIGN_MODE_USER;
}

/**
 * Įdiegia pradinę PI registro reikšmę.
 */
void initialize_pi() {
  pi = OS_DESIGN_DEFAULT_PI_VALUE;
}

/**
 * Įdiegia pradinę SI registro reikšmę.
 */
void initialize_si() {
  si = OS_DESIGN_DEFAULT_SI_VALUE;
}

/**
 * Įdiegia pradinę TI registro reikšmę.
 */
void initialize_ti() {
  ti = OS_DESIGN_DEFAULT_TI_VALUE;
}

/**
 * Įdiegia pradines kanalų registrų CH1, CH2 reikšmės.
 */
void initialize_channels() {
  ch1 = OS_DESIGN_CHANNEL_FREE;
  ch2 = OS_DESIGN_CHANNEL_FREE;
}

/**
 * Užpildo atminties masyvą simboliais, reiškiančiais tuščią baitą.
 */
void initialize_memory_to_empty_bytes() {  
  for (int i=0 ; i<OS_DESIGN_WORDS_AMOUNT ; i++) {
    for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  memory[i][j] = OS_DESIGN_EMPTY_WORD_BYTE_SYMBOL;
	}
  }
}

/**
 * Spausdina registrų reikšmes į failą.
 */
void print_register_values() {
  FILE *file;
  file = fopen(MEMORY_STATUS_FILE_NAME, "a");

  fprintf(file, "%s: %s\n", REGISTER_VALUES_PTR, ptr);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_MODE, mode);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_PI, pi);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_SI, si);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_TI, ti);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_CH1, ch1);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_CH2, ch2);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_IOI, ioi);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_CHPRTN, chPrtn);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_CHPRTS, chPrts);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_CHREAD, chRead);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_SP, sp);
  fprintf(file, "%s: %d\n", REGISTER_VALUES_PC, pc);
 
  fclose(file);
}

/**
 * Spausdina registrų reikšmes į failą ir nurodo komandą, 
 * po kurios spausdinta.
 */
void print_register_values_after_command(char *commandName) {
  FILE *file;
  file = fopen(MEMORY_STATUS_FILE_NAME, "a");
  fprintf(file, "\n%s: %s\n", REGISTER_VALUES_FIRST_LINE, commandName);
  fclose(file);
  
  print_register_values();
}

/**
 * Spausdina registrų reikšmes į failą ir nurodo eilutė virš
 * registrų.
 */
void print_register_values_with_headline(char *headline) {
  FILE *file;
  file = fopen(MEMORY_STATUS_FILE_NAME, "a");
  fprintf(file, "\n%s\n", headline);
  fclose(file);
  
  print_register_values();
}

/**
 * Išspausdina atminties būklę su nurodyta priežastimi į failą.
 */
void print_memory_status() {
  FILE *file;
  file = fopen(MEMORY_STATUS_FILE_NAME, "a");

  fprintf(file, "%s", MEMORY_STATUS_TEXT_FIRST_LINE);
  for (int i=0 ; i<OS_DESIGN_WORDS_AMOUNT ; i++) {
	for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  fprintf(file, "%c", memory[i][j]);
	}
	fprintf(file, ": %d%s\n", (i+1), MEMORY_STATUS_TEXT_WORD_LINE);
  }
  
  fclose(file);
}

/**
 * Išspausdina puslapiavimo lentelę į failą.
 */
void print_page_table() {
  FILE *file;
  file = fopen(MEMORY_STATUS_FILE_NAME, "a");

  int pageTableBlockIndex = atoi(ptr);
  int pageTableFirstWordIndex = pageTableBlockIndex * OS_DESIGN_WORDS_IN_BLOCK;

  fprintf(file, "%s", PAGE_TABLE_TEXT_FIRST_LINE);
  for (int i=0 ; i<OS_DESIGN_BLOCKS_FOR_VM ; i++) {
    fprintf(file, "%d :: %s\n", i, memory[pageTableFirstWordIndex + i]);
  }
}

/**
 * Paverčia skaičių kaip simbolį. Galioja tik rėžiuose [0 ; 9].
 */
char int_to_char_symbol(int number) {
  return (char)((int)'0' + number);
}

/**
 * Rezervuoja bloką. Techniškai, pakeičia kiekvieną žodžio baitą iš
 * OS_DESIGN_EMPTY_WORD_BYTE_SYMBOL į OS_DESIGN_RESERVED_WORD_SYMBOL.
 */
void reserve_block(int blockIndex) {
  int wordsFrom = blockIndex * OS_DESIGN_WORDS_IN_BLOCK;
  int wordsTo = blockIndex * OS_DESIGN_WORDS_IN_BLOCK + OS_DESIGN_WORDS_IN_BLOCK;
  
  for (int i=wordsFrom ; i<wordsTo ; i++) {
    for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  memory[i][j] = OS_DESIGN_RESERVED_WORD_SYMBOL;
	}
  }
}

/**
 * Įdiegia PTR registrą su reikšme, kur saugoma puslapiavimo lentelė.
 */
void initialize_ptr() {
  // Įdiegiame atsitikinių skaičių generatorių pagal laiką nuo 1970.01.01
  srand(time(NULL));
  
  // Parenkame atsitiktinį bloką puslapiavimo lentelei
  int pageTableBlockIndex = rand() % OS_DESIGN_BLOCKS_AMOUNT;
  int a1 = 0;
  int a2 = 0;
  int a3 = pageTableBlockIndex / 10;
  int a4 = pageTableBlockIndex % 10;

  // Įrašome reikšmę į PTR
  ptr[0] = int_to_char_symbol(a1);
  ptr[1] = int_to_char_symbol(a2);
  ptr[2] = int_to_char_symbol(a3);
  ptr[3] = int_to_char_symbol(a4);
  
  // Rezervuojame bloką puslapiavimo lentelei
  reserve_block(pageTableBlockIndex);
}

/**
 * Tikrina, ar realios atminties blokas yra tuščias.
 */
int is_block_empty(int blockIndex) {
  int wordsFrom = blockIndex * OS_DESIGN_WORDS_IN_BLOCK;
  int wordsTo = blockIndex * OS_DESIGN_WORDS_IN_BLOCK + OS_DESIGN_WORDS_IN_BLOCK;
  
  for (int i=wordsFrom ; i<wordsTo ; i++) {
    for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  if (memory[i][j] != OS_DESIGN_EMPTY_WORD_BYTE_SYMBOL) {
	    return FALSE;
	  }
	}
  }
  
  return TRUE;
}

/**
 * Įrašo bloko atitikmenį į puslapiavimo lentelę.
 */
void write_block_adress_to_page_table(int virtualIndex, int realIndex) {
  int pageTableBlockIndex = atoi(ptr);
  int pageTableFirstWordIndex = pageTableBlockIndex * OS_DESIGN_WORDS_IN_BLOCK;
  sprintf(memory[pageTableFirstWordIndex + virtualIndex], "%d", realIndex);
}

/**
 * Įdiegia puslapiavimo lentelę.
 */
void initialize_page_table() {
  // Įdiegiame atsitikinių skaičių generatorių pagal laiką nuo 1970.01.01
  srand(time(NULL));

  // Iteruojame per nuo 0 iki tiek blokų kiek reikia VM
  for (int i=0 ; i<OS_DESIGN_BLOCKS_FOR_VM ; i++) {
    int randomBlockIndex = -1;
	int isBlockEmpty = FALSE;
	
	// Generuojame atsitiktinį bloko numerį kol randame tuščią
	do {
	  randomBlockIndex = rand() % OS_DESIGN_BLOCKS_AMOUNT;
	  isBlockEmpty = is_block_empty(randomBlockIndex);
	} while (isBlockEmpty == FALSE);
	
	// Rezervuojame bloką
	reserve_block(randomBlockIndex);
	
	// Įrašome į puslapiavimo lentelę adresą
	write_block_adress_to_page_table(i, randomBlockIndex);
  }
}

/**
 * Gražina žodžio virtualų adresą.
 */
int get_real_word_address(int virtualWordAddress) {
  return (OS_DESIGN_BLOCKS_FOR_VM * atoi(memory[OS_DESIGN_BLOCKS_FOR_VM * atoi(ptr) + virtualWordAddress / OS_DESIGN_BLOCKS_FOR_VM])) + 
    (virtualWordAddress % OS_DESIGN_BLOCKS_FOR_VM);
}

/**
 * Įdiegia pradinį adresą, į kurį rodo SP.
 */
void initialize_sp() {
  sp = OS_DESIGN_DEFAULT_SP_VALUE;
}

/**
 * Įdiegia pradinį adresą, į kurį rodo PC.
 */
void initialize_pc() {
  pc = OS_DESIGN_DEFAULT_PC_VALUE;
}

/***
 * Tikrina, ar vartotojo pateikti programos argumentai yra teisingi.
 */
int is_argument_data_correct(int argc, const char* argv[]) {
  return argc == 2 ? TRUE : FALSE;
}

/**
 * Laukia vartotojo įvesties.
 */
void wait_for_user_interaction() {
  printf("%s\n", END_OF_WORK);
  std::getchar();
}

/**
 * Tikrina, ar failas atitinka reikalaujamą formatą.
 */
int is_file_of_required_format(char *fileByteArray) {
  // Tikriname masyvo ilgį
  if (sizeof(fileByteArray) > FILE_FORMAT_MAX_LENGTH) {
	printf("%s\n", FILE_FORMAT_ERROR_LENGTH);
	return FALSE;
  }
  
  // Tikriname pradžios žymę
  int actualFormatBeginningLength = strlen(FILE_FORMAT_BEGINNING);
  if (strncmp(fileByteArray, FILE_FORMAT_BEGINNING, actualFormatBeginningLength) != 0) {
    printf("%s\n", FILE_FORMAT_ERROR_BEGINNING);
	return FALSE;
  }
  
  // Tikriname pabaigos žymę  
  int actualFormatEndingLength = strlen(FILE_FORMAT_ENDING);
  char *lastSymbols = (char *)calloc(actualFormatEndingLength, sizeof(char));
  for (int i=0,j=(strlen(fileByteArray) - actualFormatEndingLength) ; i<actualFormatEndingLength ; i++,j++) {
	lastSymbols[i] = fileByteArray[j];
  }

  if (strcmp(lastSymbols, FILE_FORMAT_ENDING) != 0) {
	printf("%s\n", FILE_FORMAT_ERROR_ENDING);
	free(lastSymbols);
	return FALSE;
  }
  free(lastSymbols);
  
  // Jeigu viskas gerai, gražiname TRUE
  return TRUE;
}

/**
 * Nuskaito failą ir gražina simbolių masyvą. 
 */
char *init_char_array_from_file(const char *fileName) {
  // Atidarome failą
  FILE *osFile;
  osFile = fopen(fileName, "r");
  if (osFile == NULL) {
	printf("%s\n", FILE_DOES_NOT_EXIST);
    return NULL;
  }
  
  // Einame į failo pabaigą, paskaičiuojame ilgį
  fseek(osFile, 0L, SEEK_END);
  int elements = ftell(osFile);
  
  // Sukuriame char masyvą tolygų failo dydžiui
  char *dataToReturn = (char *)calloc(elements + 1, sizeof(char));
  
  // Grįžtame į failo pradžią
  fseek(osFile, 0L, SEEK_SET);

  // Nuskaitome visus char'us iš failo į masyvą
  int charCounter = 0;
  char readChar;
  while ((readChar = getc(osFile)) != EOF) {
    dataToReturn[charCounter++] = readChar;
  }
  
  // Uždarome failą
  fclose(osFile);
  
  // Pridedame pabaigos simbolį prie programos duomenų eilutės
  dataToReturn[elements + 1] = '\0'; 
  
  // Tikriname, ar failas atitinka formatą
  if (is_file_of_required_format(dataToReturn) == FALSE) {
	return NULL;
  }
  
  // Gražiname rezultatą
  return dataToReturn;
}

/**
 * Gražina tekstinę eilutę nurodytuose rėžiuose.
 */
char *substring_from_to(const char *dataLine, const int from, const int to) {
  char *arrayToReturn = (char *)calloc(to - from + 2, sizeof(char));

  for (int i=from,j=0 ; i<=to ; i++,j++) {
	arrayToReturn[j] = dataLine[i];
	
	if (i == to) {
	  arrayToReturn[j+1] = '\0';
	}
  }

  return arrayToReturn;
}

/**
 * Gražina tekstinę eilutę nuo nurodyto rėžio iki nurodyto simbolio.
 */
char *substring_from_until_symbol(const char *dataLine, const int from, const char endSymbol, const int maxLength) {
  char *arrayToReturn = (char *)calloc(maxLength, sizeof(char));
  int dataArrayIteratorIndex = from;
  int newArrayIteratorIndex = 0;
  
  do {
    char symbol = dataLine[dataArrayIteratorIndex];
	if (symbol == endSymbol) {
	  break;
	}
	
	arrayToReturn[newArrayIteratorIndex++] = symbol;
	dataArrayIteratorIndex++;
	
  } while (dataArrayIteratorIndex > 0 && dataArrayIteratorIndex <= maxLength);
  arrayToReturn[newArrayIteratorIndex] = '\0';
  
  arrayToReturn = (char *)realloc(arrayToReturn, newArrayIteratorIndex);
  return arrayToReturn;
}

/**
 * Įrašo programos vardą į bloką, kuris virtualiam lygmenį yra skirtas
 * duomenims. Nuo kur prasideda blokas nurodo: OS_DESIGN_DATA_BLOCK_FROM.
 */
int initialize_program_data_to_memory(const char *programName) {
  int programNameLength = strlen(programName);
  
  for (int i=0,j=0 ; i<strlen(programName) ; i=i+OS_DESIGN_BYTES_PER_WORD,j++) {
	int virtualWordIndex = OS_DESIGN_DATA_BLOCK_FROM * 16 + j;
  
    for (int k=0 ; k<OS_DESIGN_BYTES_PER_WORD ; k++) {
	  memory[get_real_word_address(virtualWordIndex)][k] = programName[i+k];
	}
  }

  return TRUE;
}

/**
 * Į bloką skirta programos kodui surašo visas komandas iš failo.
 */
int initialize_program_code_to_memory(char programCodeLines[][FILE_FORMAT_WORD_LENGTH + 1], int linesLength) {
  for (int i=0 ; i<linesLength ; i++) {
    int virtualWordIndex = OS_DESIGN_CODE_BLOCK_FROM * 16 + i;
	
	for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  memory[get_real_word_address(virtualWordIndex)][j] = programCodeLines[i][j];
	}
  }
  
  return TRUE;
}

/**
 * Pagal įvesties duomenis, suvedame visus duomenis į atmintį
 */
int initialize_given_program_to_memory(const char *fileByteArray) {	
  // Išgauname programos pavadinimą
  const char *programName = substring_from_until_symbol(fileByteArray, FILE_FORMAT_PROGRAM_NAME_FROM, 
    FILE_FORMAT_PROGRAM_NAME_END_SYMBOL, FILE_FORMAT_PROGRAM_NAME_LENGTH);

  if (programName == NULL) {
    printf("%s\n", FILE_FORMAT_ERROR_INCORRECT_NAME);
    return FALSE;
  }
  
  // Įrašome į atmintį
  int initializedData = initialize_program_data_to_memory(programName);
  if (initializedData == FALSE) {
	printf("%s\n", MEMORY_ERROR_DATA_FAILURE);
    return FALSE;
  }
  
  // Išgauname pačią programą į simbolių masyvą
  int programCodeLength = strlen(fileByteArray) - strlen(FILE_FORMAT_BEGINNING) - 
    strlen(FILE_FORMAT_ENDING) - strlen(programName) - 1;
  
  if (programCodeLength % FILE_FORMAT_WORD_LENGTH != 0) {
	printf("%s\n", FILE_FORMAT_WORD_LENGTH_INCORRECT);
	return FALSE;
  }
  
  int programCodeStartIndex = strlen(FILE_FORMAT_BEGINNING) + strlen(programName) + 1;

  const char* programCode = substring_from_to(fileByteArray, programCodeStartIndex, 
    programCodeStartIndex + programCodeLength - 1);

  // Iš programos simbolių masyvo, sukuriame programos žodžių 2d masyvą
  const int wordsAmount = programCodeLength / FILE_FORMAT_WORD_LENGTH;
  char programCodeLines[wordsAmount][FILE_FORMAT_WORD_LENGTH + 1];
  
  int lineNo = 0;
  for (int i=0 ; i<strlen(programCode) ; i=i+FILE_FORMAT_WORD_LENGTH) {
    for (int j=0 ; j<FILE_FORMAT_WORD_LENGTH ; j++) {
	  programCodeLines[lineNo][j] = programCode[i+j];
	}

	programCodeLines[lineNo][FILE_FORMAT_WORD_LENGTH] = '\0';
	lineNo++;
  }
  
  // Į atmintį įrašome visas kodų komandas 
  int initializedCode = initialize_program_code_to_memory(programCodeLines, lineNo);
  if (initializedCode == FALSE) {
    printf("%s\n", MEMORY_ERROR_CODE_FAILURE);
	return FALSE;
  }

  // Jeigu neįvyko problemų - gražiname sėkmės reikšmę
  return TRUE;
}

/**
 * Iš atminties spausdina programos pavadinimą.
 */
void print_program_name_from_memory() {
  for (int i=0 ; i<=FILE_FORMAT_PROGRAM_NAME_LENGTH ; i++) {
    char charToPrint = OS_DESIGN_RESERVED_WORD_SYMBOL;
	
	for (int j=0 ; j<OS_DESIGN_BYTES_PER_WORD ; j++) {
	  charToPrint = memory[get_real_word_address(OS_DESIGN_DATA_BLOCK_FROM * OS_DESIGN_WORDS_IN_BLOCK + i)][j];
	  
	  if (charToPrint != OS_DESIGN_RESERVED_WORD_SYMBOL) {
	    printf("%c", charToPrint);
	  } else {
	    break;
	  }
	}
	
	if (charToPrint == OS_DESIGN_RESERVED_WORD_SYMBOL) {
	  break;
	}
  }
  printf("\n");
}

/**
 * Iš atminties išgeneruoja žodį.
 */
char *parse_word_from_memory(int address) {
  char *word = (char *)calloc(OS_DESIGN_BYTES_PER_WORD, sizeof(char));
  
  for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
    word[i] = memory[address][i];
  }
  
  return word;
}

/**
 * Skaičių paverčia į tekstinę eilutę.
 */
char *decimal_to_string_format(int decimal) {
  char *returnable = (char *)calloc(OS_DESIGN_BYTES_PER_WORD, sizeof(char));
  sprintf(returnable, "%d", decimal);
  return returnable;
}

/**
 * Į nurodytą realų adresą įrašomas žodis.
 */
void write_word_to_memory_at(int realAddress, char *word) {
  for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
    memory[realAddress][i] = word[i];
  }
}

/**
 * Konvertuoja du simbolius, is eiles sudarancius sesioliktaini
 * skaiciu i desimatini ir grazina jo reiksme.
 */
int two_hex_symbols_to_decimal(char x, char y) {
  char hexString[3] = {x, y, '\0'};
  return strtol(hexString, NULL, 16);
}

/**
 * Tikrina, ar pateiktas adresas patenka į atmintį ir pagal
 * tai gražina loginę reikšmę.
 */
int is_address_within_boundaries(int realWordAddress) {
  if (realWordAddress < 0 || realWordAddress > OS_DESIGN_WORDS_AMOUNT) {
    return FALSE;
  }

  return TRUE;
}

/**
 * LDxy – į steko viršūnę perkelia reikšmę iš duomenų srities 
 * adresu SP++; 16 * x + y. (x < 7). xy - sesioliktainis skaicius.
 */
void os_command_ld(char x, char y) {
  int wordNumber = two_hex_symbols_to_decimal(x, y);
  sp++;
  
  if (is_address_within_boundaries(get_real_word_address(wordNumber)) == FALSE) {
    pi = OS_DESIGN_DEFAULT_PI_INVALID_ADDRESS;
    return;
  }
  
  for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
    memory[get_real_word_address(sp)][i] = memory[get_real_word_address(wordNumber)][i];
  }
}

/**
 * PTxy – steko viršūnėje esantį žodį deda į duomenų sritį nurodytu
 * adresu 16 * x + y. (x < 7); SP--. xy - sesioliktainis skaicius.
 */
void os_command_pt(char x, char y) {
  int wordNumber = two_hex_symbols_to_decimal(x, y);
  
  if (is_address_within_boundaries(get_real_word_address(wordNumber)) == FALSE) {
    pi = OS_DESIGN_DEFAULT_PI_INVALID_ADDRESS;
    return;
  }
  
  for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
    memory[get_real_word_address(wordNumber)][i] = memory[get_real_word_address(sp)][i];
  }
  
  sp--;
}

/**
 * ADDN – sudeda du viršutinius steko elementus. Rezultatą padeda į 
 * steko viršūnę ir steko rodyklę sumažina vienetu. [SP – 1] = [SP – 1] 
 * + [SP]; SP--.
 */
void os_command_addn() {
  int numberOne = atoi(memory[get_real_word_address(sp - 1)]);
  int numberTwo = atoi(memory[get_real_word_address(sp)]);
  int sumResult = numberOne + numberTwo;
  char *result = decimal_to_string_format(sumResult);
  write_word_to_memory_at(get_real_word_address(sp - 1), result);
  sp--;
}

/**
 * SUBN – atima steko viršūnėje esantį elementą iš antro nuo viršaus 
 * elemento. Rezultatą padeda į steko viršūnę  ir steko rodyklę sumažina 
 * vienetu. [SP – 1] = [ SP – 1] - [SP]; SP--.
 */
void os_command_subn() {
  int numberOne = atoi(memory[get_real_word_address(sp - 1)]);
  int numberTwo = atoi(memory[get_real_word_address(sp)]);
  int subResult = numberOne - numberTwo;
  char *result = decimal_to_string_format(subResult);
  write_word_to_memory_at(get_real_word_address(sp - 1), result);
  sp--;
}

/**
 * READ – nurodo kanalų įrenginiui vykdyti skaitymą.
 */
void os_command_read() {
  chRead = OS_DESIGN_CH_CMD_DO;
  si = OS_DESIGN_DEFAULT_SI_READ;
}

/**
 * PRTS – nurodo kanalų įrenginiui vykdyti eilutės spausdinimą.
 */
void os_command_prts() {
  chPrts = OS_DESIGN_CH_CMD_DO;
  si = OS_DESIGN_DEFAULT_SI_PRTS;
}

/**
 * PRTN – nurodo kanalų įrenginiui vykdyti skaičiaus spausdinimą.
 */
void os_command_prtn() {
  chPrtn = OS_DESIGN_CH_CMD_DO;
  si = OS_DESIGN_DEFAULT_SI_PRTN;
}

/**
 * STOP – nurodo registrui, kad reikia stabdyti programą.
 */
void os_command_stop() {
  si = OS_DESIGN_DEFAULT_SI_STOP;
}

/**
 * Nuskaito komandą, kokia buvo nurodyta atmintyje ir ją vykdo.
 */
void parse_command_and_launch_it(char *command) {
  // Komanda PTxy
  if (strncmp(LD, command, 2) == 0) {
	os_command_ld(command[2], command[3]);
  
  // Komanda PTxy
  } else if (strncmp(PT, command, 2) == 0) {
    os_command_pt(command[2], command[3]);
  
  // Komanda ADDN
  } else if (strncmp(ADDN, command, 4) == 0) {
    os_command_addn();
  
  // Komanda SUBN
  } else if (strncmp(SUBN, command, 4) == 0) {
    os_command_subn();
  
  // Komanda READ
  } else if (strncmp(READ, command, 4) == 0) {
	os_command_read();
  
  // Komanda PRTS
  } else if (strncmp(PRTS, command, 4) == 0) {
	os_command_prts();
  
  // Komanda PRTN
  } else if (strncmp(PRTN, command, 4) == 0) {
	os_command_prtn();
  
  // Komanda STOP
  } else if (strncmp(STOP, command, 4) == 0) {
	os_command_stop();
  
  // Neatpažinta komanda
  } else {
    pi = OS_DESIGN_DEFAULT_PI_INVALID_OPERATION;
  }
  
  // Padidiname komandos skaitliuką
  pc++;
  
  // Spausdiname registrų reikšmes
  print_register_values_after_command(command);
}

/**
 * Vykdomas nuskaitymas iš kanalų įrenginio. nuskaito 
 * vartotojo įvedimą kaip skaičių ir įrašo į steko viršūnę.
 */
void channel_read() {
  int numberTyped = 0;
  scanf("%d", &numberTyped);
  
  if (numberTyped < -999 || numberTyped > 9999) {
    pi = OS_DESIGN_DEFAULT_PI_INVALID_ASSIGNMENT;
  }
  
  chToRead = decimal_to_string_format(numberTyped); 
}

/**
 * Vykdomas skaičiaus spausdinimas iš kanalų įrenginio.
 * Steko viršūnėje esantį žodį traktuoja kaip skaitinę reikšmę ir išveda
 * į išvedimo įrenginį.
 */
void channel_prtn() {
  char *numberToPrintStr = (char *)calloc(OS_DESIGN_BYTES_PER_WORD, sizeof(char));
  
  for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
    numberToPrintStr[i] = memory[get_real_word_address(sp)][i];
  }
  
  chToPrtn = atoi(numberToPrintStr);
}

/**
 * Vykdomas tekstinės eilutės spausdinimas iš kanalų įrenginio.
 * Steko viršūnėje esantį žodį traktuoja kaip simbolius ir išveda į 
 * išvedimo įrenginį.
 */
void channel_prts() {
  chToPrts = memory[get_real_word_address(sp)];
}

/**
 * Įjungia kanalų įrenginį ir tikrina kanalų registrus.
 */
void enable_channel_mechanism() {
  // Vykdome skaičiaus nuskaitymą jeigu laisvas 1 kanalas
  if (chRead == OS_DESIGN_CH_CMD_DO && ch1 == OS_DESIGN_CHANNEL_FREE) {	
    char headline[] = HEADLINE_CHANNEL_READ;
    print_register_values_with_headline(headline);
    channel_read();
    chRead = OS_DESIGN_CH_CMD_DEFAULT;
	
	ioi = OS_DESIGN_IOI_DONE_READ;
	
  // Vykdome eilutės spausdinimą jeigu laisvas 2 kanalas
  } else if (chPrts == OS_DESIGN_CH_CMD_DO && ch2 == OS_DESIGN_CHANNEL_FREE) {
	char headline[] = HEADLINE_CHANNEL_PRTS;
    print_register_values_with_headline(headline);
    channel_prts();
    chPrts = OS_DESIGN_CH_CMD_DEFAULT;
    
	ioi = OS_DESIGN_IOI_DONE_PRTS;
  
  // Vykdome skaičiaus spausdinimą jeigu laisvas 2 kanalas
  } else if (chPrtn == OS_DESIGN_CH_CMD_DO && ch2 == OS_DESIGN_CHANNEL_FREE) {	
    char headline[] = HEADLINE_CHANNEL_PRTN;
	print_register_values_with_headline(headline);
    channel_prtn();
    chPrtn = OS_DESIGN_CH_CMD_DEFAULT;
	
	ioi = OS_DESIGN_IOI_DONE_PRTN;
  }  
}

/**
 * Baigia darbą bei atspausdina atminties išraišką ir puslapiavimo
 * lentelę.
 */
void perform_interrupt_quit(int errorCode) {
  char stopName[] = STOP;
  print_register_values_after_command(stopName);
  print_memory_status();
  print_page_table();
  exit(errorCode);
}

/**
 *  Tikrina pertraukimus ir juos vykdo.
 */
void check_interrupts() {
  // Pereiname į supervizoriaus rėžimą, jeigu vykdomas pertraukimas
  if ((pi + si) > 0 || ti == 0) {
    mode = OS_DESIGN_MODE_SUPERVISOR;
  }
  
  // Jeigu PI reikšme didesnė už 0, vadinasi ivyko klaida
  if (pi > 0) {
    perform_interrupt_quit(EXIT_FAILURE);

  // Jeigu taimeris tapo 0, tiketina, kad:
  //  - patekome į infinite loop'ą
  //  - programoje esti daugiau komandų nei leidžiama
  } else if (ti == 0) {
    perform_interrupt_quit(EXIT_FAILURE);

  // Jeigu SI tapo 1, vadinasi vykdoma komandą STOP
  } else if (si == OS_DESIGN_DEFAULT_SI_STOP) {
    perform_interrupt_quit(EXIT_SUCCESS);
	
  // Jeigu SI tapo 2, vadinasi vykdoma komanda READ
  } else if (si == OS_DESIGN_DEFAULT_SI_READ) {
    ch1 = OS_DESIGN_CHANNEL_IN_USE;
	sp++;
    write_word_to_memory_at(get_real_word_address(sp), chToRead);
	
  // Jeigu SI tapo 3, vadinasi vykdoma komanda PRTS
  } else if (si == OS_DESIGN_DEFAULT_SI_PRTS) {
    ch2 = OS_DESIGN_CHANNEL_IN_USE;
	for (int i=0 ; i<OS_DESIGN_BYTES_PER_WORD ; i++) {
      printf("%c", chToPrts[i]);
    }
    printf("\n");
	
  // Jeigu SI tapo 4, vadinasi vykdoma komanda PRTN
  } else if (si == OS_DESIGN_DEFAULT_SI_PRTN) {
    ch2 = OS_DESIGN_CHANNEL_IN_USE;
    printf("%d\n", chToPrtn);
  }

  // Atnaujiname SI ir PI
  si = OS_DESIGN_DEFAULT_SI_VALUE;
  pi = OS_DESIGN_DEFAULT_PI_VALUE;
  
  // Jeigu IOI rodo, kad baige spausdinimo darba - atlaisviname antraji kanala
  if (ioi == OS_DESIGN_IOI_DONE_PRTN
      || ioi == OS_DESIGN_IOI_DONE_PRTN) {
    ch2 = OS_DESIGN_CHANNEL_FREE;
    ioi = OS_DESIGN_IOI_DEFAULT;
	
  // Jeigu IOI rodo, kad baige skaitymo darba - atlaisviname pirmaji kanala
  } else if (ioi == OS_DESIGN_IOI_DONE_READ) {
    ch1 = OS_DESIGN_CHANNEL_FREE;
    ioi = OS_DESIGN_IOI_DEFAULT;
  }
  
  // Grįžtame į vartotojo rėžimą
  mode = OS_DESIGN_MODE_USER;
}

/**
 * Vykdo visą OS algoritmą iš atminties.
 */
void load_os() {  
  while (true) {
	// Dekoduojame komandą
	char *commandName = parse_word_from_memory(get_real_word_address(pc));
	
	// Vykdome komandą
	parse_command_and_launch_it(commandName);
	
	// Sumažiname taimerio reikšmė
	ti--;
	
	// Įjungiame kanalų įrenginį
	enable_channel_mechanism();
	
	// Tikriname pertraukimus
	check_interrupts();
  }
}

/**
 * Pradeda darbą. 
 */
int main(int argc, const char *argv[]) {
  // Tikriname vartotojo įvestus parametrus
  if (is_argument_data_correct(argc, argv) == FALSE) {
    printf("%s\n", INCORRECT_PARAMETERS);
	wait_for_user_interaction();
	return EXIT_FAILURE;
  }
  
  // Nuskaitome failo duomenis į baitų masyvą
  const char *fileByteArray = init_char_array_from_file(argv[1]);
  if (fileByteArray == NULL) {
    wait_for_user_interaction();
	return EXIT_FAILURE;
  }
	
  // Išvalome failą, skirtą atminties būklei stebėti,
  // jeigu toks egzistuoja
  remove(MEMORY_STATUS_FILE_NAME);
	
  // Įdiegiame registrų pradines reikšmes
  initialize_memory_to_empty_bytes();
  initialize_channels();
  initialize_mode();
  initialize_pi();
  initialize_si();
  initialize_ti();
  initialize_ptr();
  initialize_page_table();
  initialize_sp();
  initialize_pc();
  initialize_ioi();
  initialize_ch_cmd();
  
  // Pakrauname programą į atmintį
  int flowSuccess = initialize_given_program_to_memory(fileByteArray);
  if (flowSuccess == FALSE) {
	wait_for_user_interaction();
	return EXIT_FAILURE;
  }
  
  // Vykdome pačią programą
  load_os();
  
  // Jeigu programa pasiekia šį tašką, tai ji išjungiama ne su OS komanda
  return EXIT_FAILURE;
}