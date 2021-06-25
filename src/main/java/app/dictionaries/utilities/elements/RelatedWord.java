package app.dictionaries.utilities.elements;

/**
 * RealatedWord - class for find Fuzzysearcher, it doesnt relate to Word class
 */
public class RelatedWord implements Comparable<RelatedWord> {
    private double score;
    private String target;

    /**
     * Constructor
     *
     * @param Score  score of this related word
     * @param Target target of word
     */
    public RelatedWord(double Score, String Target) {
        target = Target;
        score = Score;
    }

    /**
     * Get target
     *
     * @return target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Get score
     *
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * Override compareTo functon
     *
     * @param a other related word
     * @return the comparision
     */
    @Override
    public int compareTo(RelatedWord a) {
        if (this.getScore() - a.getScore() < 0) return 1;
        if (this.getScore() - a.getScore() > 0) return -1;
        return 0;
    }
}
