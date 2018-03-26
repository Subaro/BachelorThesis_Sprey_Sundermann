package de.ovgu.featureide.fm.attributes.computations.impl;

import java.util.ArrayList;
import java.util.List;

import org.prop4j.Node;
import org.prop4j.analyses.AbstractSolverAnalysisFactory;
import org.prop4j.analyses.impl.smt.FeatureAttributeMinimumAnalysis;
import org.prop4j.solver.impl.SmtProblem;
import org.prop4j.solvers.impl.javasmt.smt.JavaSmtSolver;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.ovgu.featureide.fm.attributes.base.IFeatureAttribute;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class SmtMinimumComputation extends SmtRangeComputation {

	public SmtMinimumComputation(Configuration config, IFeatureAttribute attribute) {
		super(config, attribute);
	}

	public Object getExactMinimum() {
		Node formula = buildFormula();
		List<String> variables = new ArrayList<>();
		variables.addAll(FeatureUtils.getFeatureNamesPreorder(config.getFeatureModel()));
		variables.addAll(attIdentifiers);
		variables.add(SUM);
		SmtProblem maximum = new SmtProblem(formula, variables);

		AbstractSolverAnalysisFactory factory = AbstractSolverAnalysisFactory.getJavaSmtFactory();

		FeatureAttributeMinimumAnalysis analysis = (FeatureAttributeMinimumAnalysis) factory.getAnalysis(FeatureAttributeMinimumAnalysis.class, maximum);
		analysis.setVariable(SUM);
		analysis.getSolver().setConfiguration(JavaSmtSolver.SOLVER_TYPE, Solvers.Z3);
		Object result = LongRunningWrapper.runMethod(analysis, new NullMonitor());

		return result;
	}

}
