/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author criso
 */
public class VSM {
    /**
     * Normalized frequency of all words of each document
     * @param path document or collection that will be process
     * @return data structure with the word and the normalized frequency
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static HashMap<Integer, HashMap<String,Double>> highestFrequency (String path) throws FileNotFoundException,
            IOException {
        double max = 0, cont = 0;
        int i=1;
        String pathMax = "";
        
        HashMap<Integer, HashMap<String,Double>> output = new HashMap<>();
        
        BufferedReader br;
        String word;
        File[] files = new File(path).listFiles();

        for (File file: files){
            cont = 0;
            max = 0;
            HashMap<String, Double> map = new HashMap<>();
            HashMap<String, Double> tmp = new HashMap<>();
            
            br = new BufferedReader(new FileReader(file));
            
            while ( (word = br.readLine()) != null) {
                if (map.containsKey(word)) {
                    map.put(word, map.get(word)+1);
                    cont=map.get(word)+1;
                }
                else {
                    map.put(word, (double)1);
                    cont=1;
                }
            
                if(cont>max){
                    max=cont-1;
                    pathMax=word;
                }
            }
            br = new BufferedReader(new FileReader(file));
                while ( (word = br.readLine()) != null) {
                    if (map.containsKey(word)) {
                        tmp.put(word, map.get(word)/max);
                    }
                }
            output.put(i, tmp);
            i++;
        }     
        return output;    
    }
    /**
     * Create a data structure with documentary frequency of each word
     * @param VSMstructure main data structure with words of each document
     * @param sizeCollection size of collection
     * @return data structure with the word and its normalized frequency
     */
    public static HashMap<String, Double> idf (HashMap<Integer, HashMap<String,Double>> VSMstructure, int sizeCollection) {
        HashMap<String, Integer> tmp = new HashMap<>();
        HashMap<String, Double> output = new HashMap<>();
        
        VSMstructure.entrySet().stream().map((e) -> e.getValue()).forEachOrdered((HashMap<String, Double> words) -> {
            words.entrySet().forEach((Map.Entry<String, Double> e2) -> {
                if (tmp.containsKey(e2.getKey())) {
                    tmp.put(e2.getKey(),tmp.get(e2.getKey())+1);
                } else {
                    tmp.put(e2.getKey(), 1);
                }
            });
        });
        tmp.entrySet().forEach((e) -> {
            output.put(e.getKey(), Math.log10(sizeCollection/e.getValue()));
        });
        return output;
    }
    /**
     * @param idf data structure with documentary frequency of each word
     * @param VSMstructure main data structure with words of each document
     * @return data structure with the weight of each word in collection
     */
    public static HashMap<Integer, HashMap<String, Double>> wij (HashMap<String, Double> idf,
            HashMap<Integer, HashMap<String, Double>> VSMstructure) {
        HashMap<Integer, HashMap<String, Double>> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : VSMstructure.entrySet()){
                HashMap<String, Double> words = entry.getValue();
                tmp = new HashMap<>();
                for (Map.Entry<String, Double> e : idf.entrySet()) {
                    if (words.containsKey(e.getKey())) {
                        //weight without normalize
                        tmp.put(e.getKey(), idf.get(e.getKey())*e.getValue());
                    }
                }
            output.put(entry.getKey(), tmp);
        }
        return output;
    }
    /**
     * Normalized weights of all words of each document
     * @param wij main data structure with words of each document
     * @return data structure with the weight of each word in each document
     */
    public static HashMap<Integer, HashMap<String, Double>> wnij (HashMap<Integer, HashMap<String, Double>> wij) {
        HashMap<Integer, HashMap<String, Double>> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : wij.entrySet()){
            HashMap<String, Double> words = entry.getValue();
            tmp = new HashMap<>();
            double cont = 0, root = 0;
            for (Map.Entry<String, Double> e : words.entrySet()) {
                //sum of weights word's raised to the square
                tmp.put(e.getKey(), Math.pow(e.getValue(), 2));
                cont += Math.pow(e.getValue(), 2);
            }
            
            root = Math.sqrt(cont);
            double suma = 0;

            for (Map.Entry<String, Double> e : words.entrySet()) {
                //Normalizate weights
                tmp.replace(e.getKey(), e.getValue()/root);
            }
            output.put(entry.getKey(), tmp);
        }
        return output;
    }
}
