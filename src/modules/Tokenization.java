/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.BufferedWriter;
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
     * Given a path of a file, this function returns the same text without HTML tags,
     * @param path name of file
     * @return text without HTML tags
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
        text = text.replaceAll("[^A-Za-z0-9-_\\n]", " ");
        String[] listOfWords = text.split("\\s+");

        for (String words: listOfWords){
            if (!words.equals("-")){
                clean += words;
                if (!clean.endsWith("\n")) 
                    clean += "\n";
            }
        }
        return clean;
    }
    /**
     * Given a text, this function returns the same text but each word is tokenized
     * @param text the content of the file
     * @param bw path where save this text
     * @return tokenized text
     * @throws java.io.IOException
     */
    public static int tokenization (BufferedWriter bw, String text) throws IOException {
        int contador=0;
        StringTokenizer tokens=new StringTokenizer(text);
        while(tokens.hasMoreTokens()){
            //write each token in the file
            String token = tokens.nextToken();
            contador++;
            bw.write(token+"\n");
        }
        bw.close();
        return contador;
    }
}
