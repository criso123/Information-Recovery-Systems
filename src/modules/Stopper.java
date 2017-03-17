/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
     * @param br file of source where I catch texts
     * @return data structure with the data of file with the empty words
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
     * @param array data structure with empty words
     * @param bw file of source where I catch texts
     * @param text the text that I follow process
     * @return data structure with the data of file with the empty words
     * @throws java.io.IOException
     */
    public static String tokenizationAfter (ArrayList<String> array, BufferedWriter bw, String text) throws IOException {
        String output = "";
        StringTokenizer tokens = new StringTokenizer(text);
        while(tokens.hasMoreTokens()){
            String token = tokens.nextToken();
            if (!array.contains(token)){
                bw.write(token+"\n");
                output += token+"\n";
            }
        }
        bw.close();
        return output;
    }
}
