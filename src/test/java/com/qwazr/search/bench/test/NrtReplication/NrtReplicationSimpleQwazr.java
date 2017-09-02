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
package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.index.QueryDefinition;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NrtReplicationSimpleQwazr extends NrtReplicationBase {

	final QueryDefinition query1 = shortAbstractQuery("a the an then").build();
	final QueryDefinition query2 = shortAbstractQuery("who when where what").build();

	final SummaryStatistics requesterMaster = new SummaryStatistics();
	final SummaryStatistics requesterSlave1 = new SummaryStatistics();
	final SummaryStatistics requesterSlave2 = new SummaryStatistics();

	@Override
	public void postFlush() {

		super.postFlush();

		requesterMaster.addValue(query(master, query1));
		requesterSlave1.addValue(query(slave1, query1));
		requesterSlave2.addValue(query(slave2, query1));

		requesterMaster.addValue(query(master, query2));
		requesterSlave1.addValue(query(slave1, query2));
		requesterSlave2.addValue(query(slave2, query2));

		System.out.println("FLUSHED: " + indexedDocumentsCount.get());
		dump("master", requesterMaster);
		dump("slave1", requesterSlave1);
		dump("slave2", requesterSlave2);
		System.out.println();

	}

	@Override
	public void postCheck() {
	}
}
