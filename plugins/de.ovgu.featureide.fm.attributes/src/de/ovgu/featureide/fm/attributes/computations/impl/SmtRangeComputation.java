package de.ovgu.featureide.fm.attributes.computations.impl;

import java.util.ArrayList;
import java.util.List;

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

import de.ovgu.featureide.fm.attributes.base.IFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.ExtendedFeature;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;

public abstract class SmtRangeComputation {

	Configuration config;
	IFeatureAttribute attribute;
	List<String> attIdentifiers = new ArrayList<>();
	List<Term> attVariables = new ArrayList<>();

	protected static final String SUM = "sum";

	public SmtRangeComputation(Configuration config, IFeatureAttribute attribute) {
		this.config = config;
		this.attribute = attribute;
	}

	protected Node buildFormula() {
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
		formula = new And(formula, buildSumNode());
		return formula;

	}

	protected Node buildAttributeNode(IFeatureAttribute att) {
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

	protected Node buildSumNode() {
		if (attribute.getValue() instanceof Long) {
			Variable<LongType> sum = new Variable<LongType>(SUM, new LongType(0));
			return new Equal(sum, Function.sum(attVariables.toArray(new Term[attVariables.size()])));
		} else {
			Variable<DoubleType> sum = new Variable<DoubleType>(SUM, new DoubleType(0));
			return new Equal(sum, Function.sum(attVariables.toArray(new Term[attVariables.size()])));
		}

	}

}
