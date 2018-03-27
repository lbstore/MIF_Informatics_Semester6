/*
 * Tests the functionality of the `dup2` system call. The first argument is the
 * original file the we are duplicating. If a second argument is passed in we
 * open this file, write a string to it, then duplicate it. This tests that our
 * kernel is properly closing and duplicating the file. If the second argument
 * is not passed in, we simply duplicate the original file to descriptor 10. In
 * both cases the original file should have the string "foobar\n". If not, then
 * something went wrong
 */

#include "types.h"
#include "stat.h"
#include "user.h"
#include "fcntl.h"

int
main(int argc, char *argv[]) {
    int origfd, newfd;

    if (argc < 2) {
      printf(2, "%s: Not enough arguments\n", argv[0]);
      printf(2, "Usage: origfile [newfile]\n");
      exit();
    }

    unlink(argv[1]);

    if ((origfd = open(argv[1], O_CREATE|O_RDWR)) < 0) {
      printf(2, "Cannot open '%s'\n", argv[2]);
      exit();
    }
    if (argc > 2) {
      unlink(argv[3]);
      if ((newfd = open(argv[2], O_CREATE|O_RDWR)) < 0) {
          printf(2, "Cannot open '%s'\n", argv[3]);
          exit();
      }
      write(newfd, "ignored\n", 8);
    } else {
      newfd = 10;
    }

    write(origfd, "foo", 3);
    if (dup2(origfd, newfd) < 0) {
        printf(2, "dup2 error\n");
    }

    write(newfd, "bar\n", 4);

    exit();
}
