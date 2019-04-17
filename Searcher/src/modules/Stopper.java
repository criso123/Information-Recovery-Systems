/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author criso
 */
public class Stopper {
    /**
     * Load empty words
     * @param br file where I catch empty words
     * @return data structure with each empty word
     * @throws java.io.IOException
     */
    public static ArrayList<String> loadEmptyWords (BufferedReader br) throws IOException {
        ArrayList<String> stopWords = new ArrayList<>();
        Integer cont=0;
        String content = br.readLine();
            while (content != null) {
                stopWords.add(content);
                cont++;
                content = br.readLine();
            }
            return stopWords;
    }
    /**
     * Process the files
     * @param emptyWords data structure with empty words
     * @param text the text that I follow process
     * @return procesed text of each file of the collection
     * @throws java.io.IOException
     */
    public static String tokenizationAfter (ArrayList<String> emptyWords, String text) throws IOException {
        String output = "";
        StringTokenizer tokens = new StringTokenizer(text);
        while(tokens.hasMoreTokens()){
            String token = tokens.nextToken();
            if (!emptyWords.contains(token)){
                output += token+" ";
            }
        }
        return output;
    }
}
