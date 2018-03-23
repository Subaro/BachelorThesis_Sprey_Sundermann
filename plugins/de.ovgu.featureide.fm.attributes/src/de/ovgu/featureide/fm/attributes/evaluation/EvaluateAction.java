package de.ovgu.featureide.fm.attributes.evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;

import de.ovgu.featureide.fm.attributes.base.IFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.DoubleFeatureAttribute;
import de.ovgu.featureide.fm.attributes.base.impl.ExtendedFeature;
import de.ovgu.featureide.fm.attributes.base.impl.LongFeatureAttribute;
import de.ovgu.featureide.fm.attributes.computations.impl.EstimatedMaximumComputation;
import de.ovgu.featureide.fm.attributes.computations.impl.EstimatedMinimumComputation;
import de.ovgu.featureide.fm.attributes.computations.impl.SmtEvaluation;
import de.ovgu.featureide.fm.attributes.view.FeatureAttributeView;
import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.configuration.ConfigurationEditor;

public class EvaluateAction extends Action {

	FeatureAttributeView view;

	public EvaluateAction(FeatureAttributeView view) {
		super("", AS_PUSH_BUTTON);
		this.view = view;
		setImageDescriptor(FMUIPlugin.getDefault().getImageDescriptor("icons/attribute_obj.ico"));
	}

	@Override
	public void run() {
		if (view.getCurrentEditor() instanceof ConfigurationEditor) {
			ConfigurationEditor editor = (ConfigurationEditor) view.getCurrentEditor();
			FMCorePlugin.getDefault().logInfo(Integer.toString(editor.getConfiguration().getSelectedFeatures().size()));
			Configuration config = editor.getConfiguration();
			IFeatureModel fm = config.getFeatureModel();
			IFeatureAttribute att = null;
			Boolean breakHard = false;
			for (IFeature feat : fm.getFeatures()) {
				ExtendedFeature ext = (ExtendedFeature) feat;
				for (IFeatureAttribute tempAtt : ext.getAttributes()) {
					if (tempAtt instanceof LongFeatureAttribute || tempAtt instanceof DoubleFeatureAttribute) {
						att = tempAtt;
						breakHard = true;
					}
				}
				if (breakHard) {
					break;
				}
			}
			try {
				String rootName = fm.getStructure().getRoot().getFeature().getName();
				FileWriter writer =
					new FileWriter("C:\\Users\\User\\Documents\\Uni_Informatik\\WS1718\\Bachelorarbeit\\Evaluation\\" + rootName + ".txt", true);
				String valueTuple = "";
				BufferedReader br =
					new BufferedReader(new FileReader("C:\\Users\\User\\Documents\\Uni_Informatik\\WS1718\\Bachelorarbeit\\Evaluation\\" + rootName + ".txt"));
				if (br.readLine() == null) {
					writer.write("#Selected,#Unselected,EstMin,MinRun,EstMax,MaxRun,SmtMin,SmtMax,MinRun,MaxRun,OverheadRun");
				}
				br.close();

				Object[] estMax = getMaxEstimationPair(config, att);
				Object[] estMin = getMinEstimationPair(config, att);
				// Object[] smtData = getExactPair(config, att);

				valueTuple += Integer.toString(config.getSelectedFeatures().size());
				valueTuple += "," + Integer.toString(config.getUnSelectedFeatures().size());
				valueTuple += "," + estMin[0].toString() + "," + estMin[1].toString();
				valueTuple += "," + estMax[0].toString() + "," + estMax[1].toString();
				// valueTuple += "," + smtData[0].toString() + "," + smtData[1].toString() + "," + smtData[2].toString() + "," + smtData[3] + "," + smtData[4];
				writer.write(System.lineSeparator() + valueTuple);
				writer.close();
			} catch (IOException e) {
				FMCorePlugin.getDefault().logInfo("I tried so hard");
				e.printStackTrace();
			}

		}
	}

	private Object[] getMaxEstimationPair(Configuration config, IFeatureAttribute att) {
		Object[] result = new Object[2];
		EstimatedMaximumComputation maxComp = new EstimatedMaximumComputation(config, att);
		long startTime = System.currentTimeMillis();
		result[0] = (double) Math.round((double) maxComp.getSelectionSum() * 10d) / 10d;
		result[1] = System.currentTimeMillis() - startTime;
		return result;
	}

	private Object[] getMinEstimationPair(Configuration config, IFeatureAttribute att) {
		Object[] result = new Object[2];
		EstimatedMinimumComputation maxComp = new EstimatedMinimumComputation(config, att);
		long startTime = System.currentTimeMillis();
		result[0] = (double) Math.round((double) maxComp.getSelectionSum() * 10d) / 10d;
		result[1] = System.currentTimeMillis() - startTime;
		return result;
	}

	private Object[] getExactPair(Configuration config, IFeatureAttribute att) {
		SmtEvaluation ranges = new SmtEvaluation(config, att);
		Object[] result = ranges.getSelectionSum();
		return result;
	}
}
