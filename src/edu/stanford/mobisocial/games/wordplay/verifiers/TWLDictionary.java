package edu.stanford.mobisocial.games.wordplay.verifiers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class TWLDictionary implements Dictionary{

	private final String DICTIONARY = "twl";
	private final int fixed_width_length = 15;
	private long numWords = 0;

	private RandomAccessFile referenceFile = null;
	private AssetManager mAssets;
	
	private final String TAG = "TWLDictionary";
	public TWLDictionary(WordPlayActivity context) {
		mAssets = context.getAssets();
		File file = new File(getBaseDirectory(), "ref.dat");
		InputStream in = null;
		if (!file.exists()) {
			try {
				//Log.w(TAG, "ref.dat doesn't exist, creating");
				in = mAssets.open("dict/ref.dat");
				File refFile = new File(getBaseDirectory(), "ref.dat");
				File fileDirectory = new File(getBaseDirectory());
				fileDirectory.mkdirs();
				OutputStream out = new FileOutputStream(refFile);
		    	
				byte[] buffer = new byte[2048];
				int r;
				while ((r = in.read(buffer)) > 0) {
					out.write(buffer, 0, r);
				}
				
				out.close();
				//Log.w(TAG, "ref.dat created");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (Exception e) {
						
					}
				}
			}
		}

		File refFile = new File(getBaseDirectory(), "ref.dat");
		try {
			referenceFile = new RandomAccessFile(refFile, "r");

			numWords = referenceFile.length() / fixed_width_length;
			//Log.w(TAG, "numWords = " + numWords);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getBaseDirectory() {
    	return Environment.getExternalStorageDirectory() +
				"/Android/data/edu.stanford.mobisocial.games.wordplay/cache/" + DICTIONARY;
    }
	
	public String lookUpWord(int index) throws IOException {
		if (referenceFile == null) {
			

			File refFile = new File(getBaseDirectory(), "ref.dat");
			referenceFile = new RandomAccessFile(refFile, "r");
		}
		
		byte[] buffer = new byte[1 + fixed_width_length];
		referenceFile.seek(index * (1 + fixed_width_length));
		//Log.w(TAG, "index: " + index);
		int read = referenceFile.read(buffer);
		if (read != buffer.length) {
			throw new IOException("Error reading word. Read " + read + ", expected " + buffer.length);
		}

		return new String(buffer, 1, Math.min(fixed_width_length, buffer[0]));
	}
	
	public boolean isWord(String word) {
		word = word.toLowerCase();
		if (word.length() < 2) {
			//Log.w(TAG, "too short");
			return false;
		}
		
		try {
			return binarySearch(0, (int)numWords-2, word) >= 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.w(TAG, "how did we get here");
		return false;
	}

	public int binarySearch(int min, int max, String search) throws IOException {
		int mid = 0;
		while (min <= max) {
			mid = (min + max) / 2;
			String midStr = lookUpWord(mid);
			int cmp = midStr.compareTo(search);
			//Log.w(TAG, "comparing " + midStr + " to " + search);
			if (cmp == 0) {
				return mid;
			}
			if (cmp < 0) {
				min = mid + 1;
			} else {
				max = mid - 1;
			}
		}
		//Log.w(TAG, "word not found");
		return -1;
	}

}
