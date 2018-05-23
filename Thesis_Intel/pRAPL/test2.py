#!/usr/bin/env python
# library and tool to access Intel MSRs (model specific registers)
import glob
import struct
import os
import math

#--------------------------------------------------------> from test.py <----------------------------------------------------------------------
msr_filename = "/dev/cpu/0/msr"
num_pkg = 1
cpu_model = 69
#--------------------------------------------------------> from test.py <----------------------------------------------------------------------


#Constants
MSR_RAPL_POWER_UNIT = 0x606
MSR_DRAM_ENERGY_UNIT =  0.000015
MSR_PKG_ENERGY_STATUS = 0x611
MSR_PP0_ENERGY_STATUS = 0x639
MSR_DRAM_ENERGY_STATUS	= 0x619
MSR_DRAM_ENERGY_UNIT  = 0.000015
MSR_PP1_ENERGY_STATUS = 0x641


#variables
power_unit = 0.0
energy_unit = 0.0
time_unit = 0.0
WRAPAROUND_VALUE = 0.0
rapl_energy_unit = 0.0
fd = 0


gpu_buffer = []
dram_buffer = []
cpu_buffer = []
package_buffer = []





def read_msr(position):
	global fd
	# Now read this file from the beginning
	os.lseek(fd, position ,os.SEEK_SET)
	val = struct.unpack('Q', os.read(fd, 8))[0]
	return val



def extractBitField(inField, width, offset):
	mask = ~0
	if ((offset+width) == 64) :
		bitMask = (mask<<offset)
	else:
		bitMask = (mask<<offset) ^ (mask<<(offset+width))
	outField = (inField & bitMask) >> offset
	return outField



#/*Get unit information to be multiplied with */
def get_msr_unit( data) :
	global power_unit
	global energy_unit
	global time_unit
	power_bit = extractBitField(data, 4, 0)
	energy_bit = extractBitField(data, 5, 8)
	time_bit = extractBitField(data, 4, 16)


	power_unit = (1.0 / math.pow(2,power_bit))	
	energy_unit = (1.0 / math.pow(2,energy_bit))	
	time_unit = (1.0 / math.pow(2,time_bit))	










def EnergyStatCheck() :
	global rapl_energy_unit 
	global num_pkg
	global fd 
	global MSR_DRAM_ENERGY_UNIT 
	global MSR_PKG_ENERGY_STATUS 
	global MSR_PP0_ENERGY_STATUS
	global MSR_DRAM_ENERGY_STATUS
	global MSR_DRAM_ENERGY_UNIT 
	global MSR_PP1_ENERGY_STATUS 

	global gpu_buffer 
	global dram_buffer 
	global cpu_buffer 
	global package_buffer 

	#double package[num_pkg];
	#double pp0[num_pkg];
	#double pp1[num_pkg];
	#double dram[num_pkg];

	package = []
	pp0 = []
	pp1 = []
	dram = []

	result = 0.0
	info_size = 0

	for i in range(0,num_pkg) : 

		result = read_msr( MSR_PKG_ENERGY_STATUS)
		print "result : %d"%result
		package.append(float(result * rapl_energy_unit))

		result = read_msr(MSR_PP0_ENERGY_STATUS)
		pp0.append(float(result * rapl_energy_unit))


		#msg1 = "%f"%package[i]
		package_buffer.append(package[i])
		#msg1 = "%f"%pp0[i]
		cpu_buffer.append(pp0[i]) 
		

		#print "package_buffer[i] : %s"%package_buffer[i]
		#print "package_buffer[i] : %s"%package_buffer[i]

		if cpu_model in (0x2D,0x3C,0x45,0x46,0x3F,0x4E,0x5E,0xD4,0x4F) :
				result = read_msr(MSR_DRAM_ENERGY_STATUS)
				if cpu_model == 0xD4 or cpu_model == 0x4F: 
					dram.append(float(result*MSR_DRAM_ENERGY_UNIT))
				else :
					dram.append(float(result*rapl_energy_unit))
				
				#msg1 ="%f"%dram[i]
				dram_buffer.append(dram[i]) 

				#info_size += strlen(package_buffer[i]) + strlen(dram_buffer[i]) + strlen(cpu_buffer[i]) + 4;	
		elif cpu_model in (0x2A,0x3A) :
				result = read_msr(MSR_PP1_ENERGY_STATUS)
				pp1.append(float(result *rapl_energy_unit))

				#msg1 ="%f"%pp1[i]
				gpu_buffer.append(p1[i])

				#info_size += strlen(package_buffer[i]) + strlen(gpu_buffer[i]) + strlen(cpu_buffer[i]) + 4;	
				
		






fd = os.open(msr_filename, os.O_RDONLY)
val = read_msr(MSR_RAPL_POWER_UNIT)



#For getting rapl_energy_unit
get_msr_unit(val)
WRAPAROUND_VALUE = int(1.0 / energy_unit)
rapl_energy_unit = energy_unit



n = 20000;

#Energy info. before running code block
EnergyStatCheck()

#-------------------------------------------> Write code here (down) <-----------------------------

num = []

for i in range(0,n):
	num.append(i)


for i in range (0,n-1):
	for j in range(i+1,n):
		if num[i]>num[j]:
			temp = num[i]
			num[i] = num[j]
			num[j] = temp

#-------------------------------------------> Write code here (up)<-----------------------------
		
##Energy info. after running code block	
EnergyStatCheck()




print "Power consumption of dram: %f"%((dram_buffer[1]-dram_buffer[0])/10.0)
print "power consumption of cpu:  %f"%((cpu_buffer[1]-cpu_buffer[0])/10.0)
print "power consumption of package: %f"%((package_buffer[1]-package_buffer[0])/10.0)



















