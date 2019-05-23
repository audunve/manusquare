package similarity.SimilarityMethodologies;

import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParameters;

public interface ISimilarity<P extends SimilarityParameters> {
    public double ComputeSimilarity(P params);
}
