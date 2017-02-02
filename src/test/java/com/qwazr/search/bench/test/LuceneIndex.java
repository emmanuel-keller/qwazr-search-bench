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

import com.qwazr.utils.FunctionUtils;
import com.qwazr.utils.IOUtils;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
public class LuceneIndex implements Closeable {

	private final Directory directory;
	private final IndexWriter indexWriter;
	private final SearcherManager searcherManager;

	LuceneIndex(Path indexDirectory, ExecutorService executorService, int ramBufferSize) throws IOException {
		directory = FSDirectory.open(indexDirectory);
		final IndexWriterConfig indexWriterConfig =
				new IndexWriterConfig(new PerFieldAnalyzerWrapper(new StandardAnalyzer()));
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		indexWriterConfig.setRAMBufferSizeMB(ramBufferSize);
		indexWriterConfig.setMergeScheduler(new SerialMergeScheduler());
		indexWriter = new IndexWriter(directory, indexWriterConfig);
		searcherManager = new SearcherManager(indexWriter,
				executorService == null ? new SearcherFactory() : new MultiThreadSearcherFactory(executorService));
	}

	final public void write(final FunctionUtils.ConsumerEx<IndexWriter, IOException> index) throws IOException {
		index.accept(indexWriter);
		indexWriter.flush();
		indexWriter.commit();
		searcherManager.maybeRefresh();
	}

	final public CheckIndex.Status check() throws IOException {
		return new CheckIndex(directory).checkIndex();
	}

	@FunctionalInterface
	public interface FunctionEx<T, R, E extends Exception> {
		R apply(T var1) throws E;
	}

	final public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException {
		final IndexSearcher searcher = searcherManager.acquire();
		try {
			return search.apply(searcher);
		} finally {
			searcherManager.release(searcher);
		}
	}

	@Override
	public void close() throws IOException {
		IOUtils.close(searcherManager, indexWriter, directory);
	}

	private static class MultiThreadSearcherFactory extends SearcherFactory {

		private final ExecutorService executorService;

		private MultiThreadSearcherFactory(final ExecutorService executorService) {
			this.executorService = executorService;
		}

		public IndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) throws IOException {
			return new IndexSearcher(reader, executorService);
		}
	}
}
