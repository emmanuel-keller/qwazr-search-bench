package com.qwazr.search.bench.test.MultiField;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class BooleanPayloadSimilarity extends Similarity {

	private static final Similarity BM25_SIM = new BM25Similarity();

	final float[] boosts;

	public BooleanPayloadSimilarity() {
		boosts = new float[] { 4, 2 };
	}

	@Override
	public long computeNorm(FieldInvertState state) {
		return BM25_SIM.computeNorm(state);
	}

	@Override
	public SimWeight computeWeight(CollectionStatistics collectionStats, TermStatistics... termStats) {
		return new BooleanPayloadSimilarity.BooleanWeight();
	}

	private static class BooleanWeight extends SimWeight {
		float boost = 1f;

		@Override
		public void normalize(float queryNorm, float boost) {
			this.boost = boost;
		}

		@Override
		public float getValueForNormalization() {
			return boost * boost;
		}
	}

	@Override
	final public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {
		final float boost = ((BooleanWeight) weight).boost;

		return new SimScorer() {

			@Override
			public float score(int doc, float freq) {
				return boost;
			}

			@Override
			public Explanation explain(int doc, Explanation freq) {
				Explanation queryBoostExpl = Explanation.match(boost, "query boost");
				return Explanation.match(queryBoostExpl.getValue(),
						"score(" + getClass().getSimpleName() + ", doc=" + doc + "), computed from:", queryBoostExpl);
			}

			@Override
			public float computeSlopFactor(int distance) {
				return 1f;
			}

			@Override
			final public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
				if (payload == null)
					return 1F;
				final int pos = PayloadHelper.decodeInt(payload.bytes, payload.offset);
				return boosts[pos];
			}
		};
	}

}
