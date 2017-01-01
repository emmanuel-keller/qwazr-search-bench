/**
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
package com.qwazr.search.bench.test;

import com.qwazr.search.annotations.Index;
import com.qwazr.search.annotations.IndexField;
import com.qwazr.search.field.FieldDefinition;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Created by ekeller on 01/01/2017.
 */
@Index(name = "shortAbstract", schema = "searchTest", ramBufferSize = BaseTest.RAM_BUFFER_SIZE)
final public class ShortAbstractQwazrRecord {

	@IndexField(name = FieldDefinition.ID_FIELD, template = FieldDefinition.Template.StringField)
	final String url;

	@IndexField(template = FieldDefinition.Template.FacetField)
	final String predicate;

	@IndexField(template = FieldDefinition.Template.TextField, analyzerClass = StandardAnalyzer.class)
	final String shortAbstract;

	public ShortAbstractQwazrRecord() {
		url = null;
		predicate = null;
		shortAbstract = null;
	}

	ShortAbstractQwazrRecord(final TtlLineReader line) {
		url = line.subject;
		predicate = line.predicate;
		shortAbstract = line.object;
	}
}
