package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import modules.Index;
import modules.Stemmer;
import modules.Stopper;
import modules.Tokenization;
import modules.VSM;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author criso
 */
public class IRS {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException{
        ArrayList<String> conf = load();
        
        //DECLARATION OF VARIABLES
        int count, countAfter, countAll = 0, countAllAfter = 0;
        int[] index = new int[8];
        for (int i=0; i<8; i++)
            index[i] = i;
        int countStemmer, countAllStemmer = 0;
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        int contNuevo2 = 0, contFinal2 = 0, max1 = 0, max2 = 0, max3 = 0, min1 = 9999, min2 = 9999, min3 = 9999;
        
        //CREATION OF FOLDER
        directoryCreation(conf.get(index[1]));
        directoryCreation(conf.get(index[2]));
        directoryCreation(conf.get(index[4]));
        directoryCreation(conf.get(index[5]));
        directoryCreation(conf.get(index[6]));
        directoryCreation(conf.get(index[7]));
        createFiles(conf.get(index[0]), conf.get(index[1]));
        createFiles(conf.get(index[1]), conf.get(index[2]));
        createFiles(conf.get(index[2]), conf.get(index[4]));
        
        //LOAD MAIN FILES WITHOUT PROCESS THEM
        File colection = new File(conf.get(index[0]));
        File files[] = colection.listFiles();
        
        //LOAD THE FILE WITH EMPTY WORDS
        BufferedReader br = new BufferedReader(new FileReader(conf.get(index[3])));
        ArrayList<String> emptyWords = Stopper.loadEmptyWords(br);
        
      //PATH OF EACH DOCUMENT IN THE COLLECTION
        HashMap<Integer, String> ID = new HashMap<>();        
        int objtIndex = 1;
        
        //MAIN LOOP
        System.out.println("Wait.....");
        for (File file : files) {
            String name = file.getName();
            
            //REMOVE ACCENTS AND TAGS HTML
            String noHTML = Tokenization.removeHTML(file.getAbsolutePath());
            String noAccents = Tokenization.cleanAccents(noHTML);
            
            //CREATION OF NEW FILES AFTER PROCESS THEM
            String newPath = conf.get(index[1]) + '\\' + name;
            String newPathEW = conf.get(index[2]) + '\\' + name;
            String newPathStemmer = conf.get(index[4]) + '\\' + name;
            
            //WRITE IN THE FOLDERS
            BufferedWriter bw, bwEW, bwStemmer, bwIndex;
            bw = new BufferedWriter(new FileWriter(newPath));
            bwEW = new BufferedWriter(new FileWriter(newPathEW));
            bwStemmer = new BufferedWriter(new FileWriter(newPathStemmer));
            
            //TOKENIZATION
            count = Tokenization.tokenization(bw, noAccents);
            countAll += count;
            
            //STOPPER
            String noEmptyWords = Stopper.tokenizationAfter(emptyWords, bwEW, noAccents);
            
            countAfter = StringUtils.countMatches(noEmptyWords, "\n");
            countAllAfter += countAfter;
            
            //STEMMER
            String noRoots = Stemmer.roots(noEmptyWords, bwStemmer);
            
            countStemmer = StringUtils.countMatches(noRoots, "\n");
            countAllStemmer += countStemmer;
            
            //INDEX
            ID.put(objtIndex, file.getAbsolutePath());
            objtIndex++;
             
            //OPERATIONS
            //MINIMUM
            if (count < min1) min1 = count;
            if (countAfter < min2) min2 = countAfter;
            if (countStemmer < min3) min3 = countStemmer;
            
            //MAXIMUM
            if (count > max1) max1 = count;
            if (countAfter > max2) max2 = countAfter;
            if (countStemmer > max3) max3 = countStemmer;

        }
        
        //Write in the XML file called ID
        Element docu = new Element("document");
        Document docum = new Document(docu);
        docum.setRootElement(docu);
        ID.entrySet().stream().map((Map.Entry<Integer, String> entry) -> {
            Element word = new Element("word");
                word.addContent(new Element("key").setText(entry.getKey().toString()));
                word.addContent(new Element("value").setText(entry.getValue().toString()));
            return word;
        }).forEachOrdered((word) -> {
            docum.getRootElement().addContent(word);
            
        });
        XMLOutputter xmlOutputID = new XMLOutputter();
        xmlOutputID.setFormat(Format.getPrettyFormat());
        xmlOutputID.output(docum, new FileWriter(conf.get(index[5]) + '\\' + "ID.xml"));
        //end of the XML file IDF
        
        //TOP 5 OF EACH MODULE
        ArrayList<Pair<String, Integer>> mostFreq = top5(conf.get(index[1]));
        ArrayList<Pair<String, Integer>> mostFreqAfter = top5(conf.get(index[2]));
        ArrayList<Pair<String, Integer>> mostFreqStem = top5(conf.get(index[4]));
        
      
        //OUTPUT OF DATA
        System.out.println("-----------------------------------------------");
        System.out.println("-             AFTER TOKENIZATION:             -");
        System.out.println("-----------------------------------------------");
        System.out.println("We have: "+countAll+" words.");
        System.out.println("We have: "+countAll/files.length+" per file.");
        System.out.println("We have: "+files.length+" documents.");
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("-        Before of remove empty words:        -");
        System.out.println("-----------------------------------------------");
        System.out.println("We have: "+countAll+" words.");
        System.out.println("We have: "+countAll/files.length+" per file.");
        System.out.println("Maximum is: "+max1+" words.");
        System.out.println("Minimum is: "+min1+" words.");
        System.out.println("Top 5 are:");
        for (int i=0; i<mostFreq.size(); i++){
            System.out.println(i+1+". "+mostFreq.get(i).getFirst()+": "+mostFreq.get(i).getSecond()+" times");
        }
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("-        After of remove empty words:         -");
        System.out.println("-----------------------------------------------");
        System.out.println("We have: "+countAllAfter+" words.");
        System.out.println("We have: "+countAllAfter/files.length+" per file.");
        System.out.println("Maximum is: "+max2+" words.");
        System.out.println("Minimum is: "+min2+" words.");
        System.out.println("Top 5 are:");
        for (int i=0; i<mostFreqAfter.size(); i++){
            System.out.println(i+1+". "+mostFreqAfter.get(i).getFirst()+": "+mostFreqAfter.get(i).getSecond()+" times");
        }
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("-           After of remove roots:            -");
        System.out.println("-----------------------------------------------");
        System.out.println("We have: "+countAllStemmer+" words.");
        System.out.println("We have: "+countAllStemmer/files.length+" per file.");
        System.out.println("Maximum is: "+max3+" words.");
        System.out.println("Minimum is: "+min3+" words.");
        System.out.println("Top 5 are:");
        for (int i=0; i<mostFreqStem.size(); i++){
            System.out.println(i+1+". "+mostFreqStem.get(i).getFirst()+": "+mostFreqStem.get(i).getSecond()+" times");
        }
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("-           After of create index:            -");
        System.out.println("-----------------------------------------------");
        System.out.println("We have: "+files.length+" files.");
        HashMap<String, Integer> DS = Index.loadDataStructure(conf.get(index[4]));
        System.out.println("We have: "+DS.size()+" different words.");
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("-   Creating the index & Vector Space Model   -");
        System.out.println("-----------------------------------------------");
        
        HashMap<Integer, HashMap<String,Double>> VSMstructure = new HashMap<>();
        VSMstructure = VSM.highestFrequency(conf.get(index[4]));
        HashMap<String, Double> idf = VSM.idf(VSMstructure, files.length);
        
        //Write in the XML file IDF
        Element wordsIDF = new Element("differents_words");
        Document document = new Document(wordsIDF);
        document.setRootElement(wordsIDF);
        idf.entrySet().stream().map((Map.Entry<String, Double> entry) -> {
            Element word = new Element("word");
                word.addContent(new Element("key").setText(entry.getKey()));
                word.addContent(new Element("value").setText(entry.getValue().toString()));
            return word;
        }).forEachOrdered((word) -> {
            document.getRootElement().addContent(word);
            
        });
        XMLOutputter xmlOutputIDF = new XMLOutputter();
        xmlOutputIDF.setFormat(Format.getPrettyFormat());
        xmlOutputIDF.output(document, new FileWriter(conf.get(index[6]) + '\\' + "IDF.xml"));
        //end of the XML file IDF
        
        HashMap<Integer, HashMap<String,Double>> wij = VSM.wij(idf, VSMstructure);
        
        HashMap<Integer, HashMap<String,Double>> wnij = VSM.wnij(wij, idf);
        
        //Write in the XML file WNij
        Element collection = new Element("collection");
        Document doc = new Document(collection);
        
        doc.setRootElement(collection);
        wnij.entrySet().stream().map((Map.Entry<Integer, HashMap<String, Double>> entry) -> {
            Element keyDoc = new Element("document");
            //attribute value of the first map
            HashMap<String, Double> words = entry.getValue();
            //second map
            Element word = new Element("words");
            words.entrySet().forEach((e) -> {
                word.setAttribute(new Attribute("id",entry.getKey().toString()));
                word.addContent(new Element("key").setText(e.getKey()));
                word.addContent(new Element("value").setText(e.getValue().toString()));
            });
            return word;
        }).forEachOrdered((keyDoc) -> {
            doc.getRootElement().addContent(keyDoc);
            
        });
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, new FileWriter(conf.get(index[7]) + '\\' + "WNij.xml"));
        //end of the XML file WNij
            
        System.out.println("");
        
        time_end = System.currentTimeMillis();
        System.out.println("The task has taken "+ ( time_end - time_start ) +" milliseconds.");
    }
    /**
     * Load parameters of conf.data
     * @throws java.IOException
     */
    private static ArrayList<String> load() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(new File("conf.data")));
        ArrayList<String> parameters = new ArrayList<>();
        String line;
        
        while ((line = br.readLine()) != null ){
            parameters.add(line);
        }
        return parameters;
    }
    /**
     * It checks if the folder exits, if dont exits it create the folder
     * @param path name of the new folder
     * @throws java.IOException
     */
    private static void directoryCreation(String path) throws IOException{
        File directory = new File(path);
        if (directory.exists())
            System.out.println("Folder: "+directory.getName()+" exits.");
        else {
            directory.mkdir();
            System.out.println("Folder: "+directory.getName()+" has been created.");
        }
    }
    /**
     * Create new files with other extension in a destiny
     * @param pathColection Colection of files
     * @param destiny path of the new folder
     * @throws java.IOException
     */
    private static void createFiles(String pathColection, String destiny) throws IOException {
        File colection = new File(pathColection);
        File files[] = colection.listFiles();
        for (File file : files) {
            String name = file.getName();
            String absolutePath = file.getAbsolutePath();
            String newName = destiny+'/'+name;
            File newColection = new File(newName);
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(newColection)); 
        }
    }
    /**
     * Create a data structure with 5 words most frequents in the text
     * @param path Colection of files
     * @throws java.IOException
     */
    private static ArrayList<Pair<String,Integer>> top5 (String path) throws IOException {
        PriorityQueue<Pair<String, Integer>> list = new PriorityQueue<>(10,(o1, o2) -> {
            return ((int) o2.getSecond() - (int) o1.getSecond());
        });
        HashMap<String,Integer> map = new HashMap<>();

        BufferedReader br;
        String word;
        ArrayList output = new ArrayList();
        File[] files = new File(path).listFiles();

        for (File file: files){
            br = new BufferedReader(new FileReader(file));
            while ( (word = br.readLine()) != null) {
                if (map.containsKey(word)) 
                    map.put(word, map.get(word) + 1);
                else 
                    map.put(word, 1);
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