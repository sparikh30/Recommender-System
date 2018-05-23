import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Algorithm{

    public float sum=0;				// used to calculate sum of non zero items for a user
    public int count=0; 		  	// calculate count of non zero items for a user
    public Double [][]columnmatrix;  
    public int columns; 
    public double nume=0;  	//calculate numerator of weightedSum()
	public double deno=0;  	//calculate denominator of weightedSum()
	public double l=0;  
	public double m=0;
	public double n=0;	
	public double mroot; 
	public double nroot;
	public double coll; 			// adjusted cosine similarity value of items
	
    // Hash Map to store list of items similar to given item
	Map<Integer, List<Integer>> Mymap = new HashMap<Integer, List<Integer>>();
    
    public Algorithm(int colummax){
    	this.columns = colummax;
    	columnmatrix = new Double[colummax][colummax];
    	
    	for(int i=1;i<colummax;i++)
    	{
    		for(int j=1;j<colummax;j++)
    		{
    			columnmatrix[i][j]=0.0;			// Initialize the column matrix with 0
    		}
    	}
    	
    }
    		
    public float findItemAverage(int[][] a,int rowval, int rowmax){
    	for(int j=1;j<rowmax;j++)
		  {
    		if(a[j][rowval]!=0) 				//if non zero value of item 
    			{
    				sum=sum+a[j][rowval]; 		//sum it 
    				count++;    				//count of non zero terms
    			}
		  }
    	float average = sum/count;
    	sum=0;    								//reset sum
    	count=0;  							    //reset count
    	return average;
	}
     
    // FIND SIMILARITY BASED ON COLLABORATIVE FILTERING ALGORITHM BELOW
    
    
    public double findItemsSimilarity(int[][] a,int rowmax,int j,int k,float[] Ritem)
    {
    	for(int i=1;i<rowmax;i++)			    	//iterate over all rows having rating for item j and k
    	{
			if(a[i][j]!=0 && a[i][k]!=0)
			{
    			l+=((a[i][j]-Ritem[j])*(a[i][k]-Ritem[k]));
    			m+=((a[i][j]-Ritem[j])*(a[i][j]-Ritem[k]));
    			n+=((a[i][k]-Ritem[j])*(a[i][k]-Ritem[k]));
    		}
    	}
    	mroot = Math.sqrt(m);
    	nroot = Math.sqrt(n);
    	coll = l/(mroot * nroot);			//apply cosine Equation
    	
    	columnmatrix[j][k]=(coll);		//add the similarity value to column matrix
    	l=0; 								// reset values
    	m=0;
    	n=0;
    	return (coll);                 
    }
   
    public void addToMap(Integer item, Integer item2){
    	
		if (Mymap.containsKey(item))   				//if key already exists in hashmap add similar item to existing key
		        Mymap.get(item).add(item2);  		
		else 
		{
		        List<Integer> list = new ArrayList<Integer>(); 				//create a list
		        list.add(item2); 											//add the item2 to the list
		        Mymap.put(item, list);										// put this list into the hashmap corresponding to key item
		                
		}	 
			
	}
        
    public List<Integer> getSimilarList(int j)
    {
    	if (Mymap.containsKey(j) )  
			return Mymap.get(j);   //get list of items similar to j 
	    else
			return null;   	    //else return null
    }
    
    public Double weightedSumOfSimilar(int i, int j, List<Integer> sim_value,int[][] a){
    	nume=0;  //initialize numerator to 0
    	deno=0; //initialize denominator to 0 
       	
    	//loop over similar items
    	for(int k=0;k<sim_value.size();k++){  
       		
    		if(a[i][sim_value.get(k)]!=0)			//if the item rating for a user is non zero
    		{
    			nume+=((columnmatrix[j][sim_value.get(k)]) * (a[i][sim_value.get(k)])); //multiply similarity value of given item j and k with rating of item k for user i. We have to take summation
    			
    			deno+=Math.abs(columnmatrix[j][sim_value.get(k)]);		    			// calculate summation of absolute value of similarity between j and k
       		}
       	}
    	Double Myprediction=nume/deno;//find predicted value
    	return Myprediction;
    }
}
