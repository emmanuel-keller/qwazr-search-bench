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

/**
 * Created by ekeller on 01/01/2017.
 */
final public class TtlLineReader {

	public String subject;
	public String predicate;
	public String object;

	private int searchPos;

	public void read(final String line) {
		searchPos = 0;
		subject = next('<', '>', line);
		predicate = next('<', '>', line);
		object = next('"', '"', line);
	}

	private String next(final char startChar, final char endChar, final String line) {

		final int start = line.indexOf(startChar, searchPos) + 1;
		if (start == 0)
			throw new IllegalArgumentException();

		searchPos = start;
		int end;
		do {
			end = line.indexOf(endChar, searchPos);
			if (end == -1)
				return null;
			if (line.charAt(end - 1) != '\\')
				break;
			searchPos = end + 1;
		} while (end != -1);

		if (end == -1)
			throw new IllegalArgumentException();

		searchPos = end + 1;
		return line.substring(start, end);

	}
}
