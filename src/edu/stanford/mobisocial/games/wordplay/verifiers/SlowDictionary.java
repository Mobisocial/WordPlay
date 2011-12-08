package edu.stanford.mobisocial.games.wordplay.verifiers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

import android.content.res.AssetManager;
import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class SlowDictionary implements Dictionary{

    private TreeSet<String> dictionary = new TreeSet<String>();
    
	public SlowDictionary(WordPlayActivity context) throws FileNotFoundException, IOException {
		AssetManager mAssets = context.getAssets();
		InputStream is = mAssets.open("dict/enable1.dat");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String word;
        while ((word = br.readLine()) != null) {
            dictionary.add(word);
        }
	}

	@Override
	public boolean isWord(String word) {
		return dictionary.contains(word);
	}
	
	

}
