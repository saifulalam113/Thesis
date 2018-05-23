import ctypes
import numpy
import glob
import struct
import os

testlib = ctypes.cdll.LoadLibrary('helper_library.so')
#testlib.__asm__ volatile ( "addl %%ebx, %%eax;" : "=a" (add) : "a" (5) , "b" (10) )

eax = 0
ebx = 0
ecx = 0
edx = 0



cpuid_info_t_info_l0_eax = 5
cpuid_info_t_info_l0_ebx = 0
cpuid_info_t_info_l0_ecx = 0
cpuid_info_t_info_l0_edx = 0


cpuid_info_t_info_l1_eax = 0
cpuid_info_t_info_l1_ebx = 0
cpuid_info_t_info_l1_ecx = 0
cpuid_info_t_info_l1_edx = 0



num_core_thread = 0
num_pkg_thread = 0
num_pkg_core = 0
num_pkg = 0


coreNum = 0
cpu_model = 0
core = 0


def get_cpu_model():
	global eax
	global ebx
	global ecx
	global edx
	global cpu_model
	eax=testlib.hello_eax(0x01,ecx)
	ebx = testlib.hello_ebx(0x01,ecx)
	ecx = testlib.hello_ecx(0x01,ecx)
	edx = testlib.hello_edx(0x01,ecx) 
	cpu_model = (((eax>>16)&0xF)<<4) + ((eax>>4)&0xF)  
	return 

def core_num():
	global coreNum
	coreNum = os.sysconf('SC_NPROCESSORS_ONLN')
	return 




	
def getProcessorTopology_level0():
	global cpuid_info_t_info_l0_eax
	global cpuid_info_t_info_l0_ebx
	global cpuid_info_t_info_l0_ecx
	global cpuid_info_t_info_l0_edx
	cpuid_info_t_info_l0_eax = testlib.cpuid_eax(0xb,0)
	cpuid_info_t_info_l0_ebx = testlib.cpuid_ebx(0xb,0)
	cpuid_info_t_info_l0_ecx = testlib.cpuid_ecx(0xb,0)
	cpuid_info_t_info_l0_edx = testlib.cpuid_edx(0xb,0)
	return


def getProcessorTopology_level1():
	global cpuid_info_t_info_l1_eax
	global cpuid_info_t_info_l1_ebx
	global cpuid_info_t_info_l1_ecx
	global cpuid_info_t_info_l1_edx
	cpuid_info_t_info_l1_eax = testlib.cpuid_eax(0xb,1)
	cpuid_info_t_info_l1_ebx = testlib.cpuid_ebx(0xb,1)
	cpuid_info_t_info_l1_ecx = testlib.cpuid_ecx(0xb,1)
	cpuid_info_t_info_l1_edx = testlib.cpuid_edx(0xb,1)
	return



def getSocketNum():
	global num_pkg_thread
	global num_core_thread
	global num_pkg_core
	global num_pkg
	global coreNum
	getProcessorTopology_level0()
	getProcessorTopology_level1()
	num_core_thread = cpuid_info_t_info_l0_ebx & 0xffff
	num_pkg_thread = cpuid_info_t_info_l1_ebx & 0xffff
	num_pkg_core = num_pkg_thread / num_core_thread
	num_pkg = coreNum / num_pkg_thread

fd = []

def readmsr(msr ):
    if not os.path.exists("/dev/cpu/0/msr"):
        os.system("/sbin/modprobe msr")
    f = os.open('/dev/cpu/0/msr', os.O_RDONLY)
    os.lseek(f,msr, os.SEEK_SET)
    val = struct.unpack('Q', os.read(f, 8))[0]
    os.close(f)
    return val


def extractBitField(inField, width, offset):
	mask = ~0
	if ((offset+width) == 64) :
		bitMask = (mask<<offset)
	else:
		bitMask = (mask<<offset) ^ (mask<<(offset+width))
	outField = (inField & bitMask) >> offset
	return outField


def ProfileInit():
	global core
	global fd
	core_num()
	get_cpu_model() 
	getSocketNum()
	for i in range(0,num_pkg):
		if(i > 0) :
			core += num_pkg_thread / 2	
		
		msr_filename = "/dev/cpu/%d/msr"%core

		print "msr_filename : %s"%msr_filename
	
	


ProfileInit()

print "num_pkg : %d"%num_pkg

print "cpu_model : %d"%cpu_model
















