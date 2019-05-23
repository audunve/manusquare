package similarity.SimilarityMethodologies;

import similarity.SimilarityMethods;

public class SimilarityFactory {
    public static ISimilarity GenerateSimilarityMethod(SimilarityMethods method) {
        switch (method) {
            case WU_PALMER:
                return new WuPalmerSimilarity();
            case RESNIK:
                return new ResnikSimilarity();
            case LIN:
                return new LinSimilarity();
            default:
                throw new UnsupportedOperationException("Invalid similarity method selected to compute similarity :: " + method);
        }
    }
}
