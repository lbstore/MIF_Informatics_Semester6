obdir = objects/
kernelDir=./kernelDir


kAssembly:
	nasm -f elf32 kernel.asm -o kasm.o

compile:kAssembly
	gcc -fno-stack-protector -m32 -c kernel.c
	
link:compile
	ld -m elf_i386 -T link.ld -o kernel.img kasm.o kernel.o
	
kernel:link
	
qemu:kernel
	qemu-system-i386 -kernel kernel.img

clean:
	rm -rf *.o kernel
	