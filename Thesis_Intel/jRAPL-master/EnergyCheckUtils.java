import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.*;
public class EnergyCheckUtils {
	public native static int scale(int freq);
	public native static int[] freqAvailable();

	public native static double[] GetPackagePowerSpec();
	public native static double[] GetDramPowerSpec();
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);
	public native static int ProfileInit();
	public native static int GetSocketNum();
	public native static String EnergyStatCheck();
	public native static void ProfileDealloc();
	public native static void SetPowerLimit(int ENABLE);
	public static int wraparoundValue;

	public static int socketNum;
	static {
		System.setProperty("java.library.path", System.getProperty("user.dir"));
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) { }

		System.loadLibrary("CPUScaler");
		//System.out.println("Before calling : ProfileInit()\n");
		wraparoundValue = ProfileInit();
		//System.out.println("After calling : ProfileInit()\n");
		socketNum = GetSocketNum();
		System.out.println("hello");
	}

	/**
	 * @return an array of current energy information.
	 * The first entry is: Dram/uncore gpu energy(depends on the cpu architecture.
	 * The second entry is: CPU energy
	 * The third entry is: Package energy
	 */

	public static double[] getEnergyStats() {
		socketNum = GetSocketNum();
		String EnergyInfo = EnergyStatCheck();
		//System.out.println("getEnergyStats: EnergyInfo : "+EnergyInfo);
		/*One Socket*/
		if(socketNum == 1) {
			double[] stats = new double[3];
			String[] energy = EnergyInfo.split("#");

			stats[0] = Double.parseDouble(energy[0]);
			stats[1] = Double.parseDouble(energy[1]);
			stats[2] = Double.parseDouble(energy[2]);

			return stats;

		} else {
		/*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[] stats = new double[3*socketNum];
			int count = 0;


			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					count = i * 3 + j;	//accumulative count
					stats[count] = Double.parseDouble(energy[j]);
				}
			}
			return stats;  
		}

	}


	/* Merge Sort function */
	public static void sort(int[] a, int low, int high) 

	{

	int N = high - low;         

	if (N <= 1) 

	    return; 

	int mid = low + N/2; 

	// recursively sort 

	sort(a, low, mid); 

	sort(a, mid, high); 

	// merge two sorted subarrays

	int[] temp = new int[N];

	int i = low, j = mid;

	for (int k = 0; k < N; k++) 

	{

	    if (i == mid)  

		temp[k] = a[j++];

	    else if (j == high) 

		temp[k] = a[i++];

	    else if (a[j]<a[i]) 

		temp[k] = a[j++];

	    else 

		temp[k] = a[i++];

	}    

	for (int k = 0; k < N; k++) 

	    a[low + k] = temp[k];         

	}


	/*Heap sort function*/
	public static void heapify(int arr[],int n,int i){
		int largest = i;
		int l = 2*i + 1;
		int r = 2*i + 2;

		if(l<n && arr[l] > arr[largest])
			largest = l;

		if(r < n && arr[r] > arr[largest])
			largest = r;
		if(largest != i){
			int swap = arr[i];
			arr[i] = arr[largest];
			arr[largest] = swap;

			heapify(arr,n,largest);
		}
	}


	
	//----------------------------------------------------------- Function (Radix Sort) --------------------------------------------//
	// A utility function to get maximum value in arr[]
	static int getMax(int arr[], int n)
	{
		int mx = arr[0];
		for (int i = 1; i < n; i++)
		    if (arr[i] > mx)
			mx = arr[i];
		return mx;
	}

	// A function to do counting sort of arr[] according to
	// the digit represented by exp.
	static void countSort(int arr[], int n, int exp)
	{
		int output[] = new int[n]; // output array
		int i;
		int count[] = new int[10];
		Arrays.fill(count,0);

		// Store count of occurrences in count[]
		for (i = 0; i < n; i++)
		    count[ (arr[i]/exp)%10 ]++;

		// Change count[i] so that count[i] now contains
		// actual position of this digit in output[]
		for (i = 1; i < 10; i++)
		    count[i] += count[i - 1];

		// Build the output array
		for (i = n - 1; i >= 0; i--)
		{
		    output[count[ (arr[i]/exp)%10 ] - 1] = arr[i];
		    count[ (arr[i]/exp)%10 ]--;
		}

		// Copy the output array to arr[], so that arr[] now
		// contains sorted numbers according to curent digit
		for (i = 0; i < n; i++)
		    arr[i] = output[i];
	}

	// The main function to that sorts arr[] of size n using
	// Radix Sort
	static void radixsort(int arr[], int n)
	{
		// Find the maximum number to know number of digits
		int m = getMax(arr, n);

		// Do counting sort for every digit. Note that instead
		// of passing digit number, exp is passed. exp is 10^i
		// where i is current digit number
		for (int exp = 1; m/exp > 0; exp *= 10)
		    countSort(arr, n, exp);
	}



	//----------------------------------------------------------- Function (Quick Sort) --------------------------------------------//
	/* This function takes last element as pivot,
	places the pivot element at its correct
	position in sorted array, and places all
	smaller (smaller than pivot) to left of
	pivot and all greater elements to right
	of pivot */
	static int partition(int arr[], int low, int high)
	{
		int pivot = arr[high]; 
		int i = (low-1); // index of smaller element
		for (int j=low; j<high; j++)
		{
		    // If current element is smaller than or
		    // equal to pivot
		    if (arr[j] <= pivot)
		    {
			i++;

			// swap arr[i] and arr[j]
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		    }
		}

		// swap arr[i+1] and arr[high] (or pivot)
		int temp = arr[i+1];
		arr[i+1] = arr[high];
		arr[high] = temp;

		return i+1;
	}


	/* The main function that implements QuickSort()
	arr[] --> Array to be sorted,
	low  --> Starting index,
	high  --> Ending index */
	static void quickSort(int arr[], int low, int high)
	{
		if (low < high)
		{
		    /* pi is partitioning index, arr[pi] is 
		      now at right place */
		    int pi = partition(arr, low, high);

		    // Recursively sort elements before
		    // partition and after partition
		    quickSort(arr, low, pi-1);
		    quickSort(arr, pi+1, high);
		}
	}











 
	//----------------------------------------------------------- Function (main) --------------------------------------------//
	public static void main(String[] args) {


		int n =15000;
		int num[] = new int[n];
		double[] before = new double[3];
		double[] after = new double[3];



		before = getEnergyStats();
		//---------------------------------------------------------- Insert code block -----------------------------------------//

			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);


		//---------------------------------------------------------- Insert code block -----------------------------------------//
		after = getEnergyStats();
		ProfileDealloc();

		
		System.out.println("Power consumption of dram: " + (after[0] - before[0]) / 10.0 + "\npower consumption of cpu: " + 				(after[1] - before[1]) / 10.0 + "\npower consumption of package: " + (after[2] - before[2]) / 10.0);
		


		/*
		double[] bubble_sort_best_case_before = new double[15]; 
		double[] selection_sort_best_case_before = new double[15]; 
		double[] merge_sort_best_case_before = new double[15]; 
		double[] quick_sort_best_case_before = new double[15]; 
		double[] radix_sort_best_case_before = new double[15]; 
		double[] heap_sort_best_case_before = new double[15]; 

		double[] bubble_sort_best_case_after = new double[15]; 
		double[] selection_sort_best_case_after = new double[15]; 
		double[] merge_sort_best_case_after = new double[15]; 
		double[] quick_sort_best_case_after = new double[15]; 
		double[] radix_sort_best_case_after = new double[15]; 
		double[] heap_sort_best_case_after = new double[15]; 


		double[] bubble_sort_worst_case_before = new double[15]; 
		double[] selection_sort_worst_case_before = new double[15]; 
		double[] merge_sort_worst_case_before = new double[15]; 
		double[] quick_sort_worst_case_before = new double[15]; 
		double[] radix_sort_worst_case_before = new double[15]; 
		double[] heap_sort_worst_case_before = new double[15]; 

		double[] bubble_sort_worst_case_after = new double[15]; 
		double[] selection_sort_worst_case_after = new double[15]; 
		double[] merge_sort_worst_case_after = new double[15]; 
		double[] quick_sort_worst_case_after = new double[15]; 
		double[] radix_sort_worst_case_after = new double[15]; 
		double[] heap_sort_worst_case_after = new double[15]; 


		double[] bubble_sort_best_case_diff = new double[15]; 
		double[] selection_sort_best_case_diff = new double[15]; 
		double[] merge_sort_best_case_diff = new double[15]; 
		double[] quick_sort_best_case_diff = new double[15]; 
		double[] radix_sort_best_case_diff = new double[15]; 
		double[] heap_sort_best_case_diff = new double[15]; 

		
		double[] bubble_sort_worst_case_diff = new double[15]; 
		double[] selection_sort_worst_case_diff = new double[15]; 
		double[] merge_sort_worst_case_diff = new double[15]; 
		double[] quick_sort_worst_case_diff = new double[15]; 
		double[] radix_sort_worst_case_diff = new double[15]; 
		double[] heap_sort_worst_case_diff = new double[15]; 




		double[] bubble_sort_best_case_average = new double[3]; 
		double[] selection_sort_best_case_average = new double[3]; 
		double[] merge_sort_best_case_average = new double[3]; 
		double[] quick_sort_best_case_average = new double[3]; 
		double[] radix_sort_best_case_average = new double[3]; 
		double[] heap_sort_best_case_average = new double[3]; 


		double[] bubble_sort_worst_case_average = new double[3]; 
		double[] selection_sort_worst_case_average = new double[3]; 
		double[] merge_sort_worst_case_average = new double[3]; 
		double[] quick_sort_worst_case_average = new double[3]; 
		double[] radix_sort_worst_case_average = new double[3]; 
		double[] heap_sort_worst_case_average = new double[3]; 





		for(int x = 0;x<5;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		
		n = 1000;
		num = new int[n];

		for(int x = 0;x<5;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 5000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();







		n = 10000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();





		n = 20000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();


	

		n = 50000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();






		n = 100000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();






		 n = 200000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 300000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 400000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 500000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 600000;
		num = new int[n];
		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();


		n = 700000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();



		n = 800000;
		num = new int[n];

		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();




		n = 900000;
		num = new int[n];
		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();


		n = 1000000;
		num = new int[n];
		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();


		n = 1500000;
		num = new int[n];
		for(int x = 0;x<1;x++){
			before = getEnergyStats();
			//---------------------------------------------> Bubble sort <---------------------------------------------//

			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_best_case_before[x*3+0] = before[0];
			bubble_sort_best_case_before[x*3+1] = before[1];
			bubble_sort_best_case_before[x*3+2] = before[2];
			

			bubble_sort_best_case_after[x*3+0] = after[0];
			bubble_sort_best_case_after[x*3+1] = after[1];
			bubble_sort_best_case_after[x*3+2] = after[2];




			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			bubble_sort_worst_case_before[x*3+0] = before[0];
			bubble_sort_worst_case_before[x*3+1] = before[1];
			bubble_sort_worst_case_before[x*3+2] = before[2];
			

			bubble_sort_worst_case_after[x*3+0] = after[0];
			bubble_sort_worst_case_after[x*3+1] = after[1];
			bubble_sort_worst_case_after[x*3+2] = after[2];
		
		

			//---------------------------------------------> Selection sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Selection sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_best_case_before[x*3+0] = before[0];
			selection_sort_best_case_before[x*3+1] = before[1];
			selection_sort_best_case_before[x*3+2] = before[2];
			

			selection_sort_best_case_after[x*3+0] = after[0];
			selection_sort_best_case_after[x*3+1] = after[1];
			selection_sort_best_case_after[x*3+2] = after[2];



			before = getEnergyStats();
			//worst case
			System.out.println("Selection sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for (int i = 0; i < n - 1; i++)  
			{  
			    int index = i;  
			    for (int j = i + 1; j < n; j++){  
				if (num[j] < num[index]){  
				    index = j;//searching for lowest index  
				}  
			    }  
			    int smallerNumber = num[index];   
			    num[index] = num[i];  
			    num[i] = smallerNumber;  
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			selection_sort_worst_case_before[x*3+0] = before[0];
			selection_sort_worst_case_before[x*3+1] = before[1];
			selection_sort_worst_case_before[x*3+2] = before[2];
			

			selection_sort_worst_case_after[x*3+0] = after[0];
			selection_sort_worst_case_after[x*3+1] = after[1];
			selection_sort_worst_case_after[x*3+2] = after[2];


		



			//---------------------------------------------> Merge sort <---------------------------------------------//
			before = getEnergyStats();
			//best case
			System.out.println("Merge sort :best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_best_case_before[x*3+0] = before[0];
			merge_sort_best_case_before[x*3+1] = before[1];
			merge_sort_best_case_before[x*3+2] = before[2];
			

			merge_sort_best_case_after[x*3+0] = after[0];
			merge_sort_best_case_after[x*3+1] = after[1];
			merge_sort_best_case_after[x*3+2] = after[2];



			//worst case
			before = getEnergyStats();
			System.out.println("Merge sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			sort(num, 0, n);
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			merge_sort_worst_case_before[x*3+0] = before[0];
			merge_sort_worst_case_before[x*3+1] = before[1];
			merge_sort_worst_case_before[x*3+2] = before[2];
			

			merge_sort_worst_case_after[x*3+0] = after[0];
			merge_sort_worst_case_after[x*3+1] = after[1];
			merge_sort_worst_case_after[x*3+2] = after[2];

			



		

				    


			//------------------------------------------------------> Heap sort <---------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Heap sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_best_case_before[x*3+0] = before[0];
			heap_sort_best_case_before[x*3+1] = before[1];
			heap_sort_best_case_before[x*3+2] = before[2];
			

			heap_sort_best_case_after[x*3+0] = after[0];
			heap_sort_best_case_after[x*3+1] = after[1];
			heap_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Heap sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i= n/2 -1;i>=0;i--){
				heapify(num,n,i);
			}
			for(int i = n-1; i>=0;i--){
				int temp = num[0];
				num[0] = num[i];
				num[i] = temp;

				heapify(num,i,0);
			}
			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			heap_sort_worst_case_before[x*3+0] = before[0];
			heap_sort_worst_case_before[x*3+1] = before[1];
			heap_sort_worst_case_before[x*3+2] = before[2];
			

			heap_sort_worst_case_after[x*3+0] = after[0];
			heap_sort_worst_case_after[x*3+1] = after[1];
			heap_sort_worst_case_after[x*3+2] = after[2];

		
			//------------------------------------------------------> Radix sort <-------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Radix sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_best_case_before[x*3+0] = before[0];
			radix_sort_best_case_before[x*3+1] = before[1];
			radix_sort_best_case_before[x*3+2] = before[2];
			

			radix_sort_best_case_after[x*3+0] = after[0];
			radix_sort_best_case_after[x*3+1] = after[1];
			radix_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Radix sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			radixsort(num, n);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			radix_sort_worst_case_before[x*3+0] = before[0];
			radix_sort_worst_case_before[x*3+1] = before[1];
			radix_sort_worst_case_before[x*3+2] = before[2];
			

			radix_sort_worst_case_after[x*3+0] = after[0];
			radix_sort_worst_case_after[x*3+1] = after[1];
			radix_sort_worst_case_after[x*3+2] = after[2];


			
			//------------------------------------------------------> Quick sort <--------------------------------------------------
			//best case
			before = getEnergyStats();
			System.out.println("Quick sort : best case");
			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_best_case_before[x*3+0] = before[0];
			quick_sort_best_case_before[x*3+1] = before[1];
			quick_sort_best_case_before[x*3+2] = before[2];
			

			quick_sort_best_case_after[x*3+0] = after[0];
			quick_sort_best_case_after[x*3+1] = after[1];
			quick_sort_best_case_after[x*3+2] = after[2];


			//worst case
			before = getEnergyStats();
			System.out.println("Quick sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			quickSort(num, 0, n-1);

			System.out.println(num[53]);
			after = getEnergyStats();
		
			//Store result
			quick_sort_worst_case_before[x*3+0] = before[0];
			quick_sort_worst_case_before[x*3+1] = before[1];
			quick_sort_worst_case_before[x*3+2] = before[2];
			

			quick_sort_worst_case_after[x*3+0] = after[0];
			quick_sort_worst_case_after[x*3+1] = after[1];
			quick_sort_worst_case_after[x*3+2] = after[2];
		}


		for(int i = 0;i < 15; i++){

			bubble_sort_best_case_diff[i] = (bubble_sort_best_case_after[i] - bubble_sort_best_case_before[i])/10.0;
			selection_sort_best_case_diff[i] = (selection_sort_best_case_after[i] - selection_sort_best_case_before[i])/10.0;
			merge_sort_best_case_diff[i] = (merge_sort_best_case_after[i] - merge_sort_best_case_before[i])/10.0;
			heap_sort_best_case_diff[i] = (heap_sort_best_case_after[i] - heap_sort_best_case_before[i])/10.0;
			radix_sort_best_case_diff[i] = (radix_sort_best_case_after[i] - radix_sort_best_case_before[i])/10.0;
			quick_sort_best_case_diff[i] = (quick_sort_best_case_after[i] - quick_sort_best_case_before[i])/10.0;

			
			bubble_sort_worst_case_diff[i] = (bubble_sort_worst_case_after[i] - bubble_sort_worst_case_before[i])/10.0;
			selection_sort_worst_case_diff[i] = (selection_sort_worst_case_after[i] - selection_sort_worst_case_before[i])/10.0;
			merge_sort_worst_case_diff[i] = (merge_sort_worst_case_after[i] - merge_sort_worst_case_before[i])/10.0;
			heap_sort_worst_case_diff[i] = (heap_sort_worst_case_after[i] - heap_sort_worst_case_before[i])/10.0;
			radix_sort_worst_case_diff[i] = (radix_sort_worst_case_after[i] - radix_sort_worst_case_before[i])/10.0;
			quick_sort_worst_case_diff[i] = (quick_sort_worst_case_after[i] - quick_sort_worst_case_before[i])/10.0;
		}

		for(int i = 0;i < 3; i++){

			bubble_sort_best_case_average[i] = (bubble_sort_best_case_diff[i]+bubble_sort_best_case_diff[i+3]+bubble_sort_best_case_diff[i+6]+bubble_sort_best_case_diff[i+9]+bubble_sort_best_case_diff[i+12])/5.0;

			selection_sort_best_case_average[i] = (selection_sort_best_case_diff[i]+selection_sort_best_case_diff[i+3]+selection_sort_best_case_diff[i+6]+selection_sort_best_case_diff[i+9]+selection_sort_best_case_diff[i+12])/5.0; 
;
			merge_sort_best_case_average[i] = (merge_sort_best_case_diff[i]+merge_sort_best_case_diff[i+3]+merge_sort_best_case_diff[i+6]+merge_sort_best_case_diff[i+9]+merge_sort_best_case_diff[i+12])/5.0;

			heap_sort_best_case_average[i] = (heap_sort_best_case_diff[i]+heap_sort_best_case_diff[i+3]+heap_sort_best_case_diff[i+6]+heap_sort_best_case_diff[i+9]+heap_sort_best_case_diff[i+12])/5.0; 

			radix_sort_best_case_average[i] = (radix_sort_best_case_diff[i]+radix_sort_best_case_diff[i+3]+radix_sort_best_case_diff[i+6]+radix_sort_best_case_diff[i+9]+radix_sort_best_case_diff[i+12])/5.0;

			quick_sort_best_case_average[i] = (quick_sort_best_case_diff[i]+quick_sort_best_case_diff[i+3]+quick_sort_best_case_diff[i+6]+quick_sort_best_case_diff[i+9]+quick_sort_best_case_diff[i+12])/5.0;





			bubble_sort_worst_case_average[i] = (bubble_sort_worst_case_diff[i]+bubble_sort_worst_case_diff[i+3]+bubble_sort_worst_case_diff[i+6]+bubble_sort_worst_case_diff[i+9]+bubble_sort_worst_case_diff[i+12])/5.0;

			selection_sort_worst_case_average[i] = (selection_sort_worst_case_diff[i]+selection_sort_worst_case_diff[i+3]+selection_sort_worst_case_diff[i+6]+selection_sort_worst_case_diff[i+9]+selection_sort_worst_case_diff[i+12])/5.0; 
;
			merge_sort_worst_case_average[i] = (merge_sort_worst_case_diff[i]+merge_sort_worst_case_diff[i+3]+merge_sort_worst_case_diff[i+6]+merge_sort_worst_case_diff[i+9]+merge_sort_worst_case_diff[i+12])/5.0;

			heap_sort_worst_case_average[i] = (heap_sort_worst_case_diff[i]+heap_sort_worst_case_diff[i+3]+heap_sort_worst_case_diff[i+6]+heap_sort_worst_case_diff[i+9]+heap_sort_worst_case_diff[i+12])/5.0; 

			radix_sort_worst_case_average[i] = (radix_sort_worst_case_diff[i]+radix_sort_worst_case_diff[i+3]+radix_sort_worst_case_diff[i+6]+radix_sort_worst_case_diff[i+9]+radix_sort_worst_case_diff[i+12])/5.0;

			quick_sort_worst_case_average[i] = (quick_sort_worst_case_diff[i]+quick_sort_worst_case_diff[i+3]+quick_sort_worst_case_diff[i+6]+quick_sort_worst_case_diff[i+9]+quick_sort_worst_case_diff[i+12])/5.0;

 
		}


		
		System.out.println("Printing:-------------------------------------->Best case ------> n : "+n);
		//print best case
		System.out.println("(bubble sort ---->best case) Power consumption of dram: "+bubble_sort_best_case_average[0]);
		System.out.println("(bubble sort ---->best case) Power consumption of cpu: "+bubble_sort_best_case_average[1]);
		System.out.println("(bubble sort ---->best case) Power consumption of package: "+bubble_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->best case) Power consumption of dram: "+selection_sort_best_case_average[0]);
		System.out.println("(selection sort ---->best case) Power consumption of cpu: "+selection_sort_best_case_average[1]);
		System.out.println("(selection sort ---->best case) Power consumption of package: "+selection_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->best case) Power consumption of dram: "+merge_sort_best_case_average[0]);
		System.out.println("(merge sort ---->best case) Power consumption of cpu: "+merge_sort_best_case_average[1]);
		System.out.println("(merge sort ---->best case) Power consumption of package: "+merge_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->best case) Power consumption of dram: "+heap_sort_best_case_average[0]);
		System.out.println("(heap sort ---->best case) Power consumption of cpu: "+heap_sort_best_case_average[1]);
		System.out.println("(heap sort ---->best case) Power consumption of package: "+heap_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->best case) Power consumption of dram: "+radix_sort_best_case_average[0]);
		System.out.println("(radix sort ---->best case) Power consumption of cpu: "+radix_sort_best_case_average[1]);
		System.out.println("(radix sort ---->best case) Power consumption of package: "+radix_sort_best_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->best case) Power consumption of dram: "+quick_sort_best_case_average[0]);
		System.out.println("(quick sort ---->best case) Power consumption of cpu: "+quick_sort_best_case_average[1]);
		System.out.println("(quick sort ---->best case) Power consumption of package: "+quick_sort_best_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Printing:-------------------------------------->Worst case");

		//print best case
		System.out.println("(bubble sort ---->worst case) Power consumption of dram: "+bubble_sort_worst_case_average[0]);
		System.out.println("(bubble sort ---->worst case) Power consumption of cpu: "+bubble_sort_worst_case_average[1]);
		System.out.println("(bubble sort ---->worst case) Power consumption of package: "+bubble_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(selection sort ---->worst case) Power consumption of dram: "+selection_sort_worst_case_average[0]);
		System.out.println("(selection sort ---->worst case) Power consumption of cpu: "+selection_sort_worst_case_average[1]);
		System.out.println("(selection sort ---->worst case) Power consumption of package: "+selection_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(merge sort ---->worst case) Power consumption of dram: "+merge_sort_worst_case_average[0]);
		System.out.println("(merge sort ---->worst case) Power consumption of cpu: "+merge_sort_worst_case_average[1]);
		System.out.println("(merge sort ---->worst case) Power consumption of package: "+merge_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(heap sort ---->worst case) Power consumption of dram: "+heap_sort_worst_case_average[0]);
		System.out.println("(heap sort ---->worst case) Power consumption of cpu: "+heap_sort_worst_case_average[1]);
		System.out.println("(heap sort ---->worst case) Power consumption of package: "+heap_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(radix sort ---->worst case) Power consumption of dram: "+radix_sort_worst_case_average[0]);
		System.out.println("(radix sort ---->worst case) Power consumption of cpu: "+radix_sort_worst_case_average[1]);
		System.out.println("(radix sort ---->worst case) Power consumption of package: "+radix_sort_worst_case_average[2]);
		System.out.println();

		System.out.println("(quick sort ---->worst case) Power consumption of dram: "+quick_sort_worst_case_average[0]);
		System.out.println("(quick sort ---->worst case) Power consumption of cpu: "+quick_sort_worst_case_average[1]);
		System.out.println("(quick sort ---->worst case) Power consumption of package: "+quick_sort_worst_case_average[2]);
		System.out.println();
		System.out.println();
		System.out.println();


		ProfileDealloc();
		*/
		


		
		
		




		
		
		



			/*
			//best case
			System.out.println("Bubble sort : best case");

			for(int i=0;i<n;i++){
				num[i] = i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[153]);
			after = getEnergyStats();
		
			System.out.println("Power consumption of dram: " + (after[0] - before[0]) / 10.0 + "\npower consumption of cpu: " + (after[1] - before[1]) / 10.0 + "\npower consumption of package: " + (after[2] - before[2]) / 10.0);



			before = getEnergyStats();
			//worst case
			System.out.println("Bubble sort : worst case");
			for(int i=0;i<n;i++){
				num[i] = n-i;
			} 

			for(int i=0; i < n; i++){  
		        	for(int j=1; j < (n-i); j++){  
		                	if(num[j-1] > num[j]){  
		                         	//swap elements  
		                        	int temp = num[j-1];  
		                        	num[j-1] = num[j];  
		                        	num[j] = temp;  
		                	}  
		                  
		         	}  
		 	}
			System.out.println(num[153]);
			after = getEnergyStats();
		
			
		
			
			System.out.println("Power consumption of dram: " + (after[0] - before[0]) / 10.0 + "\npower consumption of cpu: " + (after[1] - before[1]) / 10.0 + "\npower consumption of package: " + (after[2] - before[2]) / 10.0);*/



		



		



	}


	
}
