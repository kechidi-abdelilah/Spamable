package com.example.smsspamdetection;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Preprocess {
    private String message;
    private  Context context ;
    public Preprocess(String message ,Context context){
        this.message=message;
        this.context =  context ;


    }
    static Dictionary geek = new Hashtable(){{
        put("ain't", "is not");
        put("aren't", "are not");
        put("can't", "cannot");
        put("can't've", "cannot have");
        put("'cause" ,"because");
        put("could've", "could have");
        put("couldn't", "could not");
        put("couldn't've", "could not have");
        put("didn't", "did not");
        put("doesn't", "does not");
        put("don't" ,"do not");
        put("hadn't", "had not");
        put("hadn't've", "had not have");
        put("hasn't", "has not");
        put("haven't", "have not");
        put("he'd", "he would");
        put("he'd've", "he would have");
        put("he'll", "he will");
        put("he'll've", "he he will have");
        put("he's", "he is");
        put("how'd", "how did");
        put("how'd'y", "how do you");
        put("how'll", "how will");
        put("how's", "how is");
        put("I'd", "I would");
        put("I'd've", "I would have");
        put("I'll", "I will");
        put("I'll've", "I will have");
        put("I'm", "I am");
        put("I've", "I have");
        put("i'd", "i would");
        put("i'd've", "i would have");
        put("i'll", "i will");
        put("i'll've", "i will have");
        put("i'm", "i am");
        put("i've", "i have");
        put("isn't", "is not");
        put("it'd", "it would");
        put("it'd've", "it would have");
        put("it'll", "it will");
        put("it'll've", "it will have");
        put("it's", "it is");
        put("let's", "let us");
        put("ma'am", "madam");
        put("mayn't", "may not");
        put("might've", "might have");
        put("mightn't", "might not");
        put("mightn't've", "might not have");
        put("must've", "must have");
        put("mustn't", "must not");
        put("mustn't've","must not have");
        put("needn't", "need not");
        put("needn't've", "need not have");
        put("o'clock", "of the clock");
        put("oughtn't", "ought not");
        put("oughtn't've", "ought not have");
        put("shan't", "shall not");
        put("sha'n't", "shall not");
        put("shan't've", "shall not have");
        put("she'd", "she would");
        put("she'd've", "she would have");
        put("she'll", "she will");
        put("she'll've", "she will have");
        put("she's", "she is");
        put("should've", "should have");
        put("shouldn't", "should not");
        put("shouldn't've", "should not have");
        put("so've", "so have");
        put("so's", "so as");
        put("that'd", "that would");
        put("that'd've", "that would have");
        put("that's", "that is");
        put("there'd", "there would");
        put("there'd've", "there would have");
        put("there's", "there is");
        put("they'd", "they would");
        put("they'd've", "they would have");
        put("they'll", "they will");
        put("they'll've", "they will have");
        put("they're", "they are");
        put("they've", "they have");
        put("to've", "to have");
        put("wasn't", "was not");
        put("we'd", "we would");
        put("we'd've", "we would have");
        put("we'll", "we will");
        put("we'll've", "we will have");
        put("we're", "we are");
        put("we've", "we have");
        put("weren't", "were not");
        put("what'll", "what will");
        put("what'll've", "what will have");
        put("what're", "what are");
        put("what's", "what is");
        put("what've", "what have");
        put("when's", "when is");
        put("when've", "when have");
        put("where'd", "where did");
        put("where's", "where is");
        put("where've", "where have");
        put("who'll", "who will");
        put("who'll've", "who will have");
        put("who's", "who is");
        put("who've", "who have");
        put("why's", "why is");
        put("why've", "why have");
        put("will've", "will have");
        put("won't", "will not");
        put("won't've", "will not have");
        put("would've", "would have");
        put("wouldn't", "would not");
        put("wouldn't've", "would not have");
        put("y'all", "you all");
        put("y'all'd", "you all would");
        put("y'all'd've", "you all would have");
        put("y'all're", "you all are");
        put("y'all've", "you all have");
        put("you'd", "you would");
        put("you'd've","you would have");
        put("you'll", "you will");
        put("you'll've", "you will have");
        put("you're", "you are");
        put("you've", "you have");
    }};

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String Convert(String message){
        String[] arr = message.split(" ");
        for (int i=0;i< arr.length;i++) {
            if(geek.get(arr[i])!=null) {arr[i]= (String) geek.get(arr[i]);}

        };
        String m = String.join(" ", arr);
        m = m.toLowerCase();
        return m;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static String removeStopWords(String message , Context context) throws IOException {
        BufferedReader reader = null;
        ArrayList<String> stopWords =new ArrayList<>() ;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("english_stopwords.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                stopWords.add(mLine) ;


            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        String stopwordsRegex =stopWords.stream().collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
        String m = message.replaceAll("[^a-z ]","" );
        return m.replaceAll(stopwordsRegex, "").replaceAll(" +", " ");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static ArrayList<Float> nlp(Context co ,String message) throws IOException {
        String m = removeStopWords(Convert(message),co);
        Map<String, Float> map = new HashMap<String, Float>();
        BufferedReader br = null;
        Float i= Float.valueOf(1);
        BufferedReader reader = null;
        try{

            reader = new BufferedReader(
                    new InputStreamReader(co.getAssets().open("ales.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (!mLine.trim().equals("") ) map.put(mLine.trim(), i);
                i=i+1;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                };
            }
        }
        ArrayList<Float> token = new ArrayList<>();
        String[] c=m.split(" ");
        for(int j=0;j<c.length;j++){
            if (map.get(c[j]) != null) token.add(map.get(c[j]));

        }
        return  token;
    }


}
