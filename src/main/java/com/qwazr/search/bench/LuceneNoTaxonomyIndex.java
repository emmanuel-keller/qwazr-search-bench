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

import com.qwazr.utils.IOUtils;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.replicator.IndexRevision;
import org.apache.lucene.replicator.LocalReplicator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
public class LuceneNoTaxonomyIndex extends LuceneCommonIndex {

	final Directory dataDirectory;
	final LocalReplicator localReplicator;
	public final IndexWriter indexWriter;
	public final SearcherManager searcherManager;

	public LuceneNoTaxonomyIndex(final Path rootDirectory, final String schemaName, final String indexName,
			final ExecutorService executorService, final int ramBufferSize) throws IOException {

		Path schemaDirectory = Files.createDirectory(rootDirectory.resolve(schemaName));
		Path indexDirectory = Files.createDirectory(schemaDirectory.resolve(indexName));
		this.dataDirectory = FSDirectory.open(indexDirectory.resolve("data"));
		final IndexWriterConfig indexWriterConfig =
				new IndexWriterConfig(new PerFieldAnalyzerWrapper(new StandardAnalyzer()));
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		indexWriterConfig.setRAMBufferSizeMB(ramBufferSize);
		indexWriterConfig.setMergeScheduler(new SerialMergeScheduler());
		indexWriterConfig.setMergePolicy(new TieredMergePolicy());

		// We use snapshots deletion policy
		final SnapshotDeletionPolicy snapshotDeletionPolicy =
				new SnapshotDeletionPolicy(indexWriterConfig.getIndexDeletionPolicy());
		indexWriterConfig.setIndexDeletionPolicy(snapshotDeletionPolicy);

		this.indexWriter = new IndexWriter(this.dataDirectory, indexWriterConfig);
		searcherManager = new SearcherManager(this.indexWriter,
				executorService == null ? new SearcherFactory() : new MultiThreadSearcherFactory(executorService));
		localReplicator = new LocalReplicator();
	}

	@Override
	final public void commitAndPublish() throws IOException {
		indexWriter.flush();
		indexWriter.commit();
		searcherManager.maybeRefresh();
		localReplicator.publish(new IndexRevision(indexWriter));
	}

	@Override
	final public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException {
		final IndexSearcher indexSearcher = searcherManager.acquire();
		try {
			return search.apply(indexSearcher);
		} finally {
			searcherManager.release(indexSearcher);
		}
	}

	@Override
	public <T extends LuceneRecord> void updateDocument(final FacetsConfig facetsConfig, final T record)
			throws IOException {
		indexWriter.updateDocument(record.termId, facetsConfig.build(record.document));
	}

	@Override
	public void close() throws IOException {
		IOUtils.close(searcherManager, indexWriter, dataDirectory);
	}

}
