import java.util.ArrayList;

/**
 * Assignment 6 -- Prisoner's Dilemma -- 2ip90
 * part Patch
 * 
 * @author FILL IN
 * @author FILL IN
 * assignment group FILL IN
 * 
 * assignment copyright Kees Huizing
 */

class Patch {
    private boolean isCooperating;
    private Patch[] neighbours;
    private double alpha;
    private boolean hasSwitched;
    
    // returns true if and only if patch is cooperating
    boolean isCooperating() {
        return this.isCooperating;
    }
    
    // set strategy to C if isC is true and to D if false
    void setCooperating(boolean isC) {
        this.hasSwitched = false;
        this.isCooperating = isC;
    }
    
    // change strategy from C to D and vice versa
    void toggleStrategy() {
        this.hasSwitched = false;
        this.isCooperating = !this.isCooperating;
    }
    
    // return score of this patch in current round
    double getScore() {
        double award = this.isCooperating ? 1 : this.alpha;
        double result = 0;

        for (int x = 0; x < this.neighbours.length; x++) {
            result += this.neighbours[x].isCooperating() ? award : 0;
        }

        return result;
    }

    void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    void setNeighbours(Patch[] neighbours) {
        this.neighbours = neighbours;
    }

    boolean getNextStrategy(boolean preferOwnStrategy) {
        double currentScore = this.getScore();
        double[] neighboursScores = new double[this.neighbours.length];

        double maxNeighboursScore = 0;
        for (int x = 0; x < this.neighbours.length; x++) {
            double score = this.neighbours[x].getScore();
            neighboursScores[x] = score;
            if (maxNeighboursScore < score) {
                maxNeighboursScore = score;
            }
        }

        if (currentScore >= maxNeighboursScore && preferOwnStrategy) {
            return this.isCooperating;
        }

        double maxScore = currentScore > maxNeighboursScore ? currentScore : maxNeighboursScore;

        ArrayList<Boolean> strategiesWithMaxScore = new ArrayList<Boolean>();
        if (currentScore == maxScore) {
            strategiesWithMaxScore.add(this.isCooperating);
        }

        for (int x = 0; x < neighboursScores.length; x++) {
            if (neighboursScores[x] == maxScore) {
                strategiesWithMaxScore.add(this.neighbours[x].isCooperating);
            }
        }

        int randomIndex = PlayingField.random.nextInt(strategiesWithMaxScore.size());

        return strategiesWithMaxScore.get(randomIndex);
    }

    public boolean hasSwitched() {
        return this.hasSwitched;
    }

    public void markAsSwitched() {
        this.hasSwitched = true;
    }
}
