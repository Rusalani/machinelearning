import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.*;

//Gordon Duncan
//cpts 315
//implemented a perceptron alorithum 
public class main {
	public static final int ITERATIONS = 20;

	public static void main(String[] args) throws IOException {
		File trainData = new File("traindata.txt");
		File trainLabel = new File("trainlabels.txt");
		File testData = new File("testdata.txt");
		File testLabel = new File("testlabels.txt");
		File stopwords = new File("stoplist.txt");
		File ocrTest = new File("ocr_test.txt");
		File ocrTrain = new File("ocr_train.txt");

		PrintWriter out = new PrintWriter("output.txt");
		TreeMap<String, Integer> train = new TreeMap<String, Integer>();
		TreeMap<String, Integer> test = new TreeMap<String, Integer>();

		ArrayList<String> badWords = new ArrayList<String>();

		BufferedReader br1 = new BufferedReader(new FileReader(trainData));
		BufferedReader br2 = new BufferedReader(new FileReader(trainLabel));
		BufferedReader br3 = new BufferedReader(new FileReader(testData));
		BufferedReader br4 = new BufferedReader(new FileReader(testLabel));
		BufferedReader br5 = new BufferedReader(new FileReader(stopwords));
		BufferedReader br6 = new BufferedReader(new FileReader(ocrTest));
		BufferedReader br7 = new BufferedReader(new FileReader(ocrTrain));

		String st5 = br5.readLine();
		while ((st5 != null)) {
			badWords.add(st5);
			st5 = br5.readLine();
		}

		ArrayList<String> trainSet = read(br1, br2, badWords);
		ArrayList<String> testSet = read(br3, br4, badWords);
		ArrayList<String> vocab = new ArrayList<String>();

		for (String st3 : trainSet) {
			String[] substring = st3.split(" ");
			for (int i = 0; i < substring.length - 2; i++) {
				if (!vocab.contains(substring[i])) {
					vocab.add(substring[i]);
				}
			}
		}

		vocab.sort(String::compareTo);

		ArrayList<int[]> masterMatrix1 = preprocess(vocab, trainSet);
		ArrayList<int[]> masterMatrix2 = preprocess(vocab, testSet);

		BinaryReturn Bintrain = binaryLeraningAlg(masterMatrix1, trainSet);
		BinaryReturn Bintest = binaryLeraningAlg(masterMatrix2, testSet);
		

		ArrayList<String> trainVocabOCR = readOCR(br7);
		ArrayList<String> testVocabOCR = readOCR(br6);

		ArrayList<int[]> masterMatrix3 = preprocess2(trainVocabOCR);
		ArrayList<int[]> masterMatrix4 = preprocess2(testVocabOCR);

		MutiReturn Mutitrain = learnOCR(masterMatrix3, trainVocabOCR);
		MutiReturn Mutitest = learnOCR(masterMatrix4, testVocabOCR);
		

		out.print("Binary-Classifier\n");
		out.print("Training data\n");
		printMistake(Bintrain.mistakes, out);
		out.print("\n");
		out.print("Testing data\n");
		printMistake(Bintest.mistakes, out);
		out.print("\n");
		out.print("Accuracy\n");
		printAcurracy(Bintrain.getAcc(), Bintest.getAcc(), out);
		out.print("\n");
		out.print("Simple perceptron ");
		out.print(Bintrain.accuracy[Mutitrain.accuracy.length - 1]);
		out.print("% ");
		out.print(Bintest.accuracy[Mutitrain.accuracy.length - 1]);
		out.print("%\n");
		out.print("\n");
		out.print("Multi-Classifier\n");
		out.print("Training data\n");
		printMistake(Mutitrain.mistakes, out);
		out.print("\n");
		out.print("Testing data\n");
		printMistake(Mutitest.mistakes, out);
		out.print("\n");
		out.print("Accuracy\n");
		printAcurracy(Mutitrain.getAcc(), Mutitest.getAcc(), out);
		out.print("\n");
		out.print("Simple perceptron ");
		out.print(Mutitrain.accuracy[Mutitrain.accuracy.length - 1]);
		out.print("% ");
		out.print(Mutitest.accuracy[Mutitrain.accuracy.length - 1]);
		out.print("%\n");
		out.print("\n");
		out.close();
	}

	// Preprocessing for the ocr data
	// geting the 128 binary bits into a struct
	static ArrayList<int[]> preprocess2(ArrayList<String> trainSet) {
		ArrayList<int[]> masterMatrix1 = new ArrayList<int[]>();
		for (String s : trainSet) {
			int[] trainMatrix = new int[128];
			String[] substring = s.split(" ");
			for (int i = 0; i < substring[1].length(); i++) {
				trainMatrix[i] = Integer.parseInt(substring[1].charAt(i) + "");

			}
			masterMatrix1.add(trainMatrix);
		}
		return masterMatrix1;
	}

//Creates and returns a matrix of length of vocab with each collum representing if a certain word is in the row
	static ArrayList<int[]> preprocess(ArrayList<String> vocab, ArrayList<String> trainSet) {
		ArrayList<int[]> masterMatrix1 = new ArrayList<int[]>();
		for (String s : trainSet) {
			int[] trainMatrix = new int[vocab.size()];
			String[] substring = s.split(" ");
			for (int i = 0; i < substring.length - 2; i++) {
				int index = vocab.indexOf(substring[i]);
				if (index != -1) {
					trainMatrix[index] = 1;
				}
			}
			masterMatrix1.add(trainMatrix);
		}
		return masterMatrix1;
	}

	static void printAcurracy(int[] mistakes1, int[] mistakes2, PrintWriter out) throws IOException {
		for (int i = 1; i <= mistakes1.length; i++) {
			out.print("iteration-");
			out.print(i);
			int percent1 = (int) (mistakes1[i - 1]);
			int percent2 = (int) (mistakes2[i - 1]);
			out.print(" ");
			out.print(percent1);
			out.print("% ");
			out.print(percent2);
			out.print("%\n");
		}
	}

	static void printMistake(int[] mistakes, PrintWriter out) throws IOException {
		for (int i = 1; i <= mistakes.length; i++) {
			out.print("iteration-");
			out.print(i);
			out.print(" ");
			out.print(mistakes[i - 1]);
			out.print("\n");
		}
	}

//precptron for the ocr data returns object with all the results
	static MutiReturn learnOCR(ArrayList<int[]> vocab, ArrayList<String> data) {
		double[][] weights = new double[26][vocab.get(0).length];
		int[] mistakes = new int[ITERATIONS];
		for (int i = 0; i < ITERATIONS; i++) {
			int mistake = 0;
			for (int z = 0; z < data.size(); z++) {
				String[] ss = data.get(z).split(" ");
				int ans = (int) (ss[0].charAt(0)) - 97;
				double sum = Integer.MIN_VALUE;
				int pred = 0;

				for (int y = 0; y < weights.length; y++) {
					double dot = dotProd(weights[y], vocab.get(z));
					if (dot > sum) {
						sum = dot;
						pred = y;
					}
				}

				if (pred != ans) {
					mistake++;
					for (int k = 0; k < weights[0].length; k++) {
						weights[ans][k] = weights[ans][k] + vocab.get(z)[k];
						weights[pred][k] = weights[pred][k] - vocab.get(z)[k];

					}

				}

			}
			mistakes[i] = mistake;
		}

		return new MutiReturn(weights, mistakes, data.size());
	}

	// reads the OCR data and stores
	static ArrayList<String> readOCR(BufferedReader br7) throws IOException {
		String ocr = br7.readLine();
		ArrayList<String> ocrTrainSet = new ArrayList<String>();
		while (ocr != null) {
			if (ocr.contains("im")) {
				String[] ocr1 = ocr.split("im");
				String[] ocr2 = ocr1[1].split("\t");
				ocrTrainSet.add(ocr2[1] + " " + ocr2[0]);
			}
			ocr = br7.readLine();
		}
		return ocrTrainSet;
	}

	// Implements binary precptron returns object with all the results

	static BinaryReturn binaryLeraningAlg(ArrayList<int[]> testSet, ArrayList<String> trainSet) {
		double[] weight = new double[testSet.get(0).length];
		int[] mistakes = new int[ITERATIONS];
		for (int i = 0; i < ITERATIONS; i++) {
			int mistake = 0;
			for (int j = 0; j < testSet.size(); j++) {
				double predict = 0;

				predict = dotProd(weight, testSet.get(j));

				int ans = Integer.parseInt(trainSet.get(j).substring(trainSet.get(j).length() - 1));
				if (ans == 0) {
					ans = -1;
				}
				if (predict > 0) {
					predict = 1;
				}
				else if (predict < 0) {
					predict = -1;
				}
				else {
					predict = 0;
				}
				if (predict != ans) {
					for (int k = 0; k < weight.length; k++) {
						weight[k] = weight[k] + ans * testSet.get(j)[k];

					}
					mistake++;
				}

			}
			mistakes[i] = mistake;
		}
		return new BinaryReturn(weight, mistakes, testSet.size());
	}

	public static double sum(double[] list) {
		double sum = 0;
		for (double i : list)
			sum = sum + i;
		return sum;
	}

	public static double dotProd(double[] a, int[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	public static int sum(int[] list) {
		int sum = 0;
		for (int i : list)
			sum = sum + i;
		return sum;
	}

//reads input and stores values
	static ArrayList<String> read(BufferedReader br1, BufferedReader br2, ArrayList<String> badWords)
			throws IOException {
		ArrayList<String> badWords2 = new ArrayList<String>();
		String st1 = br1.readLine();
		String st2 = (br2.readLine());

		while ((st1 != null) && st2 != null) {
			String[] substring = st1.split(" ");
			String cleanedString = "";
			for (String s : substring) {
				boolean bad = false;
				for (String ss : badWords) {
					if (isContain(s, ss)) {
						bad = true;
						break;
					}
				}
				if (!bad) {
					cleanedString += s + " ";
				}
			}
			if (cleanedString.length() != 0) {
				badWords2.add(cleanedString + st2);
			}
			st2 = (br2.readLine());
			st1 = br1.readLine();
		}
		Collections.sort(badWords2);
		return badWords2;
	}

	private static boolean isContain(String source, String subItem) {
		String pattern = "\\b" + subItem + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		return m.find();
	}

}
