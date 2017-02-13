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

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by ekeller on 01/01/2017.
 */
public class TtlLoader<T> {

	private final File ttlFile;
	private final TtlLineReader lineReader;
	private final int batchSize;
	private int bufferSize;

	public TtlLoader(File ttlFile, int batchSize) {
		this.ttlFile = ttlFile;
		this.lineReader = new TtlLineReader();
		this.batchSize = batchSize;
		this.bufferSize = 0;
	}

	public int load(final int limit, final Function<TtlLineReader, T> function, final Consumer<T> consumer)
			throws IOException {
		int count = 0;
		try (final FileInputStream fIn = new FileInputStream(ttlFile)) {
			try (final BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(fIn, true)) {
				try (final InputStreamReader isr = new InputStreamReader(bzIn, Charset.forName("UTF-8"))) {
					try (final BufferedReader br = new BufferedReader(isr)) {
						String line;
						while ((line = br.readLine()) != null) {
							if (line.startsWith("#")) // Ignore comments
								continue;
							lineReader.read(line);
							consumer.accept(function.apply(lineReader));
							if (++bufferSize == batchSize) {
								consumer.accept(null);
								bufferSize = 0;
							}
							if (++count == limit)
								break;
						}
						if (bufferSize > 0)
							consumer.accept(null);
						return count;
					}
				}
			}
		}
	}

}
