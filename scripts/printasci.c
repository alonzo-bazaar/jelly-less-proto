#include<stdio.h>

int main(int argc, char** argv) {
	if(argc == 2) {
		int x;
		sscanf(argv[1], "%d", &x);
		putchar(x);
	}
	else {
		puts("vaffanculo");
	}
	return 0;
}
