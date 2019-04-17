/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import main.Pair;

/**
 *
 * @author criso
 */
public class PRF {
    /**
     * Create a data structure with 5 words most frequents in the text
     * @param IDF data structure with different words and it´s frequency
     * @param path Colection of files
     * @return data structure with the most frequent words
     * @throws java.io.IOException
     */
    public static ArrayList<Pair<String,Integer>> top5 (HashMap<String,Double> IDF,  String path) throws IOException {
        PriorityQueue<Pair<String, Integer>> list = new PriorityQueue<>(10,(o1, o2) -> {
            return ((int) o2.getSecond() - (int) o1.getSecond());
        });
        HashMap<String,Integer> map = new HashMap<>();

        BufferedReader br;
        String word;
        ArrayList output = new ArrayList();
        
        br = new BufferedReader(new FileReader(new File(path)));
        while ( (word = br.readLine()) != null) {
            for (HashMap.Entry<String, Double> entry : IDF.entrySet()) {
                if (entry.getValue() != 0 && entry.getKey().equals(word)) {
                    if (map.containsKey(word)) 
                        map.put(word, map.get(word) + 1);
                    else 
                        map.put(word, 1);
                }
            } 
        }
        
        map.entrySet().stream().map((entry) -> new Pair<>(entry.getKey(),entry.getValue())).forEachOrdered((tuple) -> {
            list.offer(tuple);
        });

        for (int i = 0; i < 5; i++){
            output.add(new Pair<>(list.peek().getFirst(),list.poll().getSecond()));
        }

        return output;
    }
}
