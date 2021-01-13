

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;  



public class SearchEngine {


	public static Hashtable<String, LinkedList<Word>> invertedIndx = new Hashtable<String, LinkedList<Word>>(); // hashtable representing inverted index
	public static Hashtable<String /*stemmed word*/, LinkedList<String>/*List of words which share the same root word*/> porters = new Hashtable<String, LinkedList<String>>(); // hashtable representing the implementation of porters stemming algorithm on inverted index
	public static String corpusFlg = "";  public static String corpusFile = "";
	public static String invIndxFlg = ""; public static String invIndxFile = "";
	public static String stpLstFlg = ""; public static String stpLstFile = "";
	public static String queryFlg = ""; public static String queryFile = "";
	public static String rsltsFlg = ""; public static String rsltsFile = "";
	public static String stemFlg = ""; public static String stemVal = "";
	public static String outputFlg = ""; public static String outputVal = "";
	public static String snipFlg = ""; public static String snipVal = ""; public static int snipIntVal = -1;
	public static boolean usingPorters = false; public static boolean usingGUI = false;
	static Scanner inputScan = new Scanner(System.in);

	static Frame f= new Frame("Main Menu");  // main program frame
	static Frame f2 = new Frame("Search Word"); // search word/document, user input frame/window
	static Frame f3 = new Frame("Query on File"); // Query on inputed file frame/window
	public static void main(String[] args) throws Exception {

		checkFlags(args); // checks command line arguments are correct
		System.out.println("usingPorters: " + usingPorters);
		persistanceCheck(); // checks for persistence regarding the inverted index and the newly created porters data structure
		// Loads objects from file, if object has already been serialized otherwise generates object from corpus again

		//Frame f= new Frame();  
		f.addWindowListener(new WindowAdapter(){ // allows newly created frame/window to be closed via 'X' button event listener
			public void windowClosing(WindowEvent we)
			{
				System.exit(0);
			}
		});

		final Label label = new Label("Welcome to main menu, please choose what you would like to do");          
		label.setAlignment(Label.CENTER);  
		label.setSize(400,100);  
		Button b=new Button("Continue");  
		b.setBounds(250,100,50,20);  
		final Choice c=new Choice();  
		c.setBounds(100, 100, 130, 100);  
		c.add("Query Inputted File");  
		c.add("Calculate Snippets");  
		c.add("Query/Search Word");  
		c.add("Query/Search Doc.");  
		c.add("Terminate Program");  
		f.add(c);f.add(label); f.add(b);  

		f.setSize(600,600);  
		f.setLayout(null);  
		f.setVisible(true);  
		b.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) {       
				String data = c.getItem(c.getSelectedIndex());  
				if(data.equals("Terminate Program")) {

					System.out.println("PROGRAM TERMINATED");
					System.exit(0); // quit program if user chooses too

				}
				else if (data.equals("Query Inputted File")) {

					f3.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
						public void windowClosing(WindowEvent we) {
							f3.dispose();
							f.setVisible(true);
						}
					});

					Label lbl = new Label("Enter Input File You Would Like To Query:");
					lbl.setBounds(5, 0, 300, 100);
					//lbl.setLocation(10, 30);

					TextField t1;  
					t1 = new TextField();  
					t1.setBounds(10, 60, 300, 25);

					Label lbl2 = new Label("Enter Output File You Would Like Query Results To Go:");
					lbl2.setBounds(5, 75, 300, 100);

					TextField t2;  
					t2 = new TextField();  
					t2.setBounds(10, 135, 300, 25);

					Button b = new Button("NEXT");
					b.setBounds(5,175,85,20);

					b.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							System.out.println(t1.getText());
							if(!t1.getText().equals("")) {
								//System.out.println("Word Search");
								//searchWord(t1.getText());
								//f2.dispose();
								//f.setVisible(true);
								String tempQFile = queryFile;
								String tempRFile = rsltsFile;
								queryFile = t1.getText(); // otherwise holds value passed in via command line
								if(!t2.getText().equals("")) {
									rsltsFile = t2.getText();
								}

								f3.dispose();
								System.out.println("\nQuerying on \"" + queryFile + "\" please see \"" + rsltsFile + "\" for results...");
								queryOnFile(queryFile, rsltsFile);
								//System.out.println("File has been Queried");

								queryFile = tempQFile;
								rsltsFile = tempRFile;
							}

						}

					});

					f3.add(t1);
					f3.add(t2);
					f3.add(lbl);
					f3.add(lbl2);
					f3.add(b);
					f3.setSize(600, 600);
					f3.setLayout(null);
					f.setVisible(false);
					f3.setVisible(true);



					/*System.out.println("\nQuerying on \"" + queryFile + "\" please see \"" + rsltsFile + "\" for results...");
					queryOnFile(queryFile, rsltsFile);
					System.out.println("File has been Queried");*/
				}
				else if (data.equals("Calculate Snippets")) {

					if(snipFlg.equals("")) {
						System.out.println("\nERROR: Cannot Calculate Snippet");
						System.out.println("Please Specify Snippet Flag as a Command Line Argument\n Like so \"-Snippet (integer value)\"");
					}
					else {
						Frame f4 = new Frame("Snippet Calculations");

						f4.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
							public void windowClosing(WindowEvent we) {
								f4.dispose();
								f.setVisible(true);
							}
						});

						Label lbl = new Label("Enter Query File You Would Like To Snip:");
						lbl.setBounds(5, 0, 300, 100);
						//lbl.setLocation(10, 30);

						TextField t1;  
						t1 = new TextField();  
						t1.setBounds(10, 60, 300, 25);

						Label lbl2 = new Label("Enter Output File You Would Like Snip Results To Go:");
						lbl2.setBounds(5, 75, 300, 100);

						TextField t2;  
						t2 = new TextField();  
						t2.setBounds(10, 135, 300, 25);

						Button b = new Button("SNIP");
						b.setBounds(5,175,85,20);

						b.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								System.out.println(t1.getText());
								String tempQFile = queryFile;
								String tempRFile = rsltsFile;
								if(!t1.getText().equals("")) {
									//tempQFile = queryFile;
									queryFile = t1.getText();

								}

								if(!t2.getText().equals("")) {
									rsltsFile = t2.getText();
								}
								else
									rsltsFile = "QuerySnippetResults.txt";

								f4.dispose();
								System.out.println("\nCalculating snippets on \"" + queryFile + "\" please see \"" + rsltsFile + "\" for results...");
								snipOnQueryFile(queryFile, rsltsFile, snipIntVal);
								//System.out.println("Snippets Calculated");

								queryFile = tempQFile;
								rsltsFile = tempRFile;

							}

						});

						f4.add(t1);
						f4.add(t2);
						f4.add(lbl);
						f4.add(lbl2);
						f4.add(b);
						f4.setSize(600, 600);
						f4.setLayout(null);
						f.setVisible(false);
						f4.setVisible(true);



					}

				}
				else if (data.equals("Query/Search Word")) {

					f.setVisible(false);
					f2 = new Frame("Search Word");
					f2.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
						public void windowClosing(WindowEvent we) {
							f2.dispose();
							f.setVisible(true);
						}
					});

					Label lbl = new Label("Enter Word you would like to search for:");
					lbl.setBounds(5, 0, 300, 100);
					//lbl.setLocation(10, 30);

					TextField t1;  
					t1 = new TextField();  
					t1.setBounds(10, 60, 300, 25);
					
					Label lbl2 = new Label("Enter Output File You Would Like Search Results To Go:");
					lbl2.setBounds(5, 75, 310, 100);

					TextField t2;  
					t2 = new TextField();  
					t2.setBounds(10, 135, 300, 25);

					Button b = new Button("SEARCH");
					b.setBounds(5,175,85,20);
					b.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							System.out.println(t1.getText());
							if(!t1.getText().equals("")) {
								System.out.println("Word Search");
								
								//f.setVisible(true);
								String wordOutput = "searchWordResults.txt";
								if(!t2.getText().equals("")) {
									wordOutput = t2.getText();
								}
								
								searchWord(wordOutput, t1.getText());
								f2.dispose();

							}

						}

					});


					f2.add(t1);
					f2.add(t2);
					f2.add(lbl);
					f2.add(lbl2);
					f2.add(b);
					f2.setSize(600, 600);
					f2.setLayout(null);
					f2.setVisible(true);

					//   System.out.println("User inputted speed = " + dialog.getSpeed());
				}
				else if (data.equals("Query/Search Doc.")) {

					f.setVisible(false);
					f2 = new Frame("Search Document");
					f2.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
						public void windowClosing(WindowEvent we) {
							f2.dispose();
							f.setVisible(true);
						}
					});

					Label lbl = new Label("Enter document name you would like to search for:");
					lbl.setBounds(5, 0, 300, 100);
					//lbl.setLocation(10, 30);

					TextField t1;  
					t1 = new TextField();  
					t1.setBounds(10, 60, 300, 25);
					
					Label lbl2 = new Label("Enter Output File You Would Like Doc Results To Go:");
					lbl2.setBounds(5, 75, 310, 100);

					TextField t2;  
					t2 = new TextField();  
					t2.setBounds(10, 135, 300, 25);

					Button b = new Button("SEARCH");
					b.setBounds(5,175,85,20);
					b.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							System.out.println(t1.getText());
							if(!t1.getText().equals("")) {
								System.out.println("Document Search");

								
								
								String docOutput = "searchDocResults.txt";
								if(!t2.getText().equals("")) {
									docOutput = t2.getText();
								}
								
								searchDoc(docOutput, t1.getText());
								f2.dispose();
								
								if(!usingGUI)
									f.setVisible(true);
							}

						}

					});


					f2.add(t1);
					f2.add(t2);
					f2.add(lbl);
					f2.add(lbl2);
					f2.add(b);
					f2.setSize(600, 600);
					f2.setLayout(null);
					f2.setVisible(true);

				}
				//label.setText("Operation Selected: " + data);  
			}  
		});   



		//System.out.println(porters.toString());
	}

	// Need QueryFile and ResultsFile
	// Queries the InvertedIndex, using the given QueryFile and outputs to given ResultsFile
	public static void queryOnFile(String qFile, String rFile) { 


		File queryFile = new File(qFile);
		Scanner scan; 
		StringBuilder str = new StringBuilder();
		try {
			scan = new Scanner(queryFile);
			FileWriter resultsFile = new FileWriter(rFile);
			while(scan.hasNext()) {
				String tempLine =  scan.nextLine();

				Scanner scan2 = new Scanner(tempLine);
				scan2.useDelimiter(" ");

				while(scan2.hasNext()) {
					String tempTok = scan2.next();
					//System.out.println("Type: " + tempTok);
					if(tempTok.equals("Query")) { // check if word appears in any doc. if it does output which docs
						//resultsFile.write(tempTok);
						String term = scan2.next();
						//System.out.println("\tWord: " + term);
						try {

							//Object[] wordOccurences = invertedIndx.get(term).toArray(); // array of objects of linkedlist holding word-doc occurences

							LinkedList<Word> tempList = invertedIndx.get(term); //  linkedlist of word objects associated from each word occurence in inverted index
							//System.out.println("Term: " + term + "  numMatches: " + tempList.size());
							String queryRslts = "";
							for(int i = 0; i < tempList.size(); i++) {
								queryRslts = (tempTok + " " + term + " was found in " + tempList.get(i).toString());
								resultsFile.write(queryRslts);
								str.append(queryRslts + "\n");
							}

							/*System.out.println(wordOccurences[0]);
							System.out.println(wordOccurences[1]);
							for(int i = 0; i < wordOccurences.length; i++) {
								queryRslts += (tempTok + " " + term + " was found in " + wordOccurences[i]);
								resultsFile.write(queryRslts);
							}*/
						}
						catch(NullPointerException ex) {
							resultsFile.write(tempTok + " " + term +  " was not found in any document\n");
							str.append(tempTok + " " + term +  " was not found in any document\n");
						}
					}
					else if(tempTok.equals("Frequency")) { // how many times does given word appear in each doc

						String term = scan2.next();
						//System.out.println("\tWord: " + term);

						try {

							//Object[] wordOccurences = invertedIndx.get(term).toArray();

							LinkedList<Word> tempList = invertedIndx.get(term);
							//if(tempList == null) {
							//throw new NullPointerException();
							//}
							int numMatches = tempList.size();
							//System.out.println("IN FREQUENCY");
							//System.out.println("Term: " + term + "  numMatches: " + numMatches);
							String currDoc = "";

							LinkedList<String> usedDocs = new LinkedList<String>();
							for(int i = 0; i < numMatches; i++) {
								long freq = 0;
								currDoc = tempList.get(i).docName;
								if(usedDocs.contains(currDoc) == false) {
									usedDocs.add(currDoc);
									for(int j = 0; j < numMatches; j++) {
										if(tempList.get(j).getDoc().equals(currDoc)) {
											freq++;
										}
									}
									//resultsFile.write( "ugggh " );
									resultsFile.write(tempTok + " " + term +  " was found '" + freq + "' times in document " + currDoc + "\n" );
									str.append(tempTok + " " + term +  " was found '" + freq + "' times in document " + currDoc + "\n" );
								}
							}

							/*String queryRslts = "";
							for(int i = 0; i < wordOccurences.length; i++) {
								queryRslts += (tempTok + " " + term + " was found in " + wordOccurences[i].toString());
								resultsFile.write(queryRslts);
							}*/
						}
						catch(NullPointerException ex) {
							resultsFile.write(tempTok + " " + term +  " was not found in any document\n");
							str.append(tempTok + " " + term +  " was not found in any document\n");

						}
					}
				}
				scan2.close();
			}

			if (usingGUI == true) { // if output command line flag is set to display output to GUI
				f.setVisible(false);
				Frame f1 = new Frame("Query Results");
				f1.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
					public void windowClosing(WindowEvent we) {
						f1.dispose();
						f.setVisible(true);
					}
				});
				TextArea area = new TextArea(str.toString());
				area.setBounds(10, 30, 375, 395);
				f1.add(area);
				f1.setSize(600, 600);
				f1.setLayout(null);
				f1.setVisible(true);
			}
			else
				f.setVisible(true);

			scan.close();
			resultsFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to Open QueryFile AND/OR ResultsFile");
			return;
			//System.exit(0);
			//e.printStackTrace();
		}




	}


	public static void snipOnQueryFile(String qFile, String rFile, int offset) {
		// performs the phase requirement #4
		// searches corpus and for the first occurence of every word, quote the document from 'x' words before and after the
		// specified query term


		// go thru query file, for every query word/term we look thru entire corpus
		// and finds a document snippet of the terms first occurrence 

		StringBuilder str = new StringBuilder();
		File queryFile = new File(qFile);
		Scanner scan; 
		LinkedList<String> usedDocs = new LinkedList<String>(); // Linked List to keep track of unique document names for each term
		try {
			inputScan = new Scanner(queryFile);
			FileWriter resultsFile = new FileWriter(/*"QuerySnippetResults.txt"*/rFile);
			while(inputScan.hasNext()) { // loop through queryFile and getNextLine
				String tempLine =  inputScan.nextLine();

				Scanner scan2 = new Scanner(tempLine);
				scan2.useDelimiter(" ");

				while(scan2.hasNext()) { // for each line break it down to its individual string tokens 

					String tempTok = scan2.next();
					//System.out.println("Type: " + tempTok);
					if(tempTok.equals("Query")) { // check if word appears in any doc. if it does output document snippet
						//resultsFile.write(tempTok);
						String term = scan2.next(); // term/word we are querying for and generating snippet around
						usedDocs = new LinkedList<String>();


						if(usingPorters == true) { // if user has chosen to apply porters stemming in command line the new target word is root word of users input
							ArrayList<String> w = new ArrayList<String>();

							//https://tartarus.org/martin/PorterStemmer/java.txt
							// Stemmer() - comes from the Stemmer.java class which i found on the web for the purpose of applying porters alg.
							// Here we trim the target search word inputed by user, down to its root word and search for the root instead of just the specific word
							Stemmer s = new Stemmer();
							for(int i = 0; i < term.length(); i++) {
								w.add(i, String.valueOf(term.charAt(i)));
								s.add(w.get(i).charAt(0));
								//w[i] = orgWord.charAt(i);
								//s.add(w[i]);
							}
							s.stem();
							String stemmedWord;
							stemmedWord = s.toString();
							//System.out.println(term + " : " + stemmedWord ); 
							term = stemmedWord;
						}


						try {

							//Object[] wordOccurences = invertedIndx.get(term).toArray(); // array of objects of linked list holding word-doc occurrences



							LinkedList<Word> tempList = invertedIndx.get(term); //  linked list of word objects associated from each word occurrence in inverted index
							int size = tempList.size();


							if(size > 0) {
								//System.out.println("\tWORD: " + term + " has " + size + " occurrences");
								/*double wordLoc = tempList.get(0).getIndx(); // position of the query word/term first occurrence in document 
								double strtLoc = wordLoc - offset;
								double endLoc = wordLoc + offset;
								String wordDocument = tempList.get(0).getDoc();
								if(usedDocs.contains(wordDocument)) { // if true then document snippet for that 

								}

								while(strtLoc++ < 0) {
									// if starting location of snippet is before the start of document (i.e, out of bounds)
									// keep incrementing starting index until its valid
								}*/

								// now need to loop through word occurrences and for each unique document given document and compute/print snippet for curr word
								for(int i = 0; i < size; i++) {


									double wordLoc = tempList.get(i).getIndx(); // position of the query word/term first occurrence in document 
									double strtLoc = wordLoc - offset;

									while(strtLoc < 0) {
										strtLoc++;
										// if starting location of snippet is before the start of document (i.e, out of bounds)
										// keep incrementing starting index until its valid
									}
									//boolean stop = false;
									double endLoc = wordLoc + offset;
									String wordDocument = tempList.get(i).getDoc();
									if(!usedDocs.contains(wordDocument)) { // if true then document snippet for that word/term
										resultsFile.write("Query - \"" + term +  "\" found in " + wordDocument + " Snippet : \"");
										str.append("Query - \"" + term +  "\" found in " + wordDocument + " Snippet : \"");
										usedDocs.add(wordDocument); // add to usedDocs list so we dont recompute snippet for same document
										//System.out.println("strt: " + strtLoc + "\nActual Word Loc: " + wordLoc + "\nendLoc: " + endLoc);
										File docFile = new File(corpusFile + "\\" + wordDocument);
										scan = new Scanner(docFile);

										double currWordCnt = 0;

										while(scan.hasNext() /*&& stop == false*/) { // for given term loop through current document and calculate snippet


											String tempDocLine = scan.nextLine();

											tempDocLine = tempDocLine.replaceAll("<[^>]*>", " "); // regex to remove html tags
											tempDocLine = tempDocLine.replaceAll("\\#", " ");
											tempDocLine = tempDocLine.replaceAll("\"", " ");
											tempDocLine = tempDocLine.replaceAll("\'", " ");
											tempDocLine = tempDocLine.replaceAll("\\=", " ");
											tempDocLine = tempDocLine.replaceAll("\\.", " ");
											tempDocLine = tempDocLine.replaceAll("\\,", " ");
											tempDocLine = tempDocLine.replaceAll("\\-", " ");
											tempDocLine = tempDocLine.replaceAll("\\_", " ");
											tempDocLine = tempDocLine.replaceAll("\\[", " ");
											tempDocLine = tempDocLine.replaceAll("\\]", " ");
											tempDocLine = tempDocLine.replaceAll("\\)", " ");
											tempDocLine = tempDocLine.replaceAll("\\(", " ");
											tempDocLine = tempDocLine.replaceAll("\\{", " ");
											tempDocLine = tempDocLine.replaceAll("\\}", " ");
											tempDocLine = tempDocLine.replaceAll("\\:", " ");
											tempDocLine = tempDocLine.replaceAll("\\;", " ");
											tempDocLine = tempDocLine.replaceAll("\\&", " ");
											tempDocLine = tempDocLine.replaceAll("\\s+", " ");


											Scanner scan3 = new Scanner(tempDocLine);
											scan3.useDelimiter(" ");

											while(scan3.hasNext() /*&& (stop == false)*/ ) {
												String tempDocTok = scan3.next();


												if(currWordCnt >= strtLoc && currWordCnt <= endLoc) {
													//System.out.println(tempDocTok + " ");
													resultsFile.write(tempDocTok + " ");
													str.append(tempDocTok + " ");
												}
												currWordCnt++;
											}


										}
										resultsFile.write("\"\n\n");
										str.append("\"\n\n");


									}

								}
							}

						}
						catch(NullPointerException ex) {
							resultsFile.write(tempTok + " " + term +  " was not found in any document\n\n");
							str.append(tempTok + " " + term +  " was not found in any document\n\n");

						}
					}

				}
				scan2.close();

			}


			if (usingGUI == true) { // if output command line flag is set to display output to GUI
				f.setVisible(false);
				Frame f1 = new Frame("Snippet Results");
				f1.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
					public void windowClosing(WindowEvent we) {
						f1.dispose();
						f.setVisible(true);
					}
				});
				TextArea area = new TextArea(str.toString());
				area.setBounds(10, 30, 375, 365);
				f1.add(area);
				f1.setSize(600, 600);
				f1.setLayout(null);
				f1.setVisible(true);
			}
			else {
				f.setVisible(true);
			}



			//inputScan.close();
			resultsFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to Open Specified QueryFile");
			//System.out.println(e.toString());
			f.setVisible(true);
			return;
			//System.exit(0);
			//e.printStackTrace();
		}


		inputScan = new Scanner(System.in); // resets scanner for rest of program
	}


	// need stop list, corpus, indxFile
	//  takes name of stopList, Corpus, and IndexFile
	// stores stoplist in a hashtable called 'hash'
	// populates hashtable called 'invertedIndx' 
	public static void genIndex(String stpList, String corp, String indxFile)  { 

		// HERE WRITE NEW CODE TO CHECK IF INVERTED INDEX IS ALREADY GENERATED & SERIALIZED


		int testCnt = 0;
		//File stopList = new File(".\\stoplist.txt");
		File stopList = new File(stpList);
		Scanner scan = null;
		try {
			scan = new Scanner(stopList);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Could Not Open IndexFile");
			System.exit(0);
			e1.printStackTrace();
		}


		Hashtable<Integer, String> hash = new Hashtable<Integer, String>(); // hashtable storing stoplist
		//Hashtable<String, LinkedList<Word>> invertedIndx = new Hashtable<String, LinkedList<Word>>(); // hashtable representing inverted index

		int numStopWords = 0;
		while (scan.hasNextLine())  {
			hash.put(numStopWords++, scan.nextLine());
		}
		hash.put(numStopWords++, ""); // adds empty string and blank space to stop list
		hash.put(numStopWords++, " ");

		System.out.println("numOfStopWords: " + hash.size());



		//FileWriter myWriter = new FileWriter("filename.txt");
		FileWriter myWriter = null;
		try {
			myWriter = new FileWriter(indxFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could Not Open IndexFile");
			System.exit(0);
			e.printStackTrace();
		}

		//File folder = new File("C:\\Users\\Christopher\\eclipse-workspace\\CS335_Projects\\Prj1\\Corpus"); 
		//File folder = new File("C:\\Users\\Christopher\\eclipse-workspace\\CS335_Projects\\Prj1\\test");
		File folder = new File(corp);
		File[] listOfFiles = folder.listFiles();
		int numDocs = 0;
		String fileCont = "";
		String fileName = "";
		StringBuilder contents = new StringBuilder();
		for(File file : listOfFiles) {
			/*if(testCnt++ == 15) {
				break;
			}*/
			if(file.isFile()) {
				fileName = file.getName();
				System.out.println("Doc#" + numDocs++ + ": " + fileName);
				//numDocs++;

				Scanner scan1 = null;
				try {
					scan1 = new Scanner(file);
				} catch (FileNotFoundException e) {
					System.out.println("Could Not Open Corpus File: " + "'" + fileName + "' from directory path " + corp);
					System.exit(0);
					e.printStackTrace();
				}
				long wordCnt = 0; // number of words from start of document
				while(scan1.hasNextLine()) {


					String str = scan1.nextLine();

					// previously in phase 1 inverted index was very large, here im trying to normalize the html text
					str = str.replaceAll("<[^>]*>", " "); // regex to remove html tags
					str = str.replaceAll("\\#", " ");
					str = str.replaceAll("\"", " ");
					str = str.replaceAll("\'", " ");
					str = str.replaceAll("\\=", " ");
					str = str.replaceAll("\\.", " ");
					str = str.replaceAll("\\,", " ");
					str = str.replaceAll("\\-", " ");
					str = str.replaceAll("\\_", " ");
					str = str.replaceAll("\\[", " ");
					str = str.replaceAll("\\]", " ");
					str = str.replaceAll("\\)", " ");
					str = str.replaceAll("\\(", " ");
					str = str.replaceAll("\\{", " ");
					str = str.replaceAll("\\}", " ");
					str = str.replaceAll("\\:", " ");
					str = str.replaceAll("\\;", " ");
					str = str.replaceAll("\\&", " ");
					str = str.replaceAll("\\s+", " ");

					Scanner scan2 = new Scanner(str);
					scan2.useDelimiter(" ");
					//scan2.useDelimiter("\\s+"); // \\s+ any 1 or more whitespace characters

					while(scan2.hasNext()) {
						//scan2.next();
						//System.out.println(numDocs++ + ": " + fileName);
						String tempWord = scan2.next();
						//myWriter.write("word-" + wordCnt++ + ": " + tempWord + "\n");
						//wordCnt++;
						//System.out.println(scan2.next());
						String wordToAdd = tempWord.toLowerCase();
						if((hash.containsValue(wordToAdd) == false) /*|| (tempWord.equals("") == false)*/) { // meaning word is not a member of stoplist then add to invertedIndex

							/*tempWord = tempWord.replaceAll("<[^>]*>", " "); // regex to remove html tags
							tempWord = tempWord.replaceAll("\\#", " ");
							tempWord = tempWord.replaceAll("\\.", " ");
							tempWord = tempWord.replaceAll("\\-", " ");*/

							if(invertedIndx.containsKey(wordToAdd)) {
								// inverted index already contains word then just add to its list of document occurences
								invertedIndx.get(wordToAdd).add(new Word(wordToAdd, fileName, wordCnt));

							}
							else {
								// otherwise add word to inverted index and update its document info
								invertedIndx.put(wordToAdd, new LinkedList<Word>());
								invertedIndx.get(wordToAdd).add(new Word(wordToAdd, fileName, wordCnt));

							}

						}

						wordCnt++;
					}
				}
				try {
					myWriter.flush();
				} catch (IOException e1) {
					System.out.println("Could Not Open IndexFile");
					System.exit(0);
					e1.printStackTrace();
				}


			}



		}		


		//Serialize Inverted Index for Persistence in future
		String filename = "SerializedInvIndex.ser";
		try
		{    
			//Saving object in a file 
			FileOutputStream file = new FileOutputStream(filename); 
			ObjectOutputStream out = new ObjectOutputStream(file); 

			System.out.println("Serializing InvertedIndex..."); 

			// Method for serialization of object to file
			out.writeObject(invertedIndx); 

			out.close(); 
			file.close(); 
			System.out.println("InvertedIndex has been serialized."); 
		} 
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
			System.out.println("Error in serializing Object");
		}

		//applyPorters();

		scan.close();
		try {
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Could Not Open IndexFile");
			System.exit(0);
			e.printStackTrace();
		}

		applyPorters(); // creates mapping from stemmed word to original words from inverted index

		//Serialize Inverted Index for Persistence in future
		filename = "SerializedPorters.ser";
		try
		{    
			//Saving object in a file 
			FileOutputStream file = new FileOutputStream(filename); 
			ObjectOutputStream out = new ObjectOutputStream(file); 

			System.out.println("Serializing Porters mapping..."); 

			// Method for serialization of object to file
			out.writeObject(porters); 

			out.close(); 
			file.close(); 
			System.out.println("Porters mapping has been serialized."); 
		} 
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
			System.out.println("Error in serializing Object");
		}

		try {
			myWriter = new FileWriter(indxFile);
			myWriter.write(invertedIndx.toString());
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Could Not Open IndexFile");
			System.exit(0);
			e.printStackTrace();
		}

	}




	// will check if .ser files exists if so we load inverted index and porters mapping instead of generating new one
	@SuppressWarnings("unchecked")
	public static void persistanceCheck() {


		boolean ret = false;
		File tempFile = new File("./SerializedInvIndex.ser");
		boolean exists = tempFile.exists();

		if(exists == true) { 
			// Inverted Index is already serialized therefore we load from file

			String filename = "SerializedInvIndex.ser";
			try
			{    
				// Reading the object from a file 
				FileInputStream file = new FileInputStream(filename); 
				ObjectInputStream in = new ObjectInputStream(file); 

				//System.out.println("Size of InvertedIndex: " + invertedIndx.size());
				System.out.println("Deserializing InvertedIndex...");

				// Method for deserialization of Inverted Index object 
				invertedIndx = (Hashtable<String, LinkedList<Word>>)in.readObject(); 

				System.out.println("InvertedIndex has been deserialized "); 
				//System.out.println(invertedIndx.toString()); 

				filename = "SerializedPorters.ser";
				file = new FileInputStream(filename); 
				in = new ObjectInputStream(file); 

				//System.out.println("Size of InvertedIndex: " + invertedIndx.size());
				System.out.println("Deserializing Porters Mapping...");

				// Method for deserialization of Porters Stemming object 
				porters = (Hashtable<String, LinkedList<String>>)in.readObject(); 

				in.close(); 
				file.close(); 

				System.out.println("Porters Mapping has been deserialized "); 

				in.close(); 
				file.close(); 
			} 
			catch(IOException ex) 
			{ 
				System.out.println("IOException is caught"); 
			} 
			catch(ClassNotFoundException ex) 
			{ 
				System.out.println("ClassNotFoundException is caught"); 
			}
			//ret = true;
		}
		else {
			//return false if inverted index is not already serialized and must be generated
			genIndex(stpLstFile, corpusFile, invIndxFile); // generates inverted index, porters mapping, and serializes both data structures for future use
			//return false;
		}

		//return ret;
	}




	public static void applyPorters() { 
		// applies porters algorithm to inverted index
		// creates a mapping from stemmed word to list of words in inverted index

		//long size = invertedIndx.size();
		Iterator hashIterator = invertedIndx.entrySet().iterator(); // entrySet() gets all key value pairs and iterator() returns an iterator

		long cnt = 1;
		while (hashIterator.hasNext()) { // while there is another element in hash map keep looping
			Map.Entry mapElement = (Map.Entry)hashIterator.next(); // returns the next element 
			String currWord = mapElement.getKey().toString(); // original word string before stemming
			//System.out.println(orgWord + " : " + cnt++); 

			// now applying porters algorithm to stemmed word and store references to original word document occurrences

			//String word = "unthinkable";
			//char[] w = new char[501];
			ArrayList<String> w = new ArrayList<String>();

			//https://tartarus.org/martin/PorterStemmer/java.txt
			// Stemmer() - comes from the Stemmer.java class which i found on the web for the purpose of applying porters alg.
			// here we go through inverted Index and create a mapping of stemmed words to list of words in inverted index
			Stemmer s = new Stemmer();
			for(int i = 0; i < currWord.length(); i++) {
				w.add(i, String.valueOf(currWord.charAt(i)));
				s.add(w.get(i).charAt(0));
				//w[i] = orgWord.charAt(i);
				//s.add(w[i]);
			}
			s.stem();
			String stemmedWord;
			stemmedWord = s.toString();
			//System.out.println(currWord + " : " + stemmedWord + " : " + cnt++); 
			//System.out.print(stemmedWord);
			//u = new String(s.getResultBuffer(), 0, s.getResultLength()); 

			if(    !(porters.containsKey(stemmedWord))    )  //add stem word if it doesnt exist yet
				porters.put(stemmedWord, new LinkedList<String>());

			// match original word from inverted index to its corresponding stem word
			porters.get(stemmedWord).add(currWord);
		} 
		//System.out.println(porters.toString());
	}



	// Searches inverted index for the given word and returns with which documents
	// the word appears in and how many times it appear in that document
	public static void searchWord(String outFile,String targetWord) { // 
		StringBuilder str = new StringBuilder(); // string builder used to display to GUI

		//String targetWord = "";
		String whereTo = outFile;
		//Scanner inputScan = new Scanner(System.in);

		//System.out.println("Enter Word you would like to search for...");
		//targetWord = inputScan.nextLine(); // input string represents the target word to search for
		targetWord = targetWord.toLowerCase();
		//System.out.println("Please specify the name of the file you would like the results to be outputted too...");
		//whereTo = inputScan.nextLine();

		LinkedList<String> wordsToSearch = new LinkedList<String>();

		String stemmedWord;
		if(usingPorters == true) { // if user has chosen to apply porters stemming in command line the new target word is root word of users input
			ArrayList<String> w = new ArrayList<String>();

			//https://tartarus.org/martin/PorterStemmer/java.txt
			// Stemmer() - comes from the Stemmer.java class which i found on the web for the purpose of applying porters alg.
			// Here we trim the target search word inputed by user, down to its root word and search for the root instead of just the specific word
			Stemmer s = new Stemmer();
			for(int i = 0; i < targetWord.length(); i++) {
				w.add(i, String.valueOf(targetWord.charAt(i))); 	//w[i] = orgWord.charAt(i);
				s.add(w.get(i).charAt(0));                          //s.add(w[i]);


			}
			s.stem();
			//	String stemmedWord;
			stemmedWord = s.toString();
			//System.out.println(targetWord + " : " + stemmedWord ); 

			//targetWord = stemmedWord;

			wordsToSearch = porters.get(stemmedWord); // linked list of all words sharing the same root word, query for all these words if stemming is used
			if(wordsToSearch == null) {
				wordsToSearch = new LinkedList<String>();
				wordsToSearch.add(targetWord);
			}

			//if(wordsToSearch)
		}
		else {
			wordsToSearch.add(targetWord); 
		}

		//System.out.println(wordsToSearch.size());
		//System.out.flush();
		for(int i = 0; i < wordsToSearch.size(); i++) {
			System.out.println(wordsToSearch.get(i).toString());
			targetWord = wordsToSearch.get(i);

			//inputScan.close();
			try {
				FileWriter resultsFile = new FileWriter(whereTo);
				try {

					LinkedList<Word> tempList = invertedIndx.get(targetWord); //LinkedList of doc occurences for each word throws nullPointer if targetWord not found
					int numMatches = tempList.size();

					String currDoc = "";
					LinkedList<String> usedDocs = new LinkedList<String>(); // Linked List to keep track of unique document names
					for(int ii = 0; ii < numMatches; ii++) { // for all document occurences
						long freq = 0;
						currDoc = tempList.get(ii).docName; 
						if(usedDocs.contains(currDoc) == false) { // check if currDoc has already been if not add to usedDocs and find all occurrences of current document
							usedDocs.add(currDoc);
							for(int j = 0; j < numMatches; j++) {
								if(tempList.get(j).getDoc().equals(currDoc)) {
									freq++;
								}
							}
							resultsFile.write(targetWord +  " was found '" + freq + "' times in the document: " + currDoc + "\n" );
							str.append(targetWord +  " was found '" + freq + "' times in the document: " + currDoc + "\n");
						}

					}

				}
				catch(NullPointerException ex) {
					resultsFile.write(targetWord +  " was not found in any document\n");
					str.append(targetWord +  " was not found in any document\n");
				}

				if (usingGUI == true && ((i + 1) >= wordsToSearch.size())) { // if output command line flag is set to display output to GUI
					f.setVisible(false);
					Frame f1 = new Frame("Query Results");
					f1.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
						public void windowClosing(WindowEvent we) {
							f1.dispose();
							f.setVisible(true);
						}
					});
					TextArea area = new TextArea(str.toString());
					area.setBounds(10, 30, 375, 395);
					f1.add(area);
					f1.setSize(600, 600);
					f1.setLayout(null);
					f1.setVisible(true);
				}
				else {
					f.setVisible(true);
				}



				resultsFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Unable to Open QueryFile AND/OR ResultsFile");
				System.exit(0);
				//e.printStackTrace();
			}
		}


		System.out.println("Please see \"" + whereTo + "\" file for word search results...");
	}


	@SuppressWarnings("unchecked")
	public static void searchDoc(String outFile, String targetDoc) {

		StringBuilder str = new StringBuilder(); // string builder used to display to GUI

		//String targetDoc = "";
		String whereTo = outFile;
		//Scanner inputScan = new Scanner(System.in);
		try {
			FileWriter resultsFile = new FileWriter(whereTo);


			//Scanner inputScan = new Scanner(System.in);
			System.out.println("Enter Document you would like to search for...");
			//targetDoc = inputScan.nextLine(); // input string represents the target word to search for
			targetDoc = targetDoc.toLowerCase();

			/*int currWordCnt = 0;
			String currWord = "";*/
			boolean exists = false;
			Iterator hashIterator = invertedIndx.entrySet().iterator();
			while (hashIterator.hasNext()) { // for every word in inverted index

				int currWordCnt = 0;
				Map.Entry mapElement = (Map.Entry)hashIterator.next();
				String currWord = mapElement.getKey().toString();
				LinkedList<Word> currWordList = (LinkedList<Word>)(mapElement.getValue());

				for(int i = 0; i < currWordList.size(); i++) { 
					Word currWordObj = currWordList.get(i);
					if(currWordObj.getDoc().toLowerCase().equals(targetDoc.toLowerCase())) {
						currWordCnt++;
						exists = true;
					}
					//System.out.println(currWordObj.getData() + " : " + currWordCnt);
				}
				if(currWordCnt != 0) {
					resultsFile.write(currWord + " appears '" + currWordCnt + "' times in " + targetDoc + "\n");
					str.append(currWord + " appears '" + currWordCnt + "' times in " + targetDoc + "\n");
				}

			}
			if(exists == false) {
				resultsFile.write(targetDoc + " not found or no words exist in document\n");
				str.append(targetDoc + " not found or no words exist in document\n");
			}


			if (usingGUI == true) { // if output command line flag is set to display output to GUI
				f.setVisible(false);
				Frame f1 = new Frame("Snippet Results");
				f1.addWindowListener(new WindowAdapter() { // allows newly created frame/window to be closed via 'X' button event listener
					public void windowClosing(WindowEvent we) {
						f1.dispose();
						f2.dispose();
						f.setVisible(true);
					}
				});
				TextArea area = new TextArea(str.toString());
				area.setBounds(10, 30, 375, 365);
				f1.add(area);
				f1.setSize(600, 600);
				f1.setLayout(null);
				f1.setVisible(true);
			}


			resultsFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to create or open searchDocResults.txt for the results of document search");
			e.printStackTrace();
		}
		System.out.println("\nPlease see \"" + whereTo + "\" file for Document search results");
		//inputScan.close();
	}


	public static void checkFlags(String[] args) {
		int argc = args.length;


		for(int i = 0; i < argc; i++) {


			if(args[i].equals("-CorpusDir")) {

				corpusFlg = "-CorpusDir";
				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						corpusFile = args[i+1];
					}
					else {
						System.out.println("Incorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
				/*else {
					return;
				}*/

			}
			else if(args[i].equals("-InvertedIndex")) {
				invIndxFlg = "-InvertedIndex";


				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						invIndxFile = args[i+1];
					}
					else {
						System.out.println("Incorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
				/*else {
					return;
				}*/
			}
			else if(args[i].equals("-StopList")) {
				stpLstFlg = "-StopList";

				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						stpLstFile = args[i+1];
					}
					else {
						System.out.println("Incorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
				/*else {
					return;
				}*/
			}
			else if(args[i].equals("-Queries")) {
				queryFlg = "-Queries";

				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						queryFile = args[i+1];
					}
					else {
						System.out.println("Incorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
				/*else {
					return;
				}*/
			}
			else if(args[i].equals("-Results")) {
				rsltsFlg = "-Results";

				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						rsltsFile = args[i+1];
					}
					else {
						System.out.println("Incorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
				/*else {
					System.out.println("ggg");
					return;
				}*/

			}
			else if(args[i].equals("-Stem")) {
				stemFlg = "-Stem";
				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						stemVal = args[i+1];
						if(stemVal.equals("true")) 
							usingPorters = true; // global variable used to know if user wants stemming enabled
						else
							usingPorters = false;

					}
					else {
						System.out.println("Incorrect Format for Stemming Flag!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}
			}
			else if(args[i].equals("-Snippet")) {
				snipFlg = "-Snippet";

				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						snipVal = args[i+1];
						try {
							snipIntVal = Integer.parseInt(snipVal);
						}
						catch(NumberFormatException ex) {
							System.out.println("Invalid value for snippet flag, -Snippet must be followed by a numerical value");
							System.exit(0);
						}

					}
					else {
						System.out.println("Incorrect Format for Snippet Flag!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}

			}
			else if(args[i].equals("-Output")) {
				outputFlg = "-Snippet";

				if((i+1) < argc) {
					if(args[i+1].charAt(0) != '-') {
						outputVal = args[i+1];

						if(outputVal.equals("gui") || outputVal.equals("GUI") || outputVal.equals("both")) 
							usingGUI = true; // global variable used to know if user wants stemming enabled
						else
							usingGUI = false;

					}
					else {
						System.out.println("Incorrect Format for Snippet Flag!! Flag cannot Proceed a Flag, and a Flag must be followed by a value");
						System.exit(0);
					}
				}

			}

		}

		if(corpusFlg.equals("")  || corpusFile.equals("") || invIndxFlg.equals("") || invIndxFile.equals("") || stpLstFlg.equals("") || stpLstFile.equals("") || queryFlg.equals("") || queryFile.equals("") || rsltsFlg.equals("") || rsltsFile.equals("")) {
			System.out.println("\nIncorrect Format!! Flag cannot Proceed a Flag, and a Flag must be followed by a value\n\nCMD LINE EX:\t"
					+ "java SearchEngine -CorpusDir PathOfDir -InvertedIndex NameOfIIndexFile -StopList NameOfStopListFile -Queries QueryFile -Results ResultsFile");
			System.exit(0);
		}

	}

}
