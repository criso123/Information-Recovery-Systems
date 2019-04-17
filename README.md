# Information-Recovery-Systems

It´s a introduction to Natural Processing Language with a collection of files from University of Jaén. 

This project has been created with Netbeans and Java, the structure is:
 - build: Netbeans field created by default
 
 
 - coleccionESuja2017: Collection of articles in html format provided by University of Jaén 
 
 
 - dist: Netbeans field created by default
 
 - index: Creation of a dictionary with different words
 
 - library: Libraries used in the project
 - nbproject: Netbeans field created by default
 - normalizate: Collection of articles without format and with all words that the article contains
 - src: Code source
 - stemmer: Collection of articles with roots of each word from normalizate field
 - stopper: Collection of articles without empty words from stemmer field
 - documentación.pdf: Paper in spanish explain the project and with examples
 - build.xml: Netbeans file created by default
 - conf.data: File with paths that will be passed as parameters
 - manifest.mf: Netbeans file created by default
 - palabrasVacias.txt: Empty words collection
 - Searcher: Field with visual application and the step 6
 
This project is divided in different classes:
  1. Tokenization.java
    1.1. removeHTML function: Remove all HTML tags from collection
    1.2. cleanAccents function: Change all words to lowercase and maintain the letters of the alphabet and numbers
    1.3. tokenization function: Creation of a collection of files where each file contains all words from original collection. Each file contains all words separated by line
  2. Stopper.java
    2.1. loadEmptyWords function: Load a collection of empty words
    2.2. tokenizationAfter function: Remove all empty words from collection created in the step 1.3.
  3. Stemmer.java
    3.1. roots function: Extraction of the root of each word
  4. Index.java
    4.1. loadDataStructure function: Creation of a HashMap showing shortest file and largest file and saving a XML file with all different words
  5. VSM.java
    5.1. highestFrecuency function: Normalize the weights of each word
    5.2. idf function: Return a hashmap with <word, normalized frequency>
    5.3. wij function: Return a hashmap with the weight of each word in collection
    5.4. wnij function: Return a hasmap with the weight of each word in each document
  
  In the Searcher field, it was created to visualize relevant information with a simple application:
  6. Similarity.java
    6.1. sim function: Calculation of similarity between query and collection
    6.2. sortByValue function: Return a sorted map
    
  As improvement, I created a PRF module that it use Pseudo-relevance feedback algorithm
