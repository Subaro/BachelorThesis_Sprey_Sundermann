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
package org.prop4j.analyses.impl.smt;

import org.prop4j.analyses.AbstractSmtSolverAnalysis;
import org.prop4j.solver.IOptimizationSolver;
import org.prop4j.solver.ISmtSolver;
import org.sosy_lab.common.rationals.Rational;

import de.ovgu.featureide.fm.core.job.monitor.IMonitor;

/**
 * TODO description
 *
 * @author User
 */
public class FeatureAttributeMaximumAnalysis extends AbstractSmtSolverAnalysis<Object> {

	private Object variable;

	/**
	 * @param solver
	 */
	public FeatureAttributeMaximumAnalysis(ISmtSolver solver) {
		super(solver);
	}

	/*
	 * (non-Javadoc)
	 * @see org.prop4j.analyses.GeneralSolverAnalysis#analyze(de.ovgu.featureide.fm.core.job.monitor.IMonitor)
	 */
	@Override
	public Object analyze(IMonitor monitor) {

		if ((variable == null) || !(getSolver() instanceof IOptimizationSolver)) {
			return null;
		}
		final IOptimizationSolver solver = (IOptimizationSolver) getSolver();
		getSolver().findSolution();
		Object result = solver.maximum(variable);
		if (result instanceof Rational) {
			result = ((Rational) result).doubleValue();
		}

		return result;
	}

	public void setVariable(Object variable) {
		this.variable = variable;
	}
}
