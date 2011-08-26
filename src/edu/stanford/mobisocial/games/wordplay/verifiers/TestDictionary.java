package edu.stanford.mobisocial.games.wordplay.verifiers;

import android.util.Log;

public class TestDictionary implements Dictionary{
	
	public TestDictionary() {
		
	}

	@Override
	public boolean isWord(String word) {
		Log.w("TestDictionary", "Checking: " + word);
		if(word.length() < 3) {
			return false;
		}
		return true;
	}
	
	

}
