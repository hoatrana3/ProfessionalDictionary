package app.dictionaries.utilities.elements;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

/**
 * Word
 */
public class Word extends RecursiveTreeObject<Word> implements Comparable<Word> {
    private String target;
    private String pronunciation;
    private String explain;

    /**
     * Default constructor
     */
    public Word() {
        target = explain = pronunciation = "";
    }

    /**
     * Constructor with parameters
     *
     * @param _target  initial target
     * @param _explain initial explanation
     */
    public Word(String _target, String _explain) {
        this();
        setTarget(_target);
        setExplain(_explain);
    }

    /**
     * Constructor with parameters
     *
     * @param _target        initial target
     * @param _pronunciation initial pronunciation
     * @param _explain       initial explanation
     */
    public Word(String _target, String _pronunciation, String _explain) {
        this(_target, _explain);
        setPronunciation(_pronunciation);
    }

    /**
     * Constructor with parameters
     * This constructor will automatically cut data from dictionary into each parts to set
     *
     * @param data in data to cut
     */
    public Word(String data) {
        this();
        if (!data.isEmpty()) {
            data = data.trim();

            // Cut target from data string
            setTarget(data.substring(0, data.indexOf("||")).trim());

            // Remove target in data string
            data = data.substring(data.indexOf("||") + 3).trim();

            // Cut pronunciation from data string
            setPronunciation(data.substring(0, data.indexOf("||")).trim());

            // Remove target in data string
            data = data.substring(data.indexOf("||") + 3).trim();

            // Format data
            data = data.replaceAll("\\|\\|", "\n");
            data = data.replaceAll("\n-", "\n   -");
            data = data.replaceAll("=", "      ví dụ : ");
            data = data.replaceAll("\\+", " → ");
            data = data.replaceAll("\\*", "\n");

            // Set explanation
            setExplain(data);
        }
    }

    /**
     * Set target
     *
     * @param target in target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Get target
     *
     * @return target of this word
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set pronunciation
     *
     * @param pronunciation in pronunciation
     */
    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    /**
     * Get pronunciation
     *
     * @return pronunciation of this word
     */
    public String getPronunciation() {
        return pronunciation;
    }

    /**
     * Set explanation
     *
     * @param explain in explanation
     */
    public void setExplain(String explain) {
        this.explain = explain;
    }

    /**
     * Get explanation
     *
     * @return explain of this word
     */
    public String getExplain() {
        return explain;
    }

    /**
     * Override compareTo function of Comparable for TreeSet sort
     *
     * @param other other word
     * @return comparision value between two Words' targets - 0 for equal, -1 for smaller, 1 for bigger
     */
    @Override
    public int compareTo(Word other) {
        return this.target.toLowerCase().trim().compareTo(other.target.toLowerCase().trim());
    }

    /**
     * Override euqals function of Object java.lang
     *
     * @param other other Object
     * @return if equalss
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Word)
            return this.target.toLowerCase().trim().equalsIgnoreCase(((Word) other).target.toLowerCase().trim());
        else return false;
    }

    /**
     * OVerride toString function of java.lang.Object
     *
     * @return String need to show out
     */
    @Override
    public String toString() {
        return target;
    }

    /**
     * Convert world to data to export
     *
     * @return
     */
    public String toData() {
        String data = target + " ||";
        data = data + "! " + pronunciation + " || ";

        String temp = explain;

        temp = temp.replaceAll("\n", "||");
        temp = temp.replaceAll("(\\s*\\|\\|\\s*)(\\|\\|)+", "$2");
        temp = temp.replaceAll("\\s+-", "-");
        temp = temp.replaceAll("\\s+ví dụ : ", "=");
        temp = temp.replaceAll(" → ", "+ ");

        data = data + temp;
        data = data.replaceAll("\\|\\|\\s+", "||* ");

        return data;
    }
}
