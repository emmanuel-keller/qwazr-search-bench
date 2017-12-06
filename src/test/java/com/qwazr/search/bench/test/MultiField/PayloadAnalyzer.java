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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;

import java.io.Reader;

final public class PayloadAnalyzer extends Analyzer {

	public PayloadAnalyzer() {
	}

	@Override
	final protected TokenStreamComponents createComponents(final String fieldName) {

		final Tokenizer tokenizer = new UAX29URLEmailTokenizer();
		// Read the payload from the first token
		final FirstTokenPayloadFilter firstTokenPayloadFilter = new FirstTokenPayloadFilter(tokenizer);
		TokenStream stream = new WordDelimiterGraphFilter(firstTokenPayloadFilter,
				WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
						WordDelimiterGraphFilter.SPLIT_ON_NUMERICS | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE |
						WordDelimiterGraphFilter.CATENATE_ALL | WordDelimiterGraphFilter.CATENATE_NUMBERS |
						WordDelimiterGraphFilter.CATENATE_WORDS | WordDelimiterGraphFilter.PRESERVE_ORIGINAL,
				CharArraySet.EMPTY_SET);
		stream = SmartAnalyzerSet.ascii(stream);
		// Set the payload to any token
		stream = firstTokenPayloadFilter.newSetter(stream);
		return new TokenStreamComponents(tokenizer, stream) {
			@Override
			protected void setReader(final Reader reader) {
				super.setReader(reader);
			}
		};
	}



}
