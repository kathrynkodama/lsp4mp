/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.jdt.internal.core;

import java.util.ArrayList;

import org.eclipse.lsp4mp.jdt.core.IPropertiesCollector.MergingStrategy;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesScope;
import org.eclipse.lsp4mp.commons.metadata.ConfigurationMetadata;
import org.eclipse.lsp4mp.commons.metadata.ItemMetadata;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link PropertiesCollector}.
 *
 * @author Angelo ZERR
 *
 */
public class PropertiesCollectorTest {

	@Test
	public void merge() {
		ConfigurationMetadata configuration = new ConfigurationMetadata();
		PropertiesCollector collector = new PropertiesCollector(configuration,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES);

		ConfigurationMetadata toMerge = createToMerge();
		collector.merge(toMerge);

		Assert.assertEquals(2, configuration.getProperties().size());

		ConfigurationMetadata dupMerge = createDuplicateMerge();
		collector.merge(dupMerge); // implicitly using MergingStrategy.FORCE

		Assert.assertEquals(3, configuration.getProperties().size());
	}

	@Test
	public void mergeWithOnlySources() {
		ConfigurationMetadata configuration = new ConfigurationMetadata();
		PropertiesCollector collector = new PropertiesCollector(configuration,
				MicroProfilePropertiesScope.ONLY_SOURCES);

		ConfigurationMetadata toMerge = createToMerge();
		collector.merge(toMerge);

		Assert.assertEquals(1, configuration.getProperties().size());
	}

	@Test
	public void mergeWithReplace() {
		ConfigurationMetadata configuration = new ConfigurationMetadata();
		PropertiesCollector collector = new PropertiesCollector(configuration,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES);

		ConfigurationMetadata toMerge = createToMerge();
		collector.merge(toMerge, MergingStrategy.REPLACE);

		Assert.assertEquals(2, configuration.getProperties().size());
		Assert.assertNull(configuration.getProperties().get(0).getDescription());

		ConfigurationMetadata dupMerge = createDuplicateMerge();
		collector.merge(dupMerge, MergingStrategy.REPLACE);

		Assert.assertEquals(2, configuration.getProperties().size());
		Assert.assertNotNull(configuration.getProperties().get(1).getDescription());
	}

	@Test
	public void mergeWithIgnore() {
		ConfigurationMetadata configuration = new ConfigurationMetadata();
		PropertiesCollector collector = new PropertiesCollector(configuration,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES);

		ConfigurationMetadata toMerge = createToMerge();
		collector.merge(toMerge, MergingStrategy.IGNORE_IF_EXISTS);

		Assert.assertEquals(2, configuration.getProperties().size());
		Assert.assertNull(configuration.getProperties().get(0).getDescription());

		ConfigurationMetadata dupMerge = createDuplicateMerge();
		collector.merge(dupMerge, MergingStrategy.IGNORE_IF_EXISTS);

		Assert.assertEquals(2, configuration.getProperties().size());
		Assert.assertNull(configuration.getProperties().get(0).getDescription());
	}

	private static ConfigurationMetadata createToMerge() {
		ConfigurationMetadata toMerge = new ConfigurationMetadata();
		toMerge.setProperties(new ArrayList<>());
		// Add a binary metadata
		ItemMetadata binary = new ItemMetadata();
		binary.setName("binaryProperty");
		toMerge.getProperties().add(binary);

		// Add a source metadata
		ItemMetadata source = new ItemMetadata();
		source.setSource(true);
		toMerge.getProperties().add(source);
		return toMerge;
	}

	private static ConfigurationMetadata createDuplicateMerge() {
		ConfigurationMetadata dupMerge = new ConfigurationMetadata();
		dupMerge.setProperties(new ArrayList<>());

		// Add a second binary metadata
		ItemMetadata binary2 = new ItemMetadata();
		binary2.setName("binaryProperty");
		binary2.setDescription("binary property with description");
		dupMerge.getProperties().add(binary2);
		return dupMerge;
	}

}
