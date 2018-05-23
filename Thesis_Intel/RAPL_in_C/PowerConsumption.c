#include"helper_library.h"

using namespace std;


int main(){
	
	int core_number;
	int wraparoundValue;
	int socketNum;
	float start_result_1,start_result_2,start_result_3;
	float end_result_1,end_result_2,end_result_3;



	


	//check get_cpu_model() function
	get_cpu_model();
	core_num();
	ProfileInit();




	char* msg1 = EnergyStatCheck();
	//-----------------------------------------> Enter code block here (down) <-----------------------------------------------------------//

	int i,j,k,n;
	n = 10000000;
	printf("deque:n:%d\n",n);
	deque <int> gquiz;
	for(i=0;i<n;i++){
		gquiz.push_front(i);
	    	gquiz.push_back(i);
	}

	for(i=0;i<n;i++){
		gquiz.pop_front();
	    	gquiz.pop_back();
	}


	
	/*multiset <int,greater <int> > gquiz;        // empty map container
	
	for(i=0;i<2*n;i++){
		gquiz.insert(i*10);
	}

	for(i=0;i<2*n;i++){
		gquiz.erase(i*10);
	}*/

	/*multimap <int, int> gquiz;        // empty map container
	
	for(i=0;i<2*n;i++){
		gquiz.insert(pair <int, int> (i, i*10));
	}

	for(i=0;i<2*n;i++){
		gquiz.erase(i);
	}*/
 
   
    
 	



	//-----------------------------------------> Enter code block here (up) <-----------------------------------------------------------//
	char* msg2 = EnergyStatCheck();
	ProfileDealloc();
	




	/*We have already got energy consumption info/*/
	printf("Energy consumption info. (before) : %s\n",	msg1);
	printf("Energy consumption info.  (after) : %s\n\n",	msg2);



	char* result = strtok(msg1, "#");
	start_result_1 = atof(result);

      	result = strtok(NULL, "#");
	start_result_2 = atof(result);

      	result = strtok(NULL, "#");
	start_result_3 = atof(result);


	result = strtok(msg2, "#");
	end_result_1 = atof(result);

      	result = strtok(NULL, "#");
	end_result_2 = atof(result);

      	result = strtok(NULL, "#");
	end_result_3 = atof(result);
	




	printf("Power consumption of dram: %f\n",(end_result_1-start_result_1)/10.0);
	printf("power consumption of cpu:  %f\n",(end_result_2-start_result_2)/10.0);
	printf("power consumption of package: %f\n",(end_result_3-start_result_3)/10.0);




	return 0;
}
