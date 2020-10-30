
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


           JobConf conf2 = new JobConf(getConf(), ExemploIGTI.class);
           conf2.setJobName("Maior caso, Maior obito");
           Path diretorioSaida2 = new Path("Maiores");
 
           FileInputFormat.setInputPaths(conf2, diretorioSaida);
           FileOutputFormat.setOutputPath(conf2, diretorioSaida2);
 
           conf2.setOutputKeyClass(Text.class);
           conf2.setOutputValueClass(Text.class);

           conf2.setMapperClass(MapIGTIMaior.class);
           conf2.setReducerClass(ReduceIGTIMaior.class);

           JobClient.runJob(conf2);        

 
        }
        catch ( Exception e ) {
            throw e;
        }
        return 0;
     }
 
    public static class MapIGTI extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
            
      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {           
     
       /*
          2020-02-24T00:00:00Z,AF,Afghanistan,EMRO,1,1,0,0
          2020-02-25T00:00:00Z,AF,Afghanistan,EMRO,0,1,0,0
          2020-02-26T00:00:00Z,AF,Afghanistan,EMRO,0,1,0,0
        */
        Text txtChave = new Text();
        Text txtValor = new Text();

        String[] dadosCovid = value.toString().split(",");
        String dataEvento = dadosCovid[0];
        String paisEvento = dadosCovid[2];
        String novosCasos = dadosCovid[4];
        String novosObitos = dadosCovid[6];
  
        String strChave = dataEvento;
        String strValor = paisEvento + "|" + novosCasos + "|" + novosObitos;

        txtChave.set(strChave);
        txtValor.set(strValor);

        output.collect(txtChave, txtValor);

    
      }        
    }
 
    
    public static class ReduceIGTI extends MapReduceBase implements Reducer<Text, Text, Text, Text> {       
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {  
/* 
2020-05-27T00:00:00Z	Eswatini|5|0
2020-05-27T00:00:00Z	Botswana|0|0
2020-05-27T00:00:00Z	Estonia|10|0
2020-05-27T00:00:00Z	Eritrea|0|0
2020-05-27T00:00:00Z	Brazil|11687|807
*/
        int maiorCasos = 0, maiorObitos = 0;
        String paisCasos = "", paisObitos = "", strSaida = "";
        Text value = new Text();
        String[] campos = new String[3];

        while(values.hasNext()) {
          value = values.next();
          campos = value.toString().split("\\|");
          if (Integer.parseInt(campos[1]) > maiorCasos) {
             maiorCasos = Integer.parseInt(campos[1]);
             paisCasos = campos[0];
          }
          if (Integer.parseInt(campos[2]) > maiorObitos) {
             maiorObitos = Integer.parseInt(campos[2]);
             paisObitos = campos[0];
          }   
        }  
        strSaida = String.valueOf(maiorCasos) + "|" + paisCasos + "|";
        strSaida = strSaida + String.valueOf(maiorObitos) + "|" + paisObitos;
      
        value.set(strSaida);
        output.collect(key, value);       
    }
}

   public static class MapIGTIMaior extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {
        /*

2020-05-18T00:00:00Z	22813|United States of America|1320|United States of America
2020-05-19T00:00:00Z	31967|United States of America|1394|United States of America
2020-05-20T00:00:00Z	13227|United States of America|697|United States of America
2020-05-21T00:00:00Z	24417|United States of America|1179|Brazil

*/
          Text txtChave = new Text();
          Text txtValor = new Text();

          String strChave = "1";
          String[] valor = value.toString().split("\\t");
          txtChave.set(strChave);
          txtValor.set(valor[1]);
          output.collect(txtChave, txtValor);            
     }
}

    public static class ReduceIGTIMaior extends MapReduceBase implements Reducer<Text, Text, Text, Text> {   
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {     
           int totalCasos = 0, totalObitos = 0;
           String paisCasos = "", paisObitos = "", strSaida = "";
           Text value = new Text();
           String[] campos = new String[4];

           while (values.hasNext()){
              value = values.next();
              campos = value.toString().split("\\|");
              if (Integer.parseInt(campos[0]) > totalCasos) {
                 totalCasos = Integer.parseInt(campos[0]);
                 paisCasos = campos[1];
              }    
              if (Integer.parseInt(campos[2]) > totalObitos){
                 totalObitos = Integer.parseInt(campos[2]);
                 paisObitos = campos[3];
              }

           } 
         
           strSaida = String.valueOf(totalCasos) + "|" + paisCasos + "|";
           strSaida = strSaida + String.valueOf(totalObitos) + "|" + paisObitos;
           value.set(strSaida);
           output.collect(key, value);     
  }

}
}

