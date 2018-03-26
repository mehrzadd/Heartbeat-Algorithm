//import java.util.Scanner;

import java.util.Random;

import mpi.*;

public class Network_Topology {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MPI.Init(args);
		int rank=MPI.COMM_WORLD.Rank();		//rank(ID) of each process
		int size=MPI.COMM_WORLD.Size();		//size of nodes
	    int[][] table = new int[size][size];	
    	int[] array= new int[size*size];

	    if (rank==0) {
	    	// Initialize the matrix by the random number 0 and 1
	    	//1 shows there is a link between two nodes, otherwise 0.
    		Random r = new Random();
	    	int[][] temp_array= new int[size][size];
	    	for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (i!=j) {
						temp_array[i][j] = r.nextInt(2);
						temp_array[j][i] = temp_array[i][j];
					}
				}
			}
	    	
	    	//Transforming 2-dimensional matrix into a 1-dimensional array 
	    	for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					array[i*size+j]=temp_array[i][j];
				}
			}
		}
	    //broadcasting the array to all nodes just for extracting require information
	    MPI.COMM_WORLD.Bcast(array, 0, size*size, MPI.INT, 0);

	    for (int r=0; r<size; r++) {
	    	for (int c=r*size; c<r*size+size; c++)
	    	{
	    		table[r][(c%size)]=array[c];
	    	}
	    }  
	    if(rank==0)		//output the matrix
	    {
	    	for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					System.out.print(table[i][j]);
				}
				System.out.println();
			}
	    }
	    
// check to matrix be symmetric, because the network is not directed network.
	    if (rank==0) {
		    for (int r=0; r<size; r++) {
		    	for (int c=0; c<size; c++) {
		    		if (table[r][c]!=table[c][r]) {
		    			System.out.println("The matrix array must be symmetric");
		    			System.exit(0);
		    		}
		    	}
		    }
	    }
	    
	    // by following commands each nodes will know about the number of its neighbors 
	    int[] neighbours = new int[size*size];
	    int[] neighbour = new int[size]; 
	    for (int i=0; i<size; i++) {
	    	for (int j=0; j<size; j++){
	    		if (rank==i) {
	    			neighbour[j]=table[i][j];
	    			if (rank==0) {
	    				neighbours[j]=table[0][j];
	    			}
	    		}
	    	}
	    }
	    
	    // node with rank 0 is central node. Therefore, all other nodes must sent their information to node 0
	    if (rank!=0)
	    	MPI.COMM_WORLD.Send(neighbour, 0, size, MPI.INT, 0, rank);

	    //node 0 will receive all information
	    if (rank==0) {
	    	for (int r=1; r<size; r++) {
	    		MPI.COMM_WORLD.Recv(neighbours, r*size, size, MPI.INT, r, r);
	    	}
	    }

	    //Transforming a 1-dimensional array to a 2-dimensional array by node 0
	    int[][] final_array=new int[size][size];
	    if (rank==0) {
	    	for (int r=0; r<size; r++) {
	    		for (int c=r*size; c<r*size+size; c++)
	    		{
	    			final_array[r][(c%size)]=neighbours[c];
	    		}
	    	}
	    	int[] count_link= new int[size];

	    	for (int r=0; r<size; r++) {
		    	for (int c=0; c<size; c++) {
		    		if (final_array[r][c]==1)
		    		{
		    			count_link[r]=count_link[r]+1;
		    		}
		    	}
		    }
	    
	    	
	    	int count_n_1=0;
	    	int count_2=0;
	    	int count_1=0;
	    	int count_4=0;

	    	//counting the number of links each node has
	    	for (int r=0; r<size; r++)
	    	{
	    		if (count_link[r]==size-1)
	    			count_n_1=count_n_1+1;
	    		if (count_link[r]==2)
	    			count_2=count_2+1;
	    		if (count_link[r]==1)
	    			count_1=count_1+1;
	    		if (count_link[r]==4)
	    			count_4=count_4+1;
	    	}
	    	
	    	if (count_n_1==size)
		    	System.out.println("Network Topology is FULLY CONNECTED");
	    	else if (count_2==size)
		    	System.out.println("Network Topology is RING");
	    	else if (count_2==size-2 && count_1==2)
		    	System.out.println("Network Topology is BUS");
	    	else if (count_4==size)
		    	System.out.println("Network Topology is MESH");
	    	else if (count_1==size-1 && count_n_1==1)
		    	System.out.println("Network Topology is STAR"); 
	    	else
	    		System.out.println("No stndard topology");
	    	
	    }
		MPI.Finalize();
		
	}

}
