package edu.stanford.mobisocial.games.wordplay.constants;

public class LetterValues {
	public static int getLetterValue(char letter){
		switch(letter){
			case 'a':
				return 1;
			case 'b':
				return 4;
			case 'c':
				return 4;
			case 'd':
				return 2;
			case 'e':
				return 1;
			case 'f':
				return 4;
			case 'g':
				return 3;
			case 'h':
				return 4;
			case 'i':
				return 1;
			case 'j':
				return 10;
			case 'k':
				return 5;
			case 'l':
				return 1;
			case 'm':
				return 3;
			case 'n':
				return 1;
			case 'o':
				return 1;
			case 'p':
				return 4;
			case 'q':
				return 10;
			case 'r':
				return 1;
			case 's':
				return 1;
			case 't':
				return 1;
			case 'u':
				return 2;
			case 'v':
				return 4;
			case 'w':
				return 4;
			case 'x':
				return 8;
			case 'y':
				return 4;
			case 'z':
				return 10;
		}
		return 0;
	}

}
