import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class dm_project {

	String ifile;
	String ofile;
	BufferedWriter bufferwriter=null;
	BufferedReader bufferreader=null;
	FileWriter filewrite=null;
	
	public static void main(String[] args) 
	{
			
		dm_project obj = new dm_project();									// Object to access Driver class methods
		
		String s = "";												// string to append file lines
		int rows=944;
		int columns=1683;
		
		int[][]imatrix = new int[rows][columns]; 					//input matrix
		int[][]omatrix = new int[rows][columns]; 					//output matrix
		
		float[] Ritem  = new float[columns];

		Double Mythreshold =0.3;  //similarity threshold value is set to 0.3
		
		obj.setFile("train_all_txt.txt","output.txt");				// set input , output file
		
		Algorithm MyAlgo=new Algorithm(columns); 
		
		//Read file line by line, split the string by space

		while((s = obj.readMyfile())!=null)
		{
		    String[] splitted = s.split("\\s+");
		    Integer value = Integer.parseInt(splitted[2]);
			imatrix[Integer.parseInt(splitted[0])][Integer.parseInt(splitted[1])]=value;		// put third column into matrix value
        }
		
		long Time = System.currentTimeMillis();
		
		for(int i=1;i<columns;i++)
    	{
    		Ritem[i]=MyAlgo.findItemAverage(imatrix,i,rows);								//// find the ith item average
    	}

		for(int j=1;j<columns;j++)
		{
			for(int k=1;k<columns;k++)
			{
				  double similarity = MyAlgo.findItemsSimilarity(imatrix,rows,j,k,Ritem);	// find the item similarity and add it to hashmap datastructure
				  if(similarity >= Mythreshold)
					  MyAlgo.addToMap(j, k);
			}
		}
		
		
	 
		for (int i=1;i<rows;i++)
		{
			for(int j=1;j<columns;j++)
			{
				if(imatrix[i][j]==0)  												//if rating 0 for user i and item j then predict the rating
				{
			 		List<Integer> ListOfSimilar = MyAlgo.getSimilarList(j);			//find list of similar items
					
					if(ListOfSimilar!=null){
						Double predicted_value = MyAlgo.weightedSumOfSimilar(i,j,ListOfSimilar,imatrix);
						int prediction = (int)Math.round(predicted_value);
						if(prediction<=0)											//if predicted value after rounding off is less than or equal to 0 then set the rating as 1 as rating is between 1 and 5
                        	omatrix[i][j]=1;
						else if (prediction > 5)									//if predicted value after rounding off is greater than 5,set 5 rating 
                        	omatrix[i][j]=5;
                        else 														//else set calculated value of rating 	
                        	omatrix[i][j]=prediction;
			    	}
					else 															//if no similar item then set rating to 1
					{
					   omatrix[i][j]=1;
					}				
				}
				else 																// copy the non zero rating values directly to output array 
					omatrix[i][j]=imatrix[i][j];
			}
			
		}
		//System.out.println(omatrix[943][1600]);
		obj.writetoFile(rows, columns, omatrix);					//write the output to file
		
		
		long total_time=System.currentTimeMillis()-Time;
		System.out.println("Total Execution time is: "+total_time);

	}
	
	void setFile(String inputFile, String outputFile)
	{
		try 
		{
			this.ifile=inputFile;
			this.ofile=outputFile;
			this.bufferreader = new BufferedReader(new FileReader(inputFile));
		}
		catch(Exception e)
		{
   			System.err.println("Exception");
			System.exit(0);
		}
	}
	public String readMyfile()
	{
		try 
		{
			String str=bufferreader.readLine();
			return str;
		}
		catch (IOException e) 
		{
			System.out.println("IOException occured");
			System.exit(0);	
		}
		return null;
	}
	
	public void writetoFile(int rows,int columns,int [][]b)
	{
	
		StringBuilder builder=new StringBuilder();
		for(int i=1;i<rows;i++)
		{
			for(int j=1;j<columns;j++)
			{ 
				builder.append(i+" "+j+" "+b[i][j]+" \n");
			}  
		}
	
		String op = builder.toString();
		try 
		{
			File file = new File(ofile);
			if (!file.exists()) 
			{
				file.createNewFile();    // check if file exists  
			} // end if condition

			filewrite = new FileWriter(file.getAbsoluteFile(),false);
			bufferwriter = new BufferedWriter(filewrite);
			bufferwriter.write(op);
			bufferwriter.close();
			filewrite.close();
		} //end try block
		catch (IOException e) 
		{
			System.err.println("IOException Error");
			System.exit(0);	
		}
	}
}


