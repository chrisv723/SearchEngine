

//my own defined class called Word
//used to store word name, document name, and index of terms from Inverted Index 
public class Word implements java.io.Serializable {
	
	String data;
	String docName;
	long wordIndx;
	
	
	Word() {
		this.data = "";
		this.docName = "";
		this.wordIndx = -1;
	}
	
	Word(String word, String doc, long indx) {
		this.data = word;
		this.docName = doc;
		this.wordIndx = indx;
	}
	
	String getData() {
		return data;
	}
	
	String getDoc() {
		return docName;
	}
	
	double getIndx() {
		return wordIndx;
	}
	
	public String toString() {
		return (" (File: " + docName + "  Index: " + wordIndx + ")\n");
	}
}
