package app.dictionaries.utilities.elements;

import app.AppMain;
import app.dictionaries.utilities.Dictionary;
import com.voicerss.tts.*;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.json.JSONArray;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Algorithm manager
 */
public class Algorithms {
    /**
     * Standardlize a String
     *
     * @param s String
     * @return String after standardlize
     */
    public static String standardlize(String s) {
        s = s.trim();
        s = s.replaceAll("\\s+", " ");

        return (s.length() > 0) ? (s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase()) : "";
    }

    /**
     * Look up a word's target in dictionary
     *
     * @param list       target list
     * @param wordToFind word's target to find
     * @return index of the word in dictionary
     */
    public static int lookUpInList(List<Word> list, String wordToFind) {
        return (Collections.binarySearch(list, new Word(wordToFind, ""), new Comparator<Word>() {
            // Override Comparator for search
            @Override
            public int compare(Word w1, Word w2) {
                return (w1.getTarget().trim().compareToIgnoreCase(w2.getTarget().trim()));
            }
        }));
    }

    /**
     * Get a word elemnt from dictionary's treeset
     *
     * @param dictionary input dictionary
     * @param wordToFind word's target to find
     * @return the word
     */
    public static Word getWordInDictionary(Dictionary dictionary, String wordToFind) {
        wordToFind = standardlize(wordToFind);

        ArrayList<Word> arrayList = new ArrayList<>(dictionary.getWordsContainer());

        int index = lookUpInList(arrayList, wordToFind);

        if (index >= 0 && index < arrayList.size()) return arrayList.get(index);

        return null;
    }

    /**
     * Filter a treeset to find all word have target suit regex
     *
     * @param tree  target treeset
     * @param regex regex in use
     * @return filtered treeset
     */
    public static TreeSet<Word> filteredTree(TreeSet<Word> tree, String regex) {
        ArrayList<Word> list = new ArrayList<Word>(tree);

        list = new ArrayList<>(list.stream().filter(word -> {
                    Matcher matcher = Pattern.compile(regex).matcher(word.getTarget());
                    return matcher.find();
                }
        ).collect(Collectors.toList()));

        return new TreeSet<>(list);
    }

    /**
     * This function shows how diffirent between 2 word
     * (used in case there no word match)
     *
     * @param src  source word
     * @param dest destination word
     */

    public static int getLevenshteinDistance(String src, String dest) {
        int[][] d = new int[src.length() + 1][dest.length() + 1];
        int i, j, cost;
        char[] str1 = src.toCharArray();
        char[] str2 = dest.toCharArray();

        for (i = 0; i <= str1.length; i++) d[i][0] = i;
        for (j = 0; j <= str2.length; j++) d[0][j] = j;
        for (i = 1; i <= str1.length; i++) {
            for (j = 1; j <= str2.length; j++) {
                if (str1[i - 1] == str2[j - 1]) cost = 0;
                else cost = 1;

                d[i][j] =
                        Math.min(d[i - 1][j] + 1, java.lang.Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + cost));

                if ((i > 1) && (j > 1) && (str1[i - 1] ==
                        str2[j - 2]) && (str1[i - 2] == str2[j - 1])) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + cost);
                }
            }
        }
        return d[str1.length][str2.length];
    }

    /**
     * callUrlAndParseResult
     *
     * @param langFrom language input
     * @param langTo   language output
     * @param word     word to translate
     * @return translated string
     * @throws Exception
     */
    public static String callUrlAndParseResult(String langFrom, String langTo, String word) throws Exception {
        String url = "https://translate.googleapis.com/translate_a/single?" +
                "client=gtx&" +
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");
        StringBuffer response = new StringBuffer();
        BufferedReader in = null;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(10000);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (Exception e) {
            return "Cannot connect to sever.\nPlease check your internet or reset the dictionary!";
        } finally {
            if (in != null) in.close();
        }

        return parseResult(response.toString());
    }

    /**
     * parseResult
     *
     * @param inputJson inputJson string
     * @return result
     * @throws Exception
     */
    private static String parseResult(String inputJson) {
        JSONArray jsonArray = (JSONArray) (new JSONArray(inputJson).get(0));

        String result = new String();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONArray converter = (JSONArray) jsonArray.get(i);
            result += converter.get(0).toString();
        }

        return result;
    }

    /**
     * Check whether a file is already exist
     *
     * @param path path to file
     * @return whether this file exist
     */
    public static boolean checkFileExist(String path) {
        return (new File(path).exists());
    }

    /**
     * Check connection to a site
     * @param site link website
     * @return connection result
     */
    private static boolean testConnect(String site) {
        try {
            URL url = new URL(site);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Get document as a string from a image
     *
     * @param pathToImage path to image
     * @return string of document scanned
     */
    public static String getDocumentFromImage(String pathToImage) {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath(AppMain.pathToDirectory + "resources/data/tessdata/");
            String text = tesseract.doOCR(new File(pathToImage));

            return text;
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Fail to read text";
        }
    }

    /**
     * Create a new audio file to speech word
     *
     * @param wordToSpeech word to get speech
     */
    public static void createSoundFile(String wordToSpeech) {
        if (!Algorithms.checkFileExist(AudioPlayer.filename + wordToSpeech.replaceAll("\\s+", "") + ".mp3")) {
            File dir = new File(AudioPlayer.filename);

            if (!(dir.exists() && dir.isDirectory())) {
                dir.mkdir();
            }

            VoiceProvider tts = new VoiceProvider("d79498c5f1d141c890bec7b711456137");

            VoiceParameters params = new VoiceParameters(wordToSpeech, Languages.English_GreatBritain);
            params.setCodec(AudioCodec.MP3);
            params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
            params.setBase64(false);
            params.setSSML(false);
            params.setRate(0);

            tts.addSpeechErrorEventListener(new SpeechErrorEventListener() {
                @Override
                public void handleSpeechErrorEvent(SpeechErrorEvent e) {
                    System.out.print(e.getException().getMessage());
                }
            });

            AudioPlayer.currentTarget = wordToSpeech.replaceAll("\\s+", "");

            tts.addSpeechDataEventListener(new SpeechDataEventListener() {
                @Override
                public void handleSpeechDataEvent(SpeechDataEvent e) {
                    try {
                        byte[] voice = (byte[]) e.getData();

                        FileOutputStream fos = new FileOutputStream(AudioPlayer.filename + wordToSpeech.replaceAll("\\s+", "") + ".mp3");

                        fos.write(voice, 0, voice.length);
                        fos.flush();
                        fos.close();
                    } catch (Exception ex) {
                        System.out.println("Cannot find audio file:" + ex);
                    }
                }
            });

            tts.speechAsync(params);
        } else {
            AudioPlayer.currentTarget = wordToSpeech.replaceAll("\\s+", "");
        }
    }

    /**
     * Check network conectivity
     * @return result to connect two website used in dictionary
     */
    public static boolean checkNetwork() {
        return (testConnect("https://translate.google.com.vn") && (testConnect("http://www.voicerss.org")));
    }
}
