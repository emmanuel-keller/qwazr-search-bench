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

import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.replicator.LocalReplicator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
abstract public class LuceneCommonIndex implements Closeable {

	private final static int MAX_SSD_MERGE_THREADS =
			Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2));

	final Path indexDirectory;
	final Directory dataDirectory;
	final LocalReplicator localReplicator;
	final IndexWriter indexWriter;

	LuceneCommonIndex(final Path rootDirectory, final String schemaName, final String indexName,
			final int ramBufferSize) throws IOException {

		final Path schemaDirectory = Files.createDirectory(rootDirectory.resolve(schemaName));
		this.indexDirectory = Files.createDirectory(schemaDirectory.resolve(indexName));

		this.dataDirectory = FSDirectory.open(indexDirectory.resolve("data"));
		final IndexWriterConfig indexWriterConfig =
				new IndexWriterConfig(new PerFieldAnalyzerWrapper(new StandardAnalyzer()));
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		indexWriterConfig.setRAMBufferSizeMB(ramBufferSize);
		final ConcurrentMergeScheduler mergeScheduler = new ConcurrentMergeScheduler();
		mergeScheduler.setMaxMergesAndThreads(MAX_SSD_MERGE_THREADS, MAX_SSD_MERGE_THREADS);
		indexWriterConfig.setMergeScheduler(mergeScheduler);
		indexWriterConfig.setMergePolicy(new TieredMergePolicy());

		// We use snapshots deletion policy
		final SnapshotDeletionPolicy snapshotDeletionPolicy =
				new SnapshotDeletionPolicy(indexWriterConfig.getIndexDeletionPolicy());
		indexWriterConfig.setIndexDeletionPolicy(snapshotDeletionPolicy);

		this.indexWriter = new IndexWriter(this.dataDirectory, indexWriterConfig);
		this.localReplicator = new LocalReplicator();
	}

	abstract public void commitAndPublish() throws IOException;

	abstract public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException;

	public abstract void updateDocument(FacetsConfig facetsConfig, LuceneRecord record) throws IOException;

	@FunctionalInterface
	public interface FunctionEx<T, R, E extends Exception> {

		R apply(T var1) throws E;
	}

	final class MultiThreadSearcherFactory extends SearcherFactory {

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
