package app.dictionaries.utilities;

import app.dictionaries.utilities.elements.Word;

import java.util.TreeSet;

/**
 * Dictionary
 */
public class Dictionary {
    private TreeSet<Word> wordsContainer;

    /**
     * Default constuctor
     */
    public Dictionary() {
        this.wordsContainer = new TreeSet<Word>();
    }

    /**
     * Get container
     *
     * @return container
     */
    public TreeSet<Word> getWordsContainer() {
        return wordsContainer;
    }

    /**
     * Add Word
     *
     * @param _word word to add
     */
    public void addWord(Word _word) {
        if (_word != null) wordsContainer.add(_word);
        else System.out.println("This word has nothing in it");
    }

    /**
     * Add Word
     *
     * @param _target  target of word
     * @param _explain explanation of word
     */
    public void addWord(String _target, String _explain) {
        wordsContainer.add(new Word(_target, null, _explain));
    }

    /**
     * Add Word
     *
     * @param _target  target of word
     * @param _pronunciation pronunciation of word
     * @param _explain explanation of word
     */
    public void addWord(String _target, String _pronunciation, String _explain) {
        wordsContainer.add(new Word(_target, _pronunciation, _explain));
    }
}
