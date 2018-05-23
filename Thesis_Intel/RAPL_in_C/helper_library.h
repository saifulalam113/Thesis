#ifndef HELPER_LIBRARY
#define HELPER_LIBRARY

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h> /* its include core_num() function */
#include <math.h>
#include <stdint.h>
#include <string.h>
#include<inttypes.h>
#include <iostream>
#include <deque>
#include <list>
#include<vector>
#include <map>
#include <set>
















/*------------------------------------------------------------------->arch_spec.c<----------------------------------------------------*/

/**
 * Reference Intel ® 64 and IA-32 Architectures Software Developer’s Manual
 * for those CPUID information (December 2016)
 *
 * Table: CPUID Signature Values of DisplayFamily_DisplayModel
 */
#define SANDYBRIDGE          0x2AU
#define SANDYBRIDGE_EP       0x2DU
#define IVYBRIDGE            0x3AU
#define SKYLAKE1             0x4EU
#define SKYLAKE2             0x5EU
#define HASWELL1			 0x3CU
#define HASWELL2			 0x45U
#define HASWELL3			 0x46U
#define HASWELL_EP			 0x3FU
#define BROADWELL			       0xD4U
#define BROADWELL2	     0x4FU

#define CPUID                              \
    __asm__ volatile ("cpuid"                             \
			: "=a" (eax),     \
			"=b" (ebx),     \
			"=c" (ecx),     \
			"=d" (edx)      \
			: "0" (eax), "2" (ecx))


typedef struct APIC_ID_t {
	uint64_t smt_id;
	uint64_t core_id;
	uint64_t pkg_id;
	uint64_t os_id;
} APIC_ID_t;

typedef struct cpuid_info_t {
	uint32_t eax;
	uint32_t ebx;
	uint32_t ecx;
	uint32_t edx;
} cpuid_info_t;



uint32_t eax, ebx, ecx, edx;
uint32_t cpu_model;

int read_time = 0;
uint64_t max_pkg = 0;
uint64_t num_core_thread = 0; 
uint64_t num_pkg_thread = 0;  
uint64_t num_pkg_core = 0; 
int num_pkg = 0; 
int core = 0;
int coreNum = 0;

cpuid_info_t cpu_info;



/*-------------------------------------------------------------------> arch_spec.c <----------------------------------------------------*/




/*-------------------------------------------------------------------> msr.c <----------------------------------------------------*/

/**
 * Reference Intel ® 64 and IA-32 Architectures Software Developer’s Manual
 * for those CPUID information (December 2016)
 */
#define MSR_RAPL_POWER_UNIT		0x606

/**Energy measurement**/
#define MSR_PP0_ENERGY_STATUS		0x639
#define MSR_PP1_ENERGY_STATUS       0x641
#define MSR_PKG_ENERGY_STATUS		0x611
#define MSR_DRAM_ENERGY_STATUS		0x619

/**Power/time window maximum/minimum information(Only support for PKG and DRAM **/
#define MSR_PKG_POWER_INFO		0x614
#define MSR_DRAM_POWER_INFO     0x61C

/**Power limit**/
#define MSR_PKG_POWER_LIMIT        0x610
#define MSR_DRAM_POWER_LIMIT        0x618
#define MSR_PP0_POWER_LIMIT        0x638
#define MSR_PP1_POWER_LIMIT        0x640

/*Power domains*/
#define PKG_DOMAIN	0
#define DRAM_DOMAIN	1

/*Power limit set*/
#define DISABLE 0
#define ENABLE 1

/***global variable***/
typedef struct rapl_msr_unit {
	double power;
	double energy;
	double time;
} rapl_msr_unit;

typedef struct rapl_msr_parameter {
	double thermal_spec_power;
	double min_power;
	double max_power;
	double max_time_window;
} rapl_msr_parameter; 

typedef struct rapl_msr_power_limit_t {
	double power_limit;
	/* time_window_limit = 2^Y*F 
	 * F(23:22) Y(21:17)
	 */
	double time_window_limit; 
	uint64_t clamp_enable;
	uint64_t limit_enable;
	uint64_t lock_enable; 
} rapl_msr_power_limit_t;


/*extern char *ener_info;
extern rapl_msr_unit rapl_unit;
extern int *fd;
extern double WRAPAROUND_VALUE;
extern rapl_msr_parameter *parameters;*/


char *ener_info;
rapl_msr_unit rapl_unit;
int *fd;
double WRAPAROUND_VALUE;
rapl_msr_parameter *parameters;


typedef enum { 
	MINIMUM_POWER_LIMIT = 0,
	MAXIMUM_POWER_LIMIT,
	COSTOM_POWER_LIMIT
} msr_power_set;

typedef enum { 
	NA = 0,
	MAXIMUM_TIME_WINDOW,
	COSTOM_TIME_WINDOW
} msr_time_window_set;
#define _2POW(e)	\
((e == 0) ? 1 : (2 << (e - 1)))



//factor of F for time_window_limit. It represents these four value.
double F_arr[4] = {1.0, 1.1, 1.2, 1.3};













#define MSR_DRAM_ENERGY_UNIT 0.000015



/*-------------------------------------------------------------------> Function( Begin ) <----------------------------------------------------*/


int core_num() {
	return sysconf(_SC_NPROCESSORS_CONF);
}




void get_cpu_model(void)
{
    uint32_t eax = 0x01;
	CPUID;
    cpu_model = (((eax>>16)&0xFU)<<4) + ((eax>>4)&0xFU);
}




void cpuid(uint32_t eax_in, uint32_t ecx_in, cpuid_info_t *ci) {
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
}






cpuid_info_t getProcessorTopology(uint32_t level) {
	cpuid_info_t info;
	cpuid(0xb, level, &info);
	return info;	
}

int getSocketNum() {
	int i;
	uint32_t level1 = 0;
	uint32_t level2 = 1;
	cpuid_info_t info_l0;
	cpuid_info_t info_l1;
	coreNum = core_num();

	info_l0 = getProcessorTopology(level1);
	info_l1 = getProcessorTopology(level2);
	
	num_core_thread = info_l0.ebx & 0xffff;
	num_pkg_thread = info_l1.ebx & 0xffff;

	num_pkg_core = num_pkg_thread / num_core_thread;
	num_pkg = coreNum / num_pkg_thread;

	//printf("num_pkg_thread : %d\n",num_pkg_thread);
	//printf("num_core_thread : %d\n",num_core_thread);
	//printf("num_pkg_core = num_pkg_thread / num_core_thread : %d\n",num_pkg_core);
	//printf("coreNum : %d\n",coreNum);
	//printf("num_pkg = coreNum / num_pkg_thread : %d\n\n",num_pkg);
	//printf("num_pkg_thread: %d, num_core_thread: %d, coreNum: %d, num_pkg: %d\n", num_pkg_thread, num_core_thread, coreNum, num_pkg);

	return num_pkg;


}




uint64_t
extractBitField(uint64_t inField, uint64_t width, uint64_t offset)
{
	uint64_t mask = ~0;
	uint64_t bitMask;
	uint64_t outField;

	if ((offset+width) == 64) 
	{
		bitMask = (mask<<offset);
	}
	else 
	{
		bitMask = (mask<<offset) ^ (mask<<(offset+width));

	}

	outField = (inField & bitMask) >> offset;
	return outField;
}




uint64_t read_msr(int fd, uint64_t which) {

	uint64_t data = 0;

	if ( pread(fd, &data, sizeof data, which) != sizeof data ) {
	  printf("pread error!\n");
	}
	
	return data;
}





/*Get unit information to be multiplied with */
void get_msr_unit(rapl_msr_unit *unit_obj, uint64_t data) {

	uint64_t power_bit = extractBitField(data, 4, 0);
	uint64_t energy_bit = extractBitField(data, 5, 8);
	uint64_t time_bit = extractBitField(data, 4, 16);

	//printf("\n(1.0 / _2POW(power_bit) : %f\n",(1.0 / _2POW(power_bit)));
	//printf("\n(1.0 / _2POW(energy_bit) : %f\n",(1.0 / _2POW(energy_bit)));
	//printf("\n(1.0 / _2POW(time_bit) : %f\n",(1.0 / _2POW(time_bit)));

	unit_obj->power = (1.0 / _2POW(power_bit));	
	unit_obj->energy = (1.0 / _2POW(energy_bit));	
	unit_obj->time = (1.0 / _2POW(time_bit));	
}






/*Get wraparound value in order to prevent nagetive value*/
void 
get_wraparound_energy(double energy_unit) {
	WRAPAROUND_VALUE = 1.0 / energy_unit;
}








int ProfileInit() {
	//intArray result;
	int i;
	char msr_filename[BUFSIZ];

	get_cpu_model();	
	getSocketNum();

	int wraparound_energy;

	/*only two domains are supported for parameters check*/
	parameters = (rapl_msr_parameter *)malloc(2 * sizeof(rapl_msr_parameter));
	fd = (int *) malloc(num_pkg * sizeof(int));


	//printf("\ncore : %d\n",core);
	for(i = 0; i < num_pkg; i++) {
		if(i > 0) {
			core += num_pkg_thread / 2; 	//measure the first core of each package
		}
		sprintf(msr_filename, "/dev/cpu/%d/msr", core);
		//printf("\nmsr_filename : %s\n",msr_filename);
		//printf("\n\n/dev/cpu/%d/msr	and num_pkg: %d 	num_pkg_thread:%d\n\n",core,num_pkg,num_pkg_thread);
		fd[i] = open(msr_filename, O_RDWR);
		//printf("fd[%d] : %d\n",i,fd[i]);
	}

	uint64_t unit_info= read_msr(fd[0], MSR_RAPL_POWER_UNIT);
	get_msr_unit(&rapl_unit, unit_info);
	get_wraparound_energy(rapl_unit.energy);
	wraparound_energy = (int)WRAPAROUND_VALUE;

	//printf("unit_info : %d\n",unit_info);
	//printf("open core: %d\n", core);
	//printf("ProfileInit : function definition\n");

	return wraparound_energy;
}



int GetSocketNum() {
	return (int)getSocketNum();
}




void
initialize_energy_info(char gpu_buffer[][60], char dram_buffer[][60], char cpu_buffer[][60], char package_buffer[][60]) {

	double package[num_pkg];
	double pp0[num_pkg];
	double pp1[num_pkg];
	double dram[num_pkg];
	double result = 0.0;
	int info_size = 0;
	int i = 0;
	for (; i < num_pkg; i++) {

		result = read_msr(fd[i], MSR_PKG_ENERGY_STATUS);	//First 32 bits so don't need shift bits.
		//printf("result : %f\n",result);
		package[i] = (double) result * rapl_unit.energy;

		result = read_msr(fd[i], MSR_PP0_ENERGY_STATUS);
		pp0[i] = (double) result * rapl_unit.energy;

		//printf("package energy: %f\n", package[i]);

		sprintf(package_buffer[i], "%f", package[i]);
		sprintf(cpu_buffer[i], "%f", pp0[i]);
		
		//allocate space for string
		//printf("%" PRIu32 "\n", cpu_model);
		switch(cpu_model) {
			case SANDYBRIDGE_EP:
			case HASWELL1:
			case HASWELL2:
			case HASWELL3:
			case HASWELL_EP:
			case SKYLAKE1:
			case SKYLAKE2:
			case BROADWELL:
			case BROADWELL2:
	
				result = read_msr(fd[i],MSR_DRAM_ENERGY_STATUS);
				if (cpu_model == BROADWELL || cpu_model == BROADWELL2) {
					dram[i] =(double)result*MSR_DRAM_ENERGY_UNIT;
				} else {
					dram[i] =(double)result*rapl_unit.energy;
				}

				sprintf(dram_buffer[i], "%f", dram[i]);

				info_size += strlen(package_buffer[i]) + strlen(dram_buffer[i]) + strlen(cpu_buffer[i]) + 4;	

				/*Insert socket number*/	
				
				break;
			case SANDYBRIDGE:
			case IVYBRIDGE:


				result = read_msr(fd[i],MSR_PP1_ENERGY_STATUS);
				pp1[i] = (double) result *rapl_unit.energy;

				sprintf(gpu_buffer[i], "%f", pp1[i]);

				info_size += strlen(package_buffer[i]) + strlen(gpu_buffer[i]) + strlen(cpu_buffer[i]) + 4;	
				
		}

		ener_info = (char *) malloc(info_size);
	}
}

char *  EnergyStatCheck() {
	//string ener_string;
	char gpu_buffer[num_pkg][60]; 
	char dram_buffer[num_pkg][60]; 
	char cpu_buffer[num_pkg][60]; 
	char package_buffer[num_pkg][60];
	int dram_num = 0L;
	int cpu_num = 0L;
	int package_num = 0L;
	int gpu_num = 0L;
	//construct a string
	//char *ener_info;
	int info_size;
	int i;
	int offset = 0;

	initialize_energy_info(gpu_buffer, dram_buffer, cpu_buffer, package_buffer);

	for(i = 0; i < num_pkg; i++) {
		//allocate space for string
		//printf("%" PRIu32 "\n", cpu_model);
		switch(cpu_model) {
			case SANDYBRIDGE_EP:
			case HASWELL1:
			case HASWELL2:
			case HASWELL3:
			case HASWELL_EP:
			case SKYLAKE1:
			case SKYLAKE2:
			case BROADWELL:
			case BROADWELL2:

				//copy_to_string(ener_info, dram_buffer, dram_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				/*Insert socket number*/	
				dram_num = strlen(dram_buffer[i]);
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);
				
				memcpy(ener_info + offset, &dram_buffer[i], dram_num);
				//split sigh
				ener_info[offset + dram_num] = '#';
				memcpy(ener_info + offset + dram_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + dram_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += dram_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + dram_num + cpu_num + 2, &package_buffer[i], package_num + 1);
				}
				
				break;	
			case SANDYBRIDGE:
			case IVYBRIDGE:

				gpu_num = strlen(gpu_buffer[i]);		
				cpu_num = strlen(cpu_buffer[i]);
				package_num = strlen(package_buffer[i]);

				//copy_to_string(ener_info, gpu_buffer, gpu_num, cpu_buffer, cpu_num, package_buffer, package_num, i, &offset);
				memcpy(ener_info + offset, &gpu_buffer[i], gpu_num);
				//split sign
				ener_info[offset + gpu_num] = '#';
				memcpy(ener_info + offset + gpu_num + 1, &cpu_buffer[i], cpu_num);
				ener_info[offset + gpu_num + cpu_num + 1] = '#';
				if(i < num_pkg - 1) {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i], package_num);
					offset += gpu_num + cpu_num + package_num + 2;
					if(num_pkg > 1) {
						ener_info[offset] = '@';
						offset++;
					}
				} else {
					memcpy(ener_info + offset + gpu_num + cpu_num + 2, &package_buffer[i],
							package_num + 1);
				}
				
				break;
		default:
				printf("non of archtectures are detected\n");
				break;


		}
	}

	//ener_string = (*env)->NewStringUTF(env, ener_info);	
	//free(ener_info);
	return ener_info;

}
void ProfileDealloc
   () {
	int i;
	free(fd);	
	free(parameters);
}



/*-------------------------------------------------------------------> Function( End ) <----------------------------------------------------*/



/*
void 
parse_apic_id(cpuid_info_t info_l0, cpuid_info_t info_l1, APIC_ID_t *my_id){

	// Get the SMT ID
	uint64_t smt_mask_width = info_l0.eax & 0x1f;
	uint64_t smt_mask = ~((-1) << smt_mask_width);
	my_id->smt_id = info_l0.edx & smt_mask;

	// Get the core ID
	uint64_t core_mask_width = info_l1.eax & 0x1f;
	uint64_t core_mask = (~((-1) << core_mask_width ) ) ^ smt_mask;
	my_id->core_id = (info_l1.edx & core_mask) >> smt_mask_width;

	// Get the package ID
	uint64_t pkg_mask = (-1) << core_mask_width;
	my_id->pkg_id = (info_l1.edx & pkg_mask) >> core_mask_width;
}
*/






















/*
void
putBitField(uint64_t inField, uint64_t *data, uint64_t width, uint64_t offset)
{
	uint64_t mask = ~0;
	uint64_t bitMask;

	//The bits to be overwritten are located in the leftmost part.
	if ((offset+width) == 64) 
    {
        bitMask = (mask<<offset);
    } else {
		bitMask = (mask<<offset) ^ (mask<<(offset + width));
	}
	//Reset the bits that will be overwritten to be 0, and keep other bits the same.
	*data = ~bitMask & *data;	
	*data = *data | (inField<<offset);
}
*/








/*
void write_msr(int fd, uint64_t which, uint64_t limit_info) {
	if ( pwrite(fd, &limit_info , sizeof limit_info, which) != sizeof limit_info) 
	  printf("pwrite error!\n");
}
*/






/*
double calc_time_window(uint64_t Y, uint64_t F) {
	return _2POW(Y) * F_arr[F] * rapl_unit.time;
}
*/






/*
void 
calc_y(uint64_t *Y, double F, double custm_time) {
	*Y = log2(custm_time / rapl_unit.time / F);
}
*/






/*
rapl_msr_power_limit_t
get_specs(int fd, uint64_t addr) {
	uint64_t msr;
	rapl_msr_power_limit_t limit_info;
	msr = read_msr(fd, addr);	
	limit_info.power_limit = rapl_unit.power * extractBitField(msr, 14, 0);
	limit_info.time_window_limit = calc_time_window(extractBitField(msr, 5, 17), extractBitField(msr, 2, 22));
	limit_info.clamp_enable = extractBitField(msr, 1, 16);
	limit_info.limit_enable = extractBitField(msr, 1, 15);
	limit_info.lock_enable = extractBitField(msr, 1, 63);
	return limit_info;
}
*/






/*
void
set_package_power_limit_enable(int fd, uint64_t setting, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);	

	//enable set #1
	putBitField(setting, &msr, 1, 15);
	//enable set #2
	putBitField(setting, &msr, 1, 47);
	write_msr(fd, addr, msr);
}
*/




/*
void
set_dram_power_limit_enable(int fd, uint64_t setting, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);	

	//enable set
	putBitField(setting, &msr, 1, 15);

	write_msr(fd, addr, msr);

}
*/




/*
void
set_package_clamp_enable(int fd, uint64_t addr) {
	uint64_t msr;
	msr = read_msr(fd, addr);	

	//clamp set #1
	putBitField(0, &msr, 1, 16);
	//clamp set #2
	putBitField(0, &msr, 1, 48);
	//putBitField(power_limit, &msr, 15, 32);

	write_msr(fd, addr, msr);

}
*/





/*
//This idea is loop four possible sets of Y and F, and in return to get 
//the time window, then use the set of Y and F that is smaller than but 
//closest to the customized time.
void 
convert_optimal_yf_from_time(uint64_t *Y, uint64_t *F, double custm_time) {
	uint64_t temp_y;
	double time_window = 0.0;
	double delta = 0.0;
	double smal_delta = 5000000000.0;
	int i = 0;
	for(i = 0; i < 4; i++) {
		calc_y(&temp_y, F_arr[i], custm_time);		
		time_window = calc_time_window(temp_y, i);
		delta = custm_time -time_window;
		//printf("Y is: %ld, F is: %d, time window: %f\n", temp_y, i, time_window);
		//printf("delta is: %f\n", delta);
		if(delta > 0 && delta < smal_delta) {
			smal_delta = delta;
			*Y = temp_y;
			*F = i;
		}
	}
}
*/





/*
void
set_pkg_time_window_limit(int fd, uint64_t addr, double custm_time) {
	uint64_t msr;
	uint64_t Y;
	uint64_t F;
	msr = read_msr(fd, addr);	
	//Set the customized time window.
	convert_optimal_yf_from_time(&Y, &F, custm_time);

	//Keep everything else the same.
	//#1 time window bits
	putBitField(F, &msr, 2, 22);
	putBitField(Y, &msr, 5, 17);
	//#2 time window bits
	putBitField(F, &msr, 2, 54);
	putBitField(Y, &msr, 5, 49);

	write_msr(fd, addr, msr);

}
*/




/*
void
set_dram_time_window_limit(int fd, uint64_t addr, double custm_time) {
	uint64_t msr;
	uint64_t Y;
	uint64_t F;
	msr = read_msr(fd, addr);	
	//Set the customized time window.
	convert_optimal_yf_from_time(&Y, &F, custm_time);

	//Keep everything else the same.
	//#1 time window bits
	putBitField(F, &msr, 2, 22);
	putBitField(Y, &msr, 5, 17);

	write_msr(fd, addr, msr);
}
*/




/*
void
set_pkg_power_limit(int fd, uint64_t addr, double custm_power) {
	uint64_t msr;
	msr = read_msr(fd, addr);	
	//Set the customized power.
	uint64_t power_limit = custm_power / rapl_unit.power;
	//Keep everything else the same.
	putBitField(power_limit, &msr, 15, 0);
	putBitField(power_limit, &msr, 15, 32);

	write_msr(fd, addr, msr);

}
*/



/*
void
set_dram_power_limit(int fd, uint64_t addr, double custm_power) {
	uint64_t msr;
	msr = read_msr(fd, addr);	
	//Set the customized power.
	uint64_t power_limit = custm_power / rapl_unit.power;
	//Keep everything else the same.
	putBitField(power_limit, &msr, 15, 0);
//	putBitField(power_limit, &msr, 15, 32);

	write_msr(fd, addr, msr);

}
*/









/*
void
get_rapl_pkg_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	get_rapl_parameters(fd, MSR_PKG_POWER_INFO, (rapl_msr_unit *)unit_obj, (rapl_msr_parameter *)paras);
}
*/




/*
void
get_rapl_dram_parameters(int fd, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	get_rapl_parameters(fd, MSR_DRAM_POWER_INFO, (rapl_msr_unit *)unit_obj, (rapl_msr_parameter *)paras);
}
*/






/*
void 
get_rapl_parameters(int fd, uint64_t msr_addr, rapl_msr_unit *unit_obj, rapl_msr_parameter *paras) {
	uint64_t thermal_spec_power;
	uint64_t max_power;
	uint64_t min_power;
	uint64_t max_time_window;
	uint64_t power_info;

	power_info = read_msr(fd, msr_addr);

	thermal_spec_power = extractBitField(power_info, 15, 0);
	min_power = extractBitField(power_info, 15, 16);
	max_power = extractBitField(power_info, 15, 32);
	max_time_window = extractBitField(power_info, 6, 48);


	paras->thermal_spec_power = unit_obj->power * thermal_spec_power;
	paras->min_power = unit_obj->power * min_power;
	paras->max_power = unit_obj->power * max_power;
	paras->max_time_window = unit_obj->time * max_time_window;
}
*/




/*
void 
getPowerSpec(double result[4], rapl_msr_parameter *parameter, int domain) {

	int i;
	//Test use
	
	//printf("thermal specification power is: %f, minimum power limit is: %f, maximum power limit is: %f, maximum time window is: %f\n", 		//parameters[domain].thermal_spec_power, parameters[domain].min_power, parameters[domain].max_power, parameters	    			  //[domain].max_time_window);
		
	for(i = 0; i < 4; i++) {
		result[0] = parameters[domain].thermal_spec_power;	
		result[1] = parameters[domain].min_power;	
		result[2] = parameters[domain].max_power;	
		result[3] = parameters[domain].max_time_window;	
	}
}
*/












/*
void copy_to_string(char *ener_info, char uncore_buffer[60], int uncore_num, char cpu_buffer[60], int cpu_num, char package_buffer[60], int package_num, int i, int *offset) {
	memcpy(ener_info + *offset, &uncore_buffer, uncore_num);
	//split sigh
	ener_info[*offset + uncore_num] = '#';
	memcpy(ener_info + *offset + uncore_num + 1, &cpu_buffer, cpu_num);
	ener_info[*offset + uncore_num + cpu_num + 1] = '#';
	if(i < num_pkg - 1) {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num);
		offset += uncore_num + cpu_num + package_num + 2;
		if(num_pkg > 1) {
			ener_info[*offset] = '@';
			offset++;
		}
	} else {
		memcpy(ener_info + *offset + uncore_num + cpu_num + 2, &package_buffer, package_num + 1);
	}

}
*/












#endif


