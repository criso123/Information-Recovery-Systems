package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import modules.PRF;
import modules.Similarity;
import modules.Stemmer;
import modules.Stopper;
import modules.Tokenization;
import modules.VSM;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import modules.snippet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author criso
 */
public class Searcher {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.net.URISyntaxException
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException,
            SAXException, URISyntaxException  {
        //PARAMETERS
        ArrayList<String> conf = load();
        int[] index = new int[10];
        for (int i=0; i<10; i++)
            index[i] = i;
        
        //LOAD DATA STRUCTURES
        HashMap<Integer, HashMap<String,Double>> wnij = loadWNij(conf.get(index[3]));
        HashMap<String,Double> IDF = loadIDF(conf.get(index[1]));
        HashMap<Integer, String> IdCollection = loadID(conf.get(index[5]));
        
        //LOAD FILE WITH EMPTY WORDS
        BufferedReader br = new BufferedReader(new FileReader("palabrasVacias.txt"));
        ArrayList<String> emptyWords = Stopper.loadEmptyWords(br);
        
        //READ THE QUERY OR QUERIES
        String query = doQuery();
        String stop = "stop";
        while (query == null ? stop != null : !query.equals(stop)) {
            String finalQuery = modify(query);
        
            //MAKE VECTOR SPACE MODEL FOR EACH QUERY
            HashMap<Integer, HashMap<String,Double>> VSMstructure = new HashMap<>();

            //WEIGHT OF EACH WORD OF THE QUERY
            VSMstructure = VSM.highestFrequency(finalQuery);

            //WEIGHT WITHOUT NORMALIZATE
            HashMap<Integer, HashMap<String,Double>> wiq = VSM.wiq(IDF, VSMstructure);

            //NORMALIZATED WEIGHT
            HashMap<String, Double> wniq = VSM.wniq(wiq, IDF);
            
            //SIMILARITY BETWEEN COLLECTION AND THE QUERY
            HashMap<Integer, Double> sim = Similarity.sim(wnij, wniq);
            
            //SHOW IN THE SCREEN
            String collectionPath = conf.get(index[7]);
            
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("How many documents you want?:");
            int number = Integer.parseInt(keyboard.readLine());
            
            //CREATION OF NEW FILES AFTER PROCESS THEM
            String newPathPRF = conf.get(index[9]);
            directoryCreation(newPathPRF);
            ArrayList<Pair<String, Integer>> mostFreq = new ArrayList<>();
            
            ArrayList<String> words = show(sim, IdCollection, number, 
                    collectionPath, finalQuery, newPathPRF, emptyWords, mostFreq, IDF);
//            ArrayList<String> words = show(sim, IdCollection, number, 
//                    finalQuery, emptyWords, mostFreq, IDF);
            HashMap<String, String> tmp = new HashMap<>();
            ArrayList<String> cleanedWords = new ArrayList<>();
            
            //Pseudo feedback by relevance for the new query
            for (int i=0; i<words.size();i++){
                tmp.put(words.get(i), words.get(i));
            }
            tmp.entrySet().forEach((e) -> {
                cleanedWords.add(e.getKey());
            });
            for (int i=0; i<cleanedWords.size();i++){
                finalQuery += words.get(i)+" ";
            }
            
//            System.out.println(finalQuery);
            System.out.println("--------------------------------------------------------------------");
            ArrayList<String> wordsPRF = show(sim, IdCollection, number, collectionPath,
                    finalQuery, newPathPRF, emptyWords, mostFreq, IDF);
            
            //DO OTHER QUERY
            query = doQuery();
        }
    }
    /**
     * Write in the screen by the screen
     * @return String withe the query
     * @throws java.IOException
     */
    private static String doQuery () throws IOException {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Write your query or queries (stop for exit):");
        String write = new String(keyboard.readLine().getBytes(),"ISO-8859-1");
        return write;
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
     * Modify the query with the differents modules
     * @return String withe the modified query
     * @throws java.IOException
     */
    private static String modify (String query) throws IOException {
        //REMOVE ACCENTS
        String noAccents = Tokenization.cleanAccents(query);

        //STOPPER
        BufferedReader br = new BufferedReader(new FileReader("palabrasVacias.txt"));
        ArrayList<String> emptyWords = Stopper.loadEmptyWords(br);

        String noEmptyWords = Stopper.tokenizationAfter(emptyWords, noAccents);

        //STEMMER
        String noRoots = Stemmer.roots(noEmptyWords);

        return noRoots;
    }
    /**
     * Load data structure with Wnij
     * @param path of file XML WNij
     * @return Map of maps with data structure
     * @throws java.IOException
     * @throws javax.ParserConfigurationException
     * @throws javax.SAXException
     */
    private static HashMap<Integer, HashMap<String,Double>> loadWNij(String path) throws ParserConfigurationException,
            SAXException, IOException  {
        File fileXML = new File(path);
        HashMap<Integer, HashMap<String,Double>> wnij = new HashMap<>();
        
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("words");
        
        for (int i=0; i<nList.getLength(); i++) {
            HashMap<String,Double> words = new HashMap<>();
            Node node = nList.item(i);
            Element element = (Element) node;
            
            //ALL KEY CHILDREN
            NodeList nodeListKey = element.getElementsByTagName("key");
            //ALL CHILDREN
            NodeList nodeListValue = element.getElementsByTagName("value");

            for (int j=0; j<nodeListKey.getLength(); j++) {
                Node key = nodeListKey.item(j);
                Node value = nodeListValue.item(j);

                words.put(key.getTextContent(), Double.parseDouble(value.getTextContent()));
            }

            wnij.put(Integer.parseInt(element.getAttribute("id")), words);
        }
        return wnij;
    }
    /**
     * Load data structure with IDFs
     * @param path of file XML IDF
     * @return Map with words and its frequency
     * @throws java.IOException
     * @throws javax.ParserConfigurationException
     * @throws javax.SAXException
     */        
    private static HashMap<String,Double> loadIDF(String path) throws ParserConfigurationException,
            SAXException, IOException  {
        File fileXML = new File(path);
        HashMap<String,Double> IDF = new HashMap<>();
        
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("word");
        
        for (int i=0; i<nList.getLength(); i++) {
            Node node = nList.item(i);
            Element element = (Element) node;
            
            //ALL KEY CHILDREN
            NodeList nodeListKey = element.getElementsByTagName("key");
            //ALL VALUE CHILDREN
            NodeList nodeListValue = element.getElementsByTagName("value");

            for (int j=0; j<nodeListKey.getLength(); j++) {
                Node key = nodeListKey.item(j);
                Node value = nodeListValue.item(j);

                IDF.put(key.getTextContent(), Double.parseDouble(value.getTextContent()));
            }

        }
        return IDF;
    }
    /**
     * Load data structure with ID
     * @param path of file XML ID
     * @return Map of documents and its absolutly path
     * @throws java.IOException
     * @throws javax.ParserConfigurationException
     * @throws javax.SAXException
     */
    private static HashMap<Integer, String> loadID(String path) throws ParserConfigurationException, SAXException,
            IOException  {
        File fileXML = new File(path);
        HashMap<Integer, String> ID = new HashMap<>();
        
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("document");
        
        for (int i=0; i<nList.getLength(); i++) {
            Node node = nList.item(i);
            Element element = (Element) node;
            
            //ALL KEY CHILDREN
            NodeList nodeListKey = element.getElementsByTagName("key");
            //ALL VALUE CHILDREN
            NodeList nodeListValue = element.getElementsByTagName("value");

            for (int j=0; j<nodeListKey.getLength(); j++) {
                Node key = nodeListKey.item(j);
                Node value = nodeListValue.item(j);

                ID.put(Integer.parseInt(key.getTextContent()), value.getTextContent());
            }

        }
        return ID;
    }
    /**
     * Show documents with name, title and the phrase with a word of the query
     * @param sim similarity between word and frequency
     * @param collection data structured ID with all documents
     * @param size number of document what I go to show
     * @param absolutlyPath collection´s path
     * @param query text with the query
     * @param pathPRF path where I save the result
     * @param emptyWords data structure with empty words
     * @param mostFreq the 5 most frequent words of each document
     * @param IDF data structure with different words and it´s weight
     * @return data structure with words most frequents of n documents
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    public static ArrayList<String> show (HashMap<Integer, Double> sim, HashMap<Integer, String> collection,
            int size, String absolutlyPath, String query, String pathPRF, 
            ArrayList<String> emptyWords, ArrayList<Pair<String, Integer>> mostFreq,
            HashMap<String,Double> IDF) throws  IOException, URISyntaxException {
        
        HashMap<Integer, Double> sortedSim = Similarity.sortByValue(sim);
        int cont=0;
        String path = "";
        String absPath = "";
        String phrase = "";
        String snippetPhrase = "";
        ArrayList<String> output = new ArrayList<>();
        LinkedHashMap<String, String> snippetPath = new LinkedHashMap<>();
        LinkedHashMap<String, String> snippetTitle = new LinkedHashMap<>();
        
        for (Map.Entry<Integer, Double> e : sortedSim.entrySet()) {
            String[] tokens = collection.get(e.getKey()).split(Pattern.quote("\\"));
            if (e.getValue() == 0){
                System.out.println("This query isn´t relevant");
                break;
            }
            for (String t : tokens) {
                path = t;
                absPath = collection.get(e.getKey());
            }
            System.out.println(cont+1+". Document: "+path);
            System.out.println("   a) Similarity: "+e.getValue());
            
            
            snippetPath.put(absPath, path);
            
            //BEGIN PRF
            String txtPRF = "";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathPRF+"\\"+path))) {
                txtPRF = Tokenization.removeHTML(absPath);
                txtPRF = Tokenization.cleanAccents(txtPRF);
                txtPRF = Tokenization.tokenization(txtPRF);
                
                String stemmerTxt = "";
                StringTokenizer tokensPRF = new StringTokenizer(txtPRF);
                while(tokensPRF.hasMoreTokens()){
                    String token = tokensPRF.nextToken();
                    if (!emptyWords.contains(token)){
                        stemmerTxt += token+"\n";
                    }
                }
            bw.write(stemmerTxt);
            }
            mostFreq = PRF.top5(IDF, pathPRF+"\\"+path);
            
            for (int j=0; j<mostFreq.size(); j++){
                output.add(mostFreq.get(j).getFirst());
            }
            
            org.jsoup.nodes.Document doc = Jsoup.parse(new File(absPath), "UTF-8");
            System.out.println("   b) Title: "+doc.title());
            
            Elements text = doc.getElementsByTag("p");
            String[] queries = query.split("\\s+");
            phrase = "";
            
            for (org.jsoup.nodes.Element t : text) {
                String txt = t.text();
                String original = t.text();
                //GET A PHRASE OF THE TEXT
                StringTokenizer st = new StringTokenizer(original,".",true);
                
                
                while (st.hasMoreTokens()) {
                    //GET THE WORDS OF EACH PHRASE
                    original = st.nextToken();
                    txt = Tokenization.tokenization(original.toLowerCase());
                    txt = Stemmer.roots(txt);
                    boolean find = false;
                    
                    for (String q : queries) {
                        if (find && phrase!= null){
                            break;
                        } else if (txt.contains(q)) {
                            int index = txt.indexOf(q);
                            int length = q.length();
                            int line = txt.length();
                            String red = "\u001B[31m"+original.substring(index, index+length).toUpperCase();
                            String snippetWord = "###"+original.substring(index, index+length).toUpperCase()+"###";
                            String reset =  "\u001B[0m";
                            phrase = original.substring(0, index)+red+reset+original.substring(index+length);
                            snippetPhrase = original.substring(0, index)+snippetWord+original.substring(index+length);;
                            find = true;
                            break;
                        }
                    }
                }
            }
            System.out.println("   c) First phrase: "+phrase);
            System.out.println("");
            cont++;
            snippetTitle.put(doc.title(), snippetPhrase);
            if (cont==size){
                break;
            }
        }
        snippet.main(snippetPath, snippetTitle);
        return output;
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
}

