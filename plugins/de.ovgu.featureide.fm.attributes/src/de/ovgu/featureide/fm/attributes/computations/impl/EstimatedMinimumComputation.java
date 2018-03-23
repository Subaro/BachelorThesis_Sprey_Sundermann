package de.ovgu.featureide.fm.attributes.computations.impl;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.featureide.fm.attributes.base.IFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.DoubleFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.ExtendedFeature;
import de.ovgu.featureide.fm.attributes.base.impl.LongFeatureAttribute;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.configuration.Configuration;

/**
 * Estimates the minimum of a numerical attribute given a partial configuration Only supposed to be used on numerical attributes
 * 
 * @author Chico Sundermann
 */
public class EstimatedMinimumComputation {

	Configuration config;
	IFeatureAttribute attribute;
	List<IFeature> selectedFeatures;
	List<IFeature> unselectedFeatures;

	public EstimatedMinimumComputation(Configuration config, IFeatureAttribute attribute) {
		this.config = config;
		this.attribute = attribute;
	}

	/**
	 * Estimates the minimum of the value sum regarding a partial configuration
	 * 
	 * @return Minimum
	 */
	public Object getSelectionSum() {
		selectedFeatures = config.getSelectedFeatures();
		unselectedFeatures = config.getUnSelectedFeatures();
		return getSubtreeValue(config.getFeatureModel().getStructure().getRoot().getFeature());
	}

	private double getSubtreeValue(IFeature root) {
		double value = 0;
		ExtendedFeature ext = (ExtendedFeature) root;
		for (IFeatureAttribute att : ext.getAttributes()) {
			if (att.getName().equals(attribute.getName())) {
				if (att instanceof LongFeatureAttribute) {
					if (!(att.getValue() == null)) {
						value += (long) att.getValue();
					}

				} else if (att instanceof DoubleFeatureAttribute) {
					if (!(att.getValue() == null)) {
						value += (double) att.getValue();
					}
				}
			}
		}
		if (!root.getStructure().hasChildren()) {
			return value;
		} else {
			if (root.getStructure().isAnd()) {
				for (IFeatureStructure struc : root.getStructure().getChildren()) {
					double tempValue = getSubtreeValue(struc.getFeature());
					if (struc.isMandatory() || isSelected(struc.getFeature()) || (tempValue < 0 && !isUnselected(struc.getFeature()))) {
						value += getSubtreeValue(struc.getFeature());
					}
				}

			} else if (root.getStructure().isAlternative()) {
				List<Double> values = new ArrayList<>();
				for (IFeatureStructure struc : root.getStructure().getChildren()) {
					if (isSelected(struc.getFeature())) {
						return value + getSubtreeValue(struc.getFeature());
					}
					if (!isUnselected(struc.getFeature())) {
						values.add(getSubtreeValue(struc.getFeature()));
					}
				}
				return value + getMinValue(values);
			} else if (root.getStructure().isOr()) {
				List<Double> values = new ArrayList<>();
				int unselectedCount = 0;
				for (IFeatureStructure struc : root.getStructure().getChildren()) {
					if (isUnselected(struc.getFeature())) {
						unselectedCount++;
					} else {
						double tempValue = getSubtreeValue(struc.getFeature());
						if (isSelected(struc.getFeature()) || tempValue < 0) {
							value += tempValue;
						} else {
							values.add(tempValue);
						}
					}
				}
				if (values.size() + unselectedCount == root.getStructure().getChildrenCount()) {
					return value + getMinValue(values);
				}
			}
		}
		return value;
	}

	private boolean isSelected(IFeature feature) {
		return selectedFeatures.contains(feature);
	}

	private boolean isUnselected(IFeature feature) {
		return unselectedFeatures.contains(feature);
	}

	private double getMinValue(List<Double> values) {
		double min = values.get(0);
		for (double temp : values) {
			if (temp < min) {
				min = temp;
			}
		}
		return min;
	}

}
