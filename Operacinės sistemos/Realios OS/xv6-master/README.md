xv6
===

This is my fork of the Xv6 operating system for studying operating systems. You
can view the original README at `README.orig`. The projects homepage is here:
http://pdos.csail.mit.edu/6.828/2014/xv6.html

Feel free to pull/copy any code from this repository. All source is licensed
under the MIT open source license. Please keep in my mind that I'm not a
professional kernel developer and am using this to learn more about hardware
and operating systems. As such, I'm not making any guarantees about the
correctness/design of the code.

#### Changes from original

- Source tree has been reorganized in the following manner:
  - Kernel source is in `sys`
  - User space libraries are in `lib`
  - User space programs are in `bin`
  - All headers (kernel and user space) are located in `include`
  - Documentation/handbook generation logic has been removed from the
    respository
- CPU scheduler will halt the cpu(s) when idle
- Adds the following system calls:
  - `dup2` : This mimics the functionality of Posix dup2
- Lazily allocates pages on page faults
