package de.ovgu.featureide.fm.attributes.computations.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.prop4j.And;
import org.prop4j.Constant;
import org.prop4j.DoubleType;
import org.prop4j.Equal;
import org.prop4j.Function;
import org.prop4j.Implies;
import org.prop4j.Literal;
import org.prop4j.LongType;
import org.prop4j.Node;
import org.prop4j.Not;
import org.prop4j.Term;
import org.prop4j.Variable;
import org.prop4j.analyses.AbstractSolverAnalysisFactory;
import org.prop4j.analyses.impl.FeatureAttributeRangeAnalysis;
import org.prop4j.solver.impl.SmtProblem;
import org.prop4j.solvers.impl.javasmt.smt.JavaSmtSolver;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.ovgu.featureide.fm.attributes.base.IFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.DoubleFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.ExtendedFeature;
import de.ovgu.featureide.fm.attributes.base.impl.LongFeatureAttribute;
import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;
import de.ovgu.featureide.fm.ui.views.outline.IOutlineEntry;

public class SmtMaximumComputation implements IOutlineEntry {

	Configuration config;
	IFeatureAttribute attribute;
	List<String> attIdentifiers = new ArrayList<>();
	List<Term> attVariables = new ArrayList<>();

	private static final String LABEL = "Range of the value (exact): ";
	private static final String SUM = "sum";

	public SmtMaximumComputation(Configuration config, IFeatureAttribute attribute) {
		this.config = config;
		this.attribute = attribute;
	}

	@Override
	public String getLabel() {
		Object[] result = getSelectionSum();
		if (attribute instanceof LongFeatureAttribute) {
			result[0] = ((Double) result[0]).longValue();
			result[1] = ((Double) result[1]).longValue();
		}
		return LABEL + result[0].toString() + "-" + result[1].toString();
	}

	@Override
	public Image getLabelImage() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public List<IOutlineEntry> getChildren() {
		return null;
	}

	@Override
	public boolean supportsType(Object element) {
		return attribute instanceof LongFeatureAttribute || attribute instanceof DoubleFeatureAttribute;
	}

	@Override
	public void setConfig(Configuration config) {
		this.config = config;

	}

	private Object[] getSelectionSum() {
		long startTime = System.currentTimeMillis();
		Node formula = buildFormula();
		List<String> variables = new ArrayList<>();
		variables.addAll(FeatureUtils.getFeatureNamesPreorder(config.getFeatureModel()));
		variables.addAll(attIdentifiers);
		variables.add(SUM);
		SmtProblem maximum = new SmtProblem(formula, variables);

		AbstractSolverAnalysisFactory factory = AbstractSolverAnalysisFactory.getJavaSmtFactory();

		FeatureAttributeRangeAnalysis analysis = (FeatureAttributeRangeAnalysis) factory.getAnalysis(FeatureAttributeRangeAnalysis.class, maximum);
		analysis.setVariable(SUM);
		analysis.getSolver().setConfiguration(JavaSmtSolver.SOLVER_TYPE, Solvers.Z3);
		Object result = LongRunningWrapper.runMethod(analysis, new NullMonitor());
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		FMCorePlugin.getDefault().logInfo("Smt Ranges: " + Long.toString(duration));
		return (Object[]) result;

	}

	private Node buildFormula() {
		Node formula = config.getFeatureModel().getAnalyser().getCnf();
		for (IFeature feat : config.getSelectedFeatures()) {
			formula = new And(formula, new Literal(feat.getName()));
		}
		for (IFeature feat : config.getUnSelectedFeatures()) {
			formula = new And(formula, new Not(feat.getName()));
		}

		for (IFeature feat : config.getFeatureModel().getFeatures()) {
			if (feat instanceof ExtendedFeature) {
				ExtendedFeature ext = (ExtendedFeature) feat;
				for (IFeatureAttribute att : ext.getAttributes()) {
					if (att.getName().equals(attribute.getName())) {
						formula = new And(formula, buildAttributeNode(att));
					}
				}
			}
		}
		formula = new And(formula, getSumNode());
		return formula;

	}

	private Node buildAttributeNode(IFeatureAttribute att) {
		String identifier = att.getFeature().getName() + "$" + att.getName();
		Node impl1;
		Node impl2;
		if (att.getValue() instanceof Long) {
			Variable<LongType> attributeVar = new Variable<LongType>(identifier, new LongType(0));
			impl1 = new Implies(att.getFeature().getName(), new Equal(attributeVar, new Constant<LongType>(new LongType((long) att.getValue()))));
			impl2 = new Implies(new Not(att.getFeature().getName()), new Equal(attributeVar, new Constant<LongType>(new LongType(0))));
			attVariables.add(attributeVar);
		} else if (att.getValue() instanceof Double) {
			Variable<DoubleType> attributeVar = new Variable<DoubleType>(identifier, new DoubleType(0));
			impl1 = new Implies(att.getFeature().getName(), new Equal(attributeVar, new Constant<DoubleType>(new DoubleType((double) att.getValue()))));
			impl2 = new Implies(new Not(att.getFeature().getName()), new Equal(attributeVar, new Constant<DoubleType>(new DoubleType(0))));
			attVariables.add(attributeVar);
		} else {
			Variable<DoubleType> attributeVar = new Variable<DoubleType>(identifier, new DoubleType(0));
			impl1 = new Implies(att.getFeature().getName(), new Equal(attributeVar, new Constant<DoubleType>(new DoubleType(0))));
			impl2 = new Implies(new Not(att.getFeature().getName()), new Equal(attributeVar, new Constant<DoubleType>(new DoubleType(0))));
			attVariables.add(attributeVar);
		}

		attIdentifiers.add(identifier);
		return new And(impl1, impl2);
	}

	private Node getSumNode() {
		if (attribute.getValue() instanceof Long) {
			Variable<LongType> sum = new Variable<LongType>(SUM, new LongType(0));
			return new Equal(sum, Function.sum(attVariables.toArray(new Term[attVariables.size()])));
		} else {
			Variable<DoubleType> sum = new Variable<DoubleType>(SUM, new DoubleType(0));
			return new Equal(sum, Function.sum(attVariables.toArray(new Term[attVariables.size()])));
		}

	}

}
