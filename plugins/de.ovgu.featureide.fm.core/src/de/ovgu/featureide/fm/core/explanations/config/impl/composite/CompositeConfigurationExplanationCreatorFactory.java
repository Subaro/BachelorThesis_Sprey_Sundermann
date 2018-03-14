/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.explanations.config.impl.composite;

import java.util.Arrays;

import org.prop4j.solvers.impl.javasmt.sat.JavaSmtSatSolverFactory;

import de.ovgu.featureide.fm.core.explanations.config.AutomaticSelectionExplanationCreator;
import de.ovgu.featureide.fm.core.explanations.config.ConfigurationExplanationCreator;
import de.ovgu.featureide.fm.core.explanations.config.ConfigurationExplanationCreatorFactory;
import de.ovgu.featureide.fm.core.explanations.config.impl.mus.MusConfigurationExplanationCreatorFactory;

/**
 * Provides instances of {@link ConfigurationExplanationCreator} using composition.
 *
 * @author Timo G&uuml;nther
 */
public class CompositeConfigurationExplanationCreatorFactory extends ConfigurationExplanationCreatorFactory {

	/** Factory for LTMS. */
	// private final ConfigurationExplanationCreatorFactory ltms = new LtmsConfigurationExplanationCreatorFactory();
	/** Factory for Sat4J MUS. */
	private final MusConfigurationExplanationCreatorFactory musSat4J = new MusConfigurationExplanationCreatorFactory();
	/** Factory for JavaSmt MUS */
	private final MusConfigurationExplanationCreatorFactory musJavaSmt = new MusConfigurationExplanationCreatorFactory(new JavaSmtSatSolverFactory());

	@Override
	public AutomaticSelectionExplanationCreator getAutomaticSelectionExplanationCreator() {
		return new CompositeAutomaticSelectionExplanationCreator(
				Arrays.asList(musJavaSmt.getAutomaticSelectionExplanationCreator(), musSat4J.getAutomaticSelectionExplanationCreator()));
	}
}
