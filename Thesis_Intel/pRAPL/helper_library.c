
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h> /* its include core_num() function */
#include <math.h>
#include <stdint.h>
#include <string.h>
#include<inttypes.h>





uint32_t hello_eax(uint32_t eax,uint32_t ecx){
	//eax = 0x01;

	uint32_t eax_out;
	uint32_t ebx_out;
	uint32_t ecx_out;
	uint32_t edx_out;
	__asm__ volatile ("cpuid" : "=a" (eax_out),"=b" (ebx_out),"=c" (ecx_out),"=d" (edx_out) : "0" (eax), "2" (ecx));
	return eax_out;
}

uint32_t hello_ebx(uint32_t eax,uint32_t ecx){
	//eax = 0x01;

	uint32_t eax_out;
	uint32_t ebx_out;
	uint32_t ecx_out;
	uint32_t edx_out;
	__asm__ volatile ("cpuid" : "=a" (eax_out),"=b" (ebx_out),"=c" (ecx_out),"=d" (edx_out) : "0" (eax), "2" (ecx));
	return ebx_out;
}

uint32_t hello_ecx(uint32_t eax,uint32_t ecx){
	//eax = 0x01;

	uint32_t eax_out;
	uint32_t ebx_out;
	uint32_t ecx_out;
	uint32_t edx_out;
	__asm__ volatile ("cpuid" : "=a" (eax_out),"=b" (ebx_out),"=c" (ecx_out),"=d" (edx_out) : "0" (eax), "2" (ecx));
	return ecx_out;
}

uint32_t hello_edx(uint32_t eax,uint32_t ecx){
	//eax = 0x01;

	uint32_t eax_out;
	uint32_t ebx_out;
	uint32_t ecx_out;
	uint32_t edx_out;
	__asm__ volatile ("cpuid" : "=a" (eax_out),"=b" (ebx_out),"=c" (ecx_out),"=d" (edx_out) : "0" (eax), "2" (ecx));
	return edx_out;
}



/*int core_num(int x) {
	 x = sysconf(_SC_NPROCESSORS_CONF);
	return 0;
}

*/


typedef struct cpuid_info_t {
	uint32_t eax;
	uint32_t ebx;
	uint32_t ecx;
	uint32_t edx;
} cpuid_info_t;

uint32_t cpuid_eax(uint32_t eax_in, uint32_t ecx_in) {
	cpuid_info_t *ci;
	 asm (
#if defined(__LP64__)           /* 64-bit architecture */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
#else                           /* 32-bit architecture */
	     "pushl %%ebx;"          /* save ebx */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
	     "popl %%ebx;"           /* restore ebx */
#endif
             : "=a"(ci->eax), [ebx] "=r"(ci->ebx), "=c"(ci->ecx), "=d"(ci->edx)
             : "a"(eax_in), "c"(ecx_in)
        );

	return ci->eax;


}


uint32_t cpuid_ebx(uint32_t eax_in, uint32_t ecx_in) {
	cpuid_info_t *ci;
	 asm (
#if defined(__LP64__)           /* 64-bit architecture */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
#else                           /* 32-bit architecture */
	     "pushl %%ebx;"          /* save ebx */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
	     "popl %%ebx;"           /* restore ebx */
#endif
             : "=a"(ci->eax), [ebx] "=r"(ci->ebx), "=c"(ci->ecx), "=d"(ci->edx)
             : "a"(eax_in), "c"(ecx_in)
        );

	return ci->ebx;


}



uint32_t cpuid_ecx(uint32_t eax_in, uint32_t ecx_in) {
	cpuid_info_t *ci;
	 asm (
#if defined(__LP64__)           /* 64-bit architecture */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
#else                           /* 32-bit architecture */
	     "pushl %%ebx;"          /* save ebx */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
	     "popl %%ebx;"           /* restore ebx */
#endif
             : "=a"(ci->eax), [ebx] "=r"(ci->ebx), "=c"(ci->ecx), "=d"(ci->edx)
             : "a"(eax_in), "c"(ecx_in)
        );

	return ci->ecx;


}



uint32_t cpuid_edx(uint32_t eax_in, uint32_t ecx_in) {
	cpuid_info_t *ci;
	 asm (
#if defined(__LP64__)           /* 64-bit architecture */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
#else                           /* 32-bit architecture */
	     "pushl %%ebx;"          /* save ebx */
	     "cpuid;"                /* execute the cpuid instruction */
	     "movl %%ebx, %[ebx];"   /* save ebx output */
	     "popl %%ebx;"           /* restore ebx */
#endif
             : "=a"(ci->eax), [ebx] "=r"(ci->ebx), "=c"(ci->ecx), "=d"(ci->edx)
             : "a"(eax_in), "c"(ecx_in)
        );

	return ci->edx;


}





/*void open_file(char * msr_filename){
	int x = open("/dev/cpu/0/msr", O_RDWR);
	//return x;
}*/
















