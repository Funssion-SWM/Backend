package Funssion.Inforum.common.constant;

public enum ScoreType {
    MAKE_MEMO(50L),
    MAKE_QUESTION(30L),
    MAKE_ANSWER(20L),
    SELECT_ANSWER(20L),
    BEST_ANSWER(80L),
    MAKE_COMMENT(5L),
    LIKE(10L);

    private final Long score;


    ScoreType(Long score) {
        this.score = score;
    }

    public Long getScore(){
        return score;
    }

}
