/* there are 25 lines each of 80 columns; each element takes 2 bytes */
#ifndef OLDCONSOLE_H
#define OLDCONSOLE_H

#include "defs.cc"
#define LINES 25
#define COLUMNS_IN_LINE 80
#define BYTES_FOR_EACH_ELEMENT 2
#define SCREENSIZE BYTES_FOR_EACH_ELEMENT * COLUMNS_IN_LINE * LINES

#define KEYBOARD_DATA_PORT 0x60
#define KEYBOARD_STATUS_PORT 0x64
#define ENTER_KEY_CODE 0x1C


#define BACKSPACE 0x100
#define CRTPORT 0x3d4

extern void keyboard_handler(void);
extern char read_port(unsigned short port);
extern void write_port(unsigned short port, unsigned char data);
unsigned char keyboard_map[128] ={
    0,  27, '1', '2', '3', '4', '5', '6', '7', '8',	/* 9 */
  '9', '0', '-', '=', '\b',	/* Backspace */
  '\t',			/* Tab */
  'q', 'w', 'e', 'r',	/* 19 */
  't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\n',	/* Enter key */
    0,			/* 29   - Control */
  'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';',	/* 39 */
 '\'', '`',   0,		/* Left shift */
 '\\', 'z', 'x', 'c', 'v', 'b', 'n',			/* 49 */
  'm', ',', '.', '/',   0,				/* Right shift */
  '*',
    0,	/* Alt */
  ' ',	/* Space bar */
    0,	/* Caps lock */
    0,	/* 59 - F1 key ... > */
    0,   0,   0,   0,   0,   0,   0,   0,
    0,	/* < ... F10 */
    0,	/* 69 - Num lock*/
    0,	/* Scroll Lock */
    0,	/* Home key */
    0,	/* Up Arrow */
    0,	/* Page Up */
  '-',
    0,	/* Left Arrow */
    0,
    0,	/* Right Arrow */
  '+',
    0,	/* 79 - End key*/
    0,	/* Down Arrow */
    0,	/* Page Down */
    0,	/* Insert Key */
    0,	/* Delete Key */
    0,   0,   0,
    0,	/* F11 Key */
    0,	/* F12 Key */
    0,	/* All other keys are undefined */
};



#define INPUT_BUF 128
struct {
//  struct spinlock lock;
  char buf[INPUT_BUF];
  uint r;  // Read index
  uint w;  // Write index
  uint e;  // Edit index
} input;

/* current cursor location */
unsigned int current_loc = 0;
/* video memory begins at address 0xb8000 */
char *vidptr = (char*)0xb8000;

void kb_init(void){
    /* 0xFD is 11111101 - enables only IRQ1 (keyboard)*/
    write_port(0x21 , 0xFD);
}

void kprint(const char *str){
    unsigned int i = 0;
    while (str[i] != '\0') {
        vidptr[current_loc++] = str[i++];
        vidptr[current_loc++] = 0x07;
    }
}

void kprint_newline(void){
    unsigned int line_size = BYTES_FOR_EACH_ELEMENT * COLUMNS_IN_LINE;
    current_loc = current_loc + (line_size - current_loc % (line_size));
}

void clear_screen(void){
    unsigned int i = 0;
    while (i < SCREENSIZE) {
        vidptr[i++] = ' ';
        vidptr[i++] = 0x07;
    }
}
//void cgaputc(int c){
//  int pos;
//  
//  // Cursor position: col + 80*row.
//  write_port(CRTPORT, 14);
//  pos = read_port(CRTPORT+1) << 8;
//  write_port(CRTPORT, 15);
//  pos |= read_port(CRTPORT+1);
//
//  if(c == '\n')
//    pos += 80 - pos%80;
//  else if(c == BACKSPACE){
//    if(pos > 0) --pos;
//  } else
//    vidptr[pos++] = (c&0xff) | 0x0700;  // black on white
//  
////  if((pos/80) >= 24){  // Scroll up.
////    memmove(crt, crt+80, sizeof(crt[0])*23*80);
////    pos -= 80;
////    memset(crt+pos, 0, sizeof(crt[0])*(24*80 - pos));
////  }
//  
//  write_port(CRTPORT, 14);
//  write_port(CRTPORT+1, pos>>8);
//  write_port(CRTPORT, 15);
//  write_port(CRTPORT+1, pos);
//  vidptr[pos] = ' ' | 0x0700;
//}
void keyboard_handler_main(void){
    unsigned char status;
    char keycode;

    /* write EOI */
    write_port(0x20, 0x20);

    status = read_port(KEYBOARD_STATUS_PORT);
    /* Lowest bit of status will be set if buffer is not empty */
    if (status & 0x01) {
        keycode = read_port(KEYBOARD_DATA_PORT);
        if(keycode < 0)
            return;

        if(keycode == ENTER_KEY_CODE) {
            kprint_newline();
            kprint("$:");
            return;
        }
//        cgaputc(keyboard_map[(unsigned char) keycode]);
        vidptr[current_loc++] = keyboard_map[(unsigned char) keycode];
        vidptr[current_loc++] = 0x07;
    }
}



#endif	// OLDCONSOLE_H

