


assembly:
	nasm -f elf32 kernel.asm -o kasm.o

compile:assembly
	gcc -fno-stack-protector -m32 -c kernel.c -o kc.o
	
link:compile
	ld -m elf_i386 -T link.ld -o kernel kasm.o kc.o
	
qemu:link
	qemu-system-i386 -kernel kernel
