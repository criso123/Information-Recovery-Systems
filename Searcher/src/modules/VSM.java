/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author criso
 */
public class VSM {
    /**
     * Normalized frequency of all words of the query
     * @param path query that will be process
     * @return data structure with the data of file with the normalized frequency
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static HashMap<Integer, HashMap<String,Double>> highestFrequency (String path) throws FileNotFoundException, IOException {
        double max = 0, cont = 0;
        int i=1;
        String pathMax;
        
        HashMap<Integer, HashMap<String,Double>> output = new HashMap<>();
        HashMap<String, Double> map = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        
        String cadena;
        
        String delims = "[ ]";
        String[] tokens = path.split(delims);
        for (String t: tokens) {
            if (map.containsKey(t)) {
                map.put(t, map.get(t)+1);
                cont=map.get(t);
            }
            else {
                map.put(t, (double)1);
                cont=1;
            }
            if(cont>max){
                max=cont;
                pathMax=t;
            }
        }

        for (String t: tokens) {
            if (map.containsKey(t)) {
                tmp.put(t, map.get(t)/max);
            }
        }
        output.put(i, tmp);
        tmp = new HashMap<>();
        i++;  

        return output;    
    }
    /**
     * Create a data structure with documentary frequency of each word
     * @param VSMstructure main data structure with words of each document
     * @param sizeCollection size of collection
     * @return data structure with the word and its normalized frequency
     */
    public static HashMap<String, Double> idf (HashMap<Integer, HashMap<String,Double>> VSMstructure, int sizeCollection) {
        
        HashMap<String, Double> output = new HashMap<>();
        HashMap<String, Integer> tmp = new HashMap<>();
        
        VSMstructure.entrySet().stream().map((e) -> e.getValue()).forEachOrdered((HashMap<String, Double> words) -> {
            words.entrySet().forEach((Map.Entry<String, Double> e2) -> {
                if (tmp.containsKey(e2.getKey())) {
                    tmp.put(e2.getKey(),tmp.get(e2.getKey())+1);
                } else {
                    tmp.put(e2.getKey(), 1);
                }
            });
            tmp.entrySet().forEach((e) -> {
                output.put(e.getKey(), Math.log10(sizeCollection/e.getValue()));
            });
        });
        
        return output;
    }
    /**
     * @param idf data structure with documentary frequency of each word
     * @param VSMstructure main data structure with words of each document
     * @return data structure with the weight of each word in collection
     */
    public static HashMap<Integer, HashMap<String, Double>> wiq (HashMap<String, Double> idf, 
            HashMap<Integer, HashMap<String, Double>> VSMstructure) {
        HashMap<Integer, HashMap<String, Double>> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : VSMstructure.entrySet()){
                HashMap<String, Double> words = entry.getValue();
                tmp = new HashMap<>();
                for (Map.Entry<String, Double> e : idf.entrySet()) {
                    if (words.containsKey(e.getKey())) {
                        //normal
                        tmp.put(e.getKey(), idf.get(e.getKey())*e.getValue());
                    }
                }
            output.put(entry.getKey(), tmp);
        }
        return output;
    }
    /**
     * Normalized weights of all words of each document
     * @param VSMstructure main data structure with words of each document
     * @param idf data structure with documentary frequency of each word
     * @return data structure with the weight of each word in each document
     */
 public static HashMap<String, Double> wniq (HashMap<Integer, HashMap<String, Double>> VSMstructure,
         HashMap<String, Double> idf) {
        HashMap<String, Double> tmp = new HashMap<>();
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : VSMstructure.entrySet()){
                HashMap<String, Double> words = entry.getValue();
                tmp = new HashMap<>();
                double cont = 0, root = 0;
                for (Map.Entry<String, Double> e : words.entrySet()) {
                    tmp.put(e.getKey(), Math.pow(e.getValue(), 2));
                    cont += Math.pow(e.getValue(), 2);
                }
                //Normalizate
                root = Math.sqrt(cont);
                double suma = 0;
                
                for (Map.Entry<String, Double> e : idf.entrySet()) {
                    double value = 0;
                    if (Double.isNaN(e.getValue()/root)){
                        value = 0;
                    } else {
                        value = e.getValue()/root;
                    }
                    tmp.replace(e.getKey(), value);
                }
        }
        return tmp;
    }
}
