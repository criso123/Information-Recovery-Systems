/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;
import java.io.BufferedWriter;
import java.io.IOException;
import org.tartarus.snowball.ext.*;

/**
 *
 * @author criso
 */
public class Stemmer {
    /**
     * Catch roots of a given text 
     * @param text text with will be process
     * @param bw file of source where I catch texts
     * @return data structure with the data of file with the roots
     * @throws java.io.IOException
     */
    static public String roots (String text, BufferedWriter bw) throws IOException{
        spanishStemmer stemmer = new spanishStemmer();
        String words[] = text.split("\n");
        String output = "";
        for (String word : words) {
            stemmer.setCurrent(word);
            if (stemmer.stem())
                output += stemmer.getCurrent()+"\n";
        }
        bw.write(output);
        bw.close();
        return output;
    }
}
