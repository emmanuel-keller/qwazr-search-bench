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
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.replicator.IndexAndTaxonomyRevision;
import org.apache.lucene.replicator.LocalReplicator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
public class LuceneWithTaxonomyIndex extends LuceneCommonIndex {

	final Directory dataDirectory;
	final Directory taxonomyDirectory;
	final LocalReplicator localReplicator;
	public final IndexWriter indexWriter;
	public final IndexAndTaxonomyRevision.SnapshotDirectoryTaxonomyWriter taxonomyWriter;
	public final SearcherTaxonomyManager searcherTaxonomyManager;

	public LuceneWithTaxonomyIndex(final Path rootDirectory, final String schemaName, final String indexName,
			final ExecutorService executorService, final int ramBufferSize) throws IOException {

		Path schemaDirectory = Files.createDirectory(rootDirectory.resolve(schemaName));
		Path indexDirectory = Files.createDirectory(schemaDirectory.resolve(indexName));
		this.dataDirectory = FSDirectory.open(indexDirectory.resolve("data"));
		this.taxonomyDirectory = FSDirectory.open(indexDirectory.resolve("taxonomy"));
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
		this.taxonomyWriter = new IndexAndTaxonomyRevision.SnapshotDirectoryTaxonomyWriter(taxonomyDirectory);
		searcherTaxonomyManager = new SearcherTaxonomyManager(this.indexWriter, true,
				executorService == null ? new SearcherFactory() : new MultiThreadSearcherFactory(executorService),
				this.taxonomyWriter);
		localReplicator = new LocalReplicator();
	}

	@Override
	final public void commitAndPublish() throws IOException {
		taxonomyWriter.getIndexWriter().flush();
		taxonomyWriter.commit();
		indexWriter.flush();
		indexWriter.commit();
		searcherTaxonomyManager.maybeRefresh();
		localReplicator.publish(new IndexAndTaxonomyRevision(indexWriter, taxonomyWriter));
	}

	@Override
	final public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException {
		final SearcherTaxonomyManager.SearcherAndTaxonomy searcherAndTaxonomy = searcherTaxonomyManager.acquire();
		try {
			return search.apply(searcherAndTaxonomy.searcher);
		} finally {
			searcherTaxonomyManager.release(searcherAndTaxonomy);
		}
	}

	@Override
	public <T extends LuceneRecord> void updateDocument(final FacetsConfig facetsConfig, final T record)
			throws IOException {
		indexWriter.updateDocument(record.termId, facetsConfig.build(taxonomyWriter, record.document));
	}

	@Override
	public void close() throws IOException {
		IOUtils.close(searcherTaxonomyManager, taxonomyWriter, indexWriter, taxonomyDirectory, dataDirectory);
	}

}
