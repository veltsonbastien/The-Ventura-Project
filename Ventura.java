/*
 * Project Started: August 19th, 2018
 *  Notes: 
 *  August 19th, 2018: Project Started
 *  August 20th, 2018: Figured out how to finally add in the open nlp library, and download the appropriate files, make sure you add in all jars to the userlib in bluej
 *  August 21st, 2018: Working on the doCommand() function 
 *  August 22nd, 2018: Updated getNouns() function to better search for numbers such as "one hundred and forty two" 
 *  August 24th, 2018: Working on different types of commands: questions vs commands, etc
 */

import java.util.*; 
import java.io.*; 
import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.util.Iterator; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel; 
import opennlp.tools.util.model.BaseModel;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;

public class Ventura 
{
    
    static ParsedCommandObject p = new ParsedCommandObject();    
    private static String command = "";
    static ArrayList<functionDescriptor> descriptors = new ArrayList(); 
    static boolean includesIntegers = false; 
    static boolean includesDoubles  = false;
    static boolean knownCommand = false; 
    static ArrayList<Integer> outstandingIntegers = new ArrayList(); 
    static ArrayList<Double> outstandingDoubles   = new ArrayList(); 
    static String[] knownCommands = {"play", "open"};  
    
     public static void main (String args[])
     { 
       addDescriptors(); 
  
       System.out.println("Please Say Something: ");     
         
        Scanner sc = new Scanner(System.in); 
        command = sc.nextLine();   
        //the two main functions 
        parseCommand(command); 
        doCommand(p);  
        
      } //end of main 
    
   
    public static void addDescriptors()
    { 
     //make all new descriptors here   
     functionDescriptor add = new functionDescriptor("add", new String[] {"add","sum","plus"} );
     functionDescriptor subtract = new functionDescriptor("subtract",new String[] {"subtract","minus","remove"} ); 
     functionDescriptor multiply = new functionDescriptor("multiply", new String[] {"multiplied by","times"} ); 
     functionDescriptor divide  = new functionDescriptor("divide", new String[] {"divide", "divided by"} ); 
     
     //add them all to the arrayList here 
     descriptors.add(add); 
     descriptors.add(subtract);
     descriptors.add(multiply);
     descriptors.add(divide); 
     
    }//end of create descriptors 
      
    public static String chooseFunction(String s) 
    {
     String chosenKeyword = "Function Not Found"; 
     String temp = s;      
     System.out.println("Just checking in here, we are at the chooooseFunction, function, BY THE WAY WE HAVE: "+chosenKeyword ); 
     
     for(functionDescriptor f: descriptors) 
     {
      String[] tempArray = f.getSecondaryDescriptors();    
         
      for(int i = 0; i<tempArray.length; i++)
       {
         if(tempArray[i].equals(temp) ) 
          {
          System.out.println("Found the descriptor "+temp+" in the "+f.getPrimaryDescriptor()+" category!");     
          chosenKeyword = f.getPrimaryDescriptor();
          return chosenKeyword; 
          } //end of inner if
       } //end of inner for loop checking the strings in the secondary descriptor  
     } //end of outer for each loop
     
     return chosenKeyword; 
        
    } //end of choose function
          
    public static String chooseKnownFunction(String s)
     {
      String chosenKeyword = "Function not found"; 
      String temp = s; 
      
      for(String descriptor: knownCommands) 
       {
        if(descriptor.equals(temp) )
         {
          System.out.println("Found the descriptor "+temp+" in the "+descriptor+" category!");     
          chosenKeyword = descriptor;
          return chosenKeyword;    
          }
           
       }//end of outer for each
      
      return chosenKeyword; 
     } //end of choose known function
      
      
    public static String getVerbs(String checkForVerbs)
    {
     String result = "";    
     String chosenVerb = ""; 
 
        InputStream tokenModelIn = null;
        InputStream posModelIn = null;
        
        try {
            String sentence = checkForVerbs;
            // tokenize the sentence
            tokenModelIn = new FileInputStream("en-token.bin");
            TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            String tokens[] = tokenizer.tokenize(sentence);
            // Parts-Of-Speech Tagging
            // reading parts-of-speech model to a stream
            posModelIn = new FileInputStream("en-pos-maxent.bin");
            // loading the parts-of-speech model from stream
            POSModel posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            POSTaggerME posTagger = new POSTaggerME(posModel);
            // Tagger tagging the tokens
            String tags[] = posTagger.tag(tokens);
            System.out.println("Verbs\t:\tTag\t:\tProbability\n---------------------------------------------");
            
            for(int i=0;i<tokens.length;i++){
              
                //check if it is a known command first     
                for(int j = 0; j<knownCommands.length; j++) 
                 {
                  if(tokens[i].equals(knownCommands[j])) 
                   {
                     knownCommand = true;   
                     chosenVerb = tokens[i];   
                     result = chooseKnownFunction(chosenVerb); 
                     return result; 
                    } //end of it the token equals a known command
                 }
                 
                if( ( tags[i].equals("VB") || tags[i].equals("CC") || tags[i].equals("IN") || tags[i].equals("RB") || tags[i].equals("VBN") )&& !(tokens[i].equals("and")) )
                 {
                chosenVerb = tokens[i];  //IMPORTANT SETTING THE VERB HERE 
                 } //end of checking if it is a verb
                System.out.println(tokens[i]+"\t:\t"+tags[i]+"\t:\t");
            }
             System.out.println("CHOSEN VERB: "+chosenVerb);      
        }
        catch (IOException e) {
            // Model loading failed, handle the error
            e.printStackTrace();
        }
        finally {
            if (tokenModelIn != null) {
                try {
                    tokenModelIn.close();
                }
                catch (IOException e) {
                }
            }
            if (posModelIn != null) {
                try {
                    posModelIn.close();
                }
                catch (IOException e) {
                }
            }
        }
     
      result = chooseFunction(chosenVerb);  
      
      return result; 
      
    } //end of getVerbs 
    
    
    public static ArrayList getNouns(String checkForNouns)
    {
     ArrayList<String> result = new ArrayList(); 
     
     int integerCount = 0; 
     int doubleCount = 0;
     
       InputStream tokenModelIn = null;
       InputStream posModelIn = null;
        
        try {
            
            String sentence = checkForNouns;
            // tokenize the sentence
            tokenModelIn = new FileInputStream("en-token.bin");
            TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            String tokens[] = tokenizer.tokenize(sentence);
            posModelIn = new FileInputStream("en-pos-maxent.bin");
            POSModel posModel = new POSModel(posModelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String tags[] = posTagger.tag(tokens);
            for(int i=0;i<tokens.length;i++){
                
              if( i<tokens.length-1 && tags[i].equals("CD")) 
              { 
               int iterationCount = tokens.length-i; 
               int counter = 0; 
               int tempIndex = i; 
               String multiWordNumber = "";     
               while(counter < iterationCount &&  ( (tags[i+(counter)].equals("CD") ) ) )
                 {
                  multiWordNumber+=tokens[tempIndex]+" "; 
                  System.out.println(multiWordNumber);
                  tempIndex++;
                  counter++;   
                 }//end of while loop 
               result.add(multiWordNumber);
               integerCount++;
               i+=counter-1;          
              }//checking if the tag is equal CD     
              else if(tags[i].equals("CD")) { result.add(tokens[i]+" "); integerCount++; } 
              
              if(tags[i].equals("NNP") || tags[i].equals("NNS")  || tags[i].equals("NN") ) 
                  { result.add(tokens[i]);  }   
   
            } //end of for loop going through 
    
        }
        catch (IOException e) {
            // Model loading failed, handle the error
            e.printStackTrace();
        }
        finally {
            if (tokenModelIn != null) {
                try {
                    tokenModelIn.close();
                }
                catch (IOException e) {
                }
            }
            if (posModelIn != null) {
                try {
                    posModelIn.close();
                }
                catch (IOException e) {
                }
            }
        }   
        //handle the counts here:     
        if(doubleCount > 0 ) { includesDoubles = true; } 
        else if(integerCount == result.size()){includesIntegers = true; System.out.println("I AM TRURRREEEE" ); } 
        
     return result; 
        
    } //end of getNouns
    
    public static ArrayList integerArray(ArrayList stringArr) //TO BE UPDATED SOON 
    {
     ArrayList<String> temp = stringArr;    
     ArrayList<Integer> newArr = new ArrayList(); 
     
     for(String s1: temp)
     {
      String[] splitStrings = s1.split(" ");    
      int result = 0;
      int finalResult = 0; 
  
      for(String s: splitStrings) 
       {
           
      switch(s) 
      {
       case "one": result+= 1; break;
       case "two": result+= 2; break;
       case "three": result+= 3; break;
       case "four": result+= 4; break;
       case "five": result+= 5; break;
       case "six": result+= 6; break;
       case "seven": result+= 7; break;
       case "eight": result+= 8; break;
       case "nine": result+= 9; break;
       case "ten": result+= 10; break;
       case "eleven": result+= 11; break;
       case "twelve": result+= 12; break;
       case "thirteen": result+= 13; break;
       case "fourteen": result+= 14; break;
       case "fifteen": result+= 15; break;
       case "sixteen": result+= 16; break;
       case "seventeen": result+= 17; break;
       case "eighteen": result+= 18; break;
       case "nineteen": result+= 19; break;
       case "twenty": result+= 20; break;
       case "thirty": result+= 30; break;
       case "forty": result+= 40; break;
       case "fifty": result+= 50; break;
       case "sixty": result+= 60; break;
       case "seventy": result+= 70; break;
       case "eighty": result+= 80; break;
       case "ninety": result+= 90; break;
       case "hundred": result*= 100; break;
       case "thousand": result*= 1000; finalResult+=result; result = 0; break;
       case "million": result*=1000000; finalResult+=result; result = 0; break;
       case "billion": result*=1000000000; finalResult+=result; result = 0; break;
       default: result += 0;  break; 
 
      }//end of inner switch   
   
      }
      finalResult+=result; 
      newArr.add(finalResult);   
     }//end of inner for each loop
     return newArr;
        
    } //end of integer array
    
    public static ArrayList doubleArray (ArrayList arr) 
    {
     ArrayList<String> temp = arr; 
     ArrayList<Double> newArr = new ArrayList(); 
     
       for(String s: temp)
     {
      double result = 0.0; 
      
      switch(s) 
      {
       case "one":    result+= 1.0;   break;
       case "two":    result+= 2.0;   break; 
       case "three":  result+= 3.0;   break;
       case "four":   result+= 4.0;   break;
       case "five":   result+= 5.0;   break;
       case "six":    result+= 6.0;   break;
       case "seven":  result+= 7.0;   break;
       case "eight":  result+= 8.0;   break;
       case "nine":   result+= 9.0;   break;
       case "ten":    result+= 10.0;  break;
       default:       result += 0.0;  break;
      }//end of inner switch  
      
      newArr.add(result);  

    }//end of double array
    
    return newArr; 

    }
  
     public static void parseCommand(String s)
     {
       String temp = s;   
         
       p.setKeyword(getVerbs(temp)); 
       p.setCommandNouns(getNouns(temp)); 
       p.showObject();   
       
     }// end of parseCommand
     
     public static void doCommand(ParsedCommandObject p)
     {
       String finalKeyword = p.getKeyword(); 
       ArrayList<String> finalCommandNouns = p.getCommandNouns(); 
       
       //check for functions here: 
       
       if(includesIntegers) 
       {
        outstandingIntegers = integerArray(finalCommandNouns); 
       }//end of if there is integers
       else if(includesDoubles)
       {
        outstandingDoubles = doubleArray(finalCommandNouns); 
       }//end of it there is doubles
       
       //Do the functions here: 
       switch(finalKeyword) 
       {
         case "add" : System.out.println("The answer is: "+ add(outstandingIntegers) ); break; 
         case "subtract": System.out.println("The answer is: "+subtract(outstandingIntegers) ); break; 
         case "multiply": System.out.println("The answer is: "+multiply(outstandingIntegers) ); break; 
         default: 
           System.out.println("No function has been made for me yet!" ); 
         break;  
        }//end of the important switch
       
       
     } //end of doCommand 
     
     /**
     *ALL THE MAJOR FUNCTIONS GO HERE 
     *DOCUMENT FUNCTION NAME AND DATE CREATED WHEN MADE: 
     *Important: MAKE SURE FUNCTION NAME IS EASILY CORRESPONDABLE TO MAIN KEYWORD!
     *Add Function Name, and Keyword to it's corresponding functionDescriptors class!
     **/
     
     
     public static int add(ArrayList arr) 
     {
      ArrayList<Integer> temp = arr;    
      int result = 0; 
         
      for(Integer i: temp)
       {
       result+= i; 
       }//end of simple for loop
       
      return result; 
     }//end of add; 
     
     
     public static int subtract(ArrayList arr) 
     {
      ArrayList<Integer> temp = arr;    
      int result = 0; 
      
      result = temp.get(0)-temp.get(1); 
      
      return result; 
     }//end of subtract; 
     
     public static int multiply(ArrayList arr) 
     {
      ArrayList<Integer> temp = arr;    
      int result = 1; 
      
      for(Integer i: temp)
      {
        result*= i; 
      }
      
      return result; 
     }//end of subtract; 
     
}

class ParsedCommandObject
{
  String keyword = ""; 
  ArrayList<String> command_nouns = new ArrayList(); 
  
  public ParsedCommandObject()
  {
    keyword = "add"; 
    command_nouns.add("1");  
  } //default constructor
  
  public ParsedCommandObject(String s)
   {
    this.keyword = s; 
   }
  
  public ParsedCommandObject(String s, ArrayList arr)
  {
    this.keyword = s; 
    this.command_nouns = arr; 
  } // specific constructor
    
  public void setKeyword(String s) { this.keyword = s; }
  public void setCommandNouns(ArrayList arr) { this.command_nouns = arr; }
  public String getKeyword() { return this.keyword; } 
  public ArrayList getCommandNouns() { return this.command_nouns; } 
  
  public void showObject()
  {
   System.out.println("HERE IS THE OBJECT: "+ "\n" + 
                      "Current Keyword is: "+this.keyword + "\n"+
                      "Current Noun Set is: "+this.command_nouns); 
                  
  }//end of show object, for testing purposes
} //end of parsedCommandObject


class functionDescriptor
{

  String primary_descriptor = ""; 
  String[] secondary_descriptors = {}; 
   
  public functionDescriptor()
   {
      primary_descriptor = "Ooops"; 
      secondary_descriptors = new String[] {"Something bad...most likely"}; 
       
    }//end of default constructor 
    
  public functionDescriptor(String s, String[] sArr) 
  {
    this.primary_descriptor = s; 
    this.secondary_descriptors = sArr;   
  }
  
  void setPrimaryDescriptor(String s) { this.primary_descriptor = s; } 
  void setSecondaryDescriptors(String[] sArr) { this.secondary_descriptors = sArr; }
  
  public String getPrimaryDescriptor() { return this.primary_descriptor; } 
  public String[] getSecondaryDescriptors() { return this.secondary_descriptors; } 

   
} //end of function descriptor 
