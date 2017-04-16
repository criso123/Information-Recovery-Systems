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
import java.util.PriorityQueue;
import main.Pair;


/**
 *
 * @author criso
 */
public class Index {
 
    /**
     * Create an index wit max, min and frequency of each word
     * @param path document or collection that will be process
     * @return data structure with each word and its frequency
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static HashMap<String, Integer> loadDataStructure (String path) throws FileNotFoundException, IOException {
        int min = 9999, max = 0, cont = 1;
        String pathMin = "", pathMax = "";
        PriorityQueue<Pair<String, Integer>> list = new PriorityQueue<>(10,(o1, o2) -> {
            return ((int) o2.getSecond() - (int) o1.getSecond());
        });
        HashMap<String,Integer> map = new HashMap<>();

        BufferedReader br;
        String word;
        HashMap<String, Integer> output = new HashMap<>();
        File[] files = new File(path).listFiles();

        for (File file: files){
            br = new BufferedReader(new FileReader(file));
            while ( (word = br.readLine()) != null) {
                if (map.containsKey(word)) 
                    map.put(word, map.get(word) + 1);
                else 
                    map.put(word, 1);
                cont++;
                
            }
            if (cont<min){
                min=cont;
                pathMin = file.getName();
            }
                if(cont>max){
                    max=cont;
                    pathMax = file.getName();
                }
            cont=1;
        }
        System.out.println("The document shortest is: "+pathMin+", with: "+min+" words.");
        System.out.println("The document longest is: "+pathMax+", with: "+max+" words.");
        
        map.entrySet().stream().map((entry) -> new Pair<>(entry.getKey(),entry.getValue())).forEachOrdered((tuple) -> {
            list.offer(tuple);
        });
        
        return map;
    }
}
