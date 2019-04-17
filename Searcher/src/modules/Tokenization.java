/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author criso
 */
public class Tokenization {
    /**
     * Given a file, this function returns the same text without HTML label,
     * @param path name of file
     * @return normalized text
     * @throws java.io.IOException
     */
    public static String removeHTML (String path) throws IOException {
        Document doc = Jsoup.parse(new File(path), "UTF-8");
        return doc.text();
    }
    /**
     * Given a text, this function returns the same text without accents and rare characters,
     * @param text the content of the file
     * @return normalized text
     * @throws java.io.IOException
     */
    public static String cleanAccents(String text) throws IOException {
        String clean = "";
        text = text.toLowerCase();
        //Normalizate text to remove accents
        text = StringUtils.stripAccents(text);
        //clean everyall less this intervale
        text = text.replaceAll("[^A-Za-z0-9-_'\"\\n]", " ");
        String[] listOfWords = text.split("\\s+");

        for (String words: listOfWords){
            if (!words.equals("-")){
                clean += words+" ";
            }
        }
        return clean;
    }
    /**
     * Given a text, this function returns the same text without accents and rare characters,
     * @param text the content of the file
     * @return normalized text
     * @throws java.io.IOException
     */
    public static String tokenization (String text) throws IOException {
        String output = "";
        StringTokenizer tokens=new StringTokenizer(text);
            while(tokens.hasMoreTokens()){
                //write each token in the file
                String token = tokens.nextToken();
                output += token+'\n';
            }
            return output;
    }
}
