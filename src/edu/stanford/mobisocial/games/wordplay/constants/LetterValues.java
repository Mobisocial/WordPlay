package edu.stanford.mobisocial.games.wordplay.constants;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class LetterValues {
    public static String letterValues = "a:1,b:4,c:4,d:2,e:1,f:4,g:3,h:4,i:1,j:10,k:5," +
            "l:1,m:1,o:1,p:4,q:10,r:1,s:1,t:1,u:2,v:4,w:4,x:8,y:4,z:10, :0";

    public static String classicLetterValues = "a:1,b:3,c:3,d:2,e:1,f:4,g:2,h:4,i:1,j:8,k:5," +
            "l:1,m:3,n:1,o:1,p:3,q:10,r:1,s:1,t:1,u:1,v:4,w:4,x:8,y:4,z:10, :0";

    static final Map<String, HashMap<Character, Integer>> mLetterValueMapCache = 
            new HashMap<String, HashMap<Character, Integer>>();
    
	public static int getLetterValue(String alphabet, char letter) {
	    letter = Character.toLowerCase(letter);
	    try {
    	    if (!mLetterValueMapCache.containsKey(alphabet)) {
                HashMap<Character, Integer> map = new HashMap<Character, Integer>();
                String[] parts = alphabet.split(",");
                for (String part : parts) {
                    String[] val = part.split(":");
                    map.put(val[0].charAt(0), Integer.parseInt(val[1]));
                }
                mLetterValueMapCache.put(alphabet, map);
            }
    
    	    return mLetterValueMapCache.get(alphabet).get(letter);
	    } catch (Throwable t) {
	        Log.e("letters", "no value for " + letter + " in " + alphabet, t);
	        return 0;
	    }
	}
}
