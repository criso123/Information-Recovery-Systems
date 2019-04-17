/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author criso
 */
public class Similarity {
    /**
     * Calculate similiraity between word and frecuency
     * @param wnij data structure with weight of each word of each fileWNij
     * @param wniq data structured with weight of each word of query
     * @return map with ID of document and its similarity
     */
    public static HashMap<Integer, Double> sim (HashMap<Integer, HashMap<String, Double>> wnij, HashMap<String, Double> wniq) {
        HashMap<Integer, Double> output = new HashMap<>();
        HashMap<String, Double> tmp = new HashMap<>();
        double valueWnij = 0, valueWniq = 0;
        
        for (Map.Entry<Integer, HashMap<String, Double>> entry : wnij.entrySet()) {
            HashMap<String, Double> words = entry.getValue();
            valueWnij = 0;
            valueWniq = 0;
            for (Map.Entry<String, Double> entryQ : wniq.entrySet()) {
                valueWnij = words.entrySet().stream().filter((e) -> (entryQ.getKey() == null ? null == e.getKey() : entryQ.getKey().equals(e.getKey()))).map((e) -> e.getValue()).reduce(valueWnij, (accumulator, _item) -> accumulator + _item);
                valueWniq += entryQ.getValue();
            }
            output.put(entry.getKey(), valueWnij*valueWniq);
        }
        
        return output;
    }
    /**
     * Sort a map by value in reverse order
     * @param <K> first type parameter
     * @param <V> second type parameter
     * @param sim map what I want sorting
     * @return sorted map
     */
    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> sim) {
    return sim.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
      .collect(Collectors.toMap(
        Map.Entry::getKey, 
        Map.Entry::getValue, 
        (e1, e2) -> e1, 
        LinkedHashMap::new
      ));
    }
}
