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
package com.qwazr.search.bench;

import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
abstract public class LuceneCommonIndex implements Closeable {

	abstract public void commitAndPublish() throws IOException;

	abstract public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException;

	public abstract <T extends LuceneRecord> void updateDocument(FacetsConfig facetsConfig, T record)
			throws IOException;

	@FunctionalInterface
	public interface FunctionEx<T, R, E extends Exception> {

		R apply(T var1) throws E;
	}

	class MultiThreadSearcherFactory extends SearcherFactory {

		private final ExecutorService executorService;

		MultiThreadSearcherFactory(final ExecutorService executorService) {
			this.executorService = executorService;
		}

		@Override
		final public IndexSearcher newSearcher(final IndexReader reader, final IndexReader previousReader)
				throws IOException {
			return new IndexSearcher(reader, executorService);
		}
	}

}
