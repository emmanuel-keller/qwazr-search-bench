/*
 * Copyright 2017 Emmanuel Keller / QWAZR
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qwazr.search.bench.test.MultiField;

import com.qwazr.search.analysis.SmartAnalyzerSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.Reader;

final public class PayloadAnalyzer extends Analyzer {

	private static final IntegerEncoder integerEncoder = new IntegerEncoder();

	public PayloadAnalyzer() {
	}

	@Override
	final protected TokenStreamComponents createComponents(final String fieldName) {

		final Tokenizer tokenizer = new UAX29URLEmailTokenizer();

		TokenStream stream = new WordDelimiterGraphFilter(tokenizer,
				WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
						WordDelimiterGraphFilter.SPLIT_ON_NUMERICS | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE |
						WordDelimiterGraphFilter.CATENATE_ALL | WordDelimiterGraphFilter.CATENATE_NUMBERS |
						WordDelimiterGraphFilter.CATENATE_WORDS | WordDelimiterGraphFilter.PRESERVE_ORIGINAL,
				CharArraySet.EMPTY_SET);
		stream = SmartAnalyzerSet.ascii(stream);
		stream = new FirstTokenPayloadFilter(stream);

		return new TokenStreamComponents(tokenizer, stream) {
			@Override
			protected void setReader(final Reader reader) {
				super.setReader(reader);
			}
		};
	}

	public final class FirstTokenPayloadFilter extends FilteringTokenFilter {

		private BytesRef payload;

		private final PayloadAttribute payAtt = addAttribute(PayloadAttribute.class);
		private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

		FirstTokenPayloadFilter(TokenStream input) {
			super(input);
			payload = null;
		}

		@Override
		final public void reset() throws IOException {
			super.reset();
			payload = null;
		}

		@Override
		final protected boolean accept() throws IOException {
			if (payload == null) {
				payload = computePayload();
				return false;
			}
			payAtt.setPayload(payload);
			return true;
		}

		final BytesRef computePayload() {
			char c = termAtt.charAt(0);
			assert c >= 48 && termAtt.length() == 1;
			return new BytesRef(new byte[] { (byte) (c - 48) });
		}

	}

}
