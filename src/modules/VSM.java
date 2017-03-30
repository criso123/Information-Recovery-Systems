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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import main.Pair;

/**
 *
 * @author criso
 */
public class VSM {
    public static HashMap<Integer, HashMap<String,Double>> highestFrequency (String path) throws FileNotFoundException, IOException {
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

    public static HashMap<String, Double> idf (HashMap<String, Integer> array, HashMap<Integer, HashMap<String,Double>> map, int n) {
        HashMap<String, Integer> tmp = new HashMap<>();
        HashMap<String, Double> output = new HashMap<>();
        //Initialize of output
        for (Map.Entry<String, Integer> entry : array.entrySet()) {
            tmp.put(entry.getKey(), 0);
        }
        //dfi
        for (Map.Entry<Integer, HashMap<String, Double>> entry : map.entrySet()) {
            HashMap<String, Double> words = entry.getValue();
            for (Map.Entry<String, Double> e : words.entrySet()) 
                if (array.containsKey(e.getKey())){
                    tmp.put(e.getKey(), tmp.get(e.getKey())+1);
                }
        }
        //IDFs
        for (Map.Entry<String, Integer> entry : tmp.entrySet()) {
            output.put(entry.getKey(), Math.log10(n/entry.getValue()));
        }
        
        return output;
    }
    public static HashMap<Integer, HashMap<String, Double>> wij (HashMap<String, Double> idf, HashMap<Integer, HashMap<String, Double>> map) {
        HashMap<Integer, HashMap<String, Double>> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : map.entrySet()){
//            for (Map.Entry<String, Float> eIDF : idf.entrySet()) {
                HashMap<String, Double> words = entry.getValue();
                tmp = new HashMap<>();
                for (Map.Entry<String, Double> e : words.entrySet()) {
                    if (idf.containsKey(e.getKey())) {
                        //normal
                        tmp.put(e.getKey(), idf.get(e.getKey())*e.getValue());
                        //^2
//                        tmp.put(e.getKey(), Math.pow(idf.get(e.getKey())*e.getValue()),2);
                    }
                }
//            }
            output.put(entry.getKey(), tmp);
        }
        return output;
    }
    public static HashMap<Integer, Double> wnij (HashMap<Integer, HashMap<String, Double>> map) {
        HashMap<Integer, Double> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        HashMap<Integer, Double> param1 = new HashMap<>();
        
        int index = 0;
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : map.entrySet()){
                HashMap<String, Double> words = entry.getValue();
                tmp = new HashMap<>();
                double cont = 0;
                for (Map.Entry<String, Double> e : words.entrySet()) {
                    tmp.put(e.getKey(), Math.pow(e.getValue(), 2));
                    cont += Math.pow(e.getValue(), 2);
                }
                Math.sqrt(cont);
                output.put(entry.getKey(), cont);
        }
        return output;
    }
}
