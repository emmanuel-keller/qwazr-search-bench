package com.qwazr.search.bench.test.MultiField;

import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class FirstTokenPayloadFilter extends FilteringTokenFilter {

	private BytesRef payload;

	private final PayloadAttribute payAtt = addAttribute(PayloadAttribute.class);
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public FirstTokenPayloadFilter(TokenStream input) {
		super(input);
		payload = null;
	}

	@Override
	final public void reset() throws IOException {
		super.reset();
		payload = null;
	}

	@Override
	protected boolean accept() throws IOException {
		if (payload == null) {
			payload = computePayload();
			return false;
		}
		return true;
	}

	BytesRef computePayload() {
		char c = termAtt.charAt(0);
		assert c >= 48 && termAtt.length() == 1;
		return new BytesRef(new byte[] { (byte) (c - 48) });
	}

	final PayloadSetter newSetter(TokenStream input) {
		return new PayloadSetter(input);
	}

	public final class PayloadSetter extends TokenFilter {

		PayloadSetter(TokenStream input) {
			super(input);
		}

		@Override
		final public boolean incrementToken() throws IOException {
			if (!input.incrementToken())
				return false;
			assert payload != null;
			payAtt.setPayload(payload);
			return true;
		}
	}
}