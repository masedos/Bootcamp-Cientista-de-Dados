
package IGTI;

import java.io.*;
import java.util.*;
import java.util.Random;
import java.text.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;


public class ExemploIGTI extends Configured implements Tool 
{          
    public static void main (final String[] args) throws Exception {   
      int res = ToolRunner.run(new Configuration(), new ExemploIGTI(), args);        
      System.exit(res);           
    }   

    public int run (final String[] args) throws Exception {
      try{ 	             	       	
           JobConf conf = new JobConf(getConf(), ExemploIGTI.class); 

           conf.setJobName("Exemplo Bootcamp - Covid19");

           final FileSystem fs = FileSystem.get(conf);
  
           Path diretorioEntrada = new Path("PastaEntrada"), diretorioSaida = new Path("PastaSaida");
           fs.mkdirs(diretorioEntrada);

           fs.copyFromLocalFile(new Path("/usr/local/hadoop/Dados/covidData.txt"), diretorioEntrada); 
           
           FileInputFormat.setInputPaths(conf, diretorioEntrada);
           FileOutputFormat.setOutputPath(conf, diretorioSaida);

           conf.setOutputKeyClass(Text.class);
           conf.setOutputValueClass(Text.class);  
         
           conf.setMapperClass(MapIGTI.class);
           conf.setReducerClass(ReduceIGTI.class);

           JobClient.runJob(conf);               
        }
        catch ( Exception e ) {
            throw e;
        }
        return 0;
     }
 
    public static class MapIGTI extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
            
      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {           
         
      }        
    }
 
    
    public static class ReduceIGTI extends MapReduceBase implements Reducer<Text, Text, Text, Text> {       
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {  
        
       
    }
}

   public static class MapIGTIMaior extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {
        
         
     }
}

    public static class ReduceIGTIMaior extends MapReduceBase implements Reducer<Text, Text, Text, Text> {   
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {     
    

  
  }

}
}


