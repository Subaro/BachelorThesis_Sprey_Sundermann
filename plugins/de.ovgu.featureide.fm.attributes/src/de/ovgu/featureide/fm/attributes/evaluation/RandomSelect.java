package de.ovgu.featureide.fm.attributes.evaluation;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.jface.action.Action;

import de.ovgu.featureide.fm.attributes.view.FeatureAttributeView;
import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.configuration.ConfigurationEditor;

public class RandomSelect extends Action {

	FeatureAttributeView view;
	private static final String TEMP_TEST = "tempTest";

	public RandomSelect(FeatureAttributeView view) {
		super("", AS_PUSH_BUTTON);
		this.view = view;
		setImageDescriptor(FMUIPlugin.getDefault().getImageDescriptor("icons/attribute_obj.ico"));
	}

	@Override
	public void run() {
		if (view.getCurrentEditor() instanceof ConfigurationEditor) {
			ConfigurationEditor editor = (ConfigurationEditor) view.getCurrentEditor();
			List<SelectableFeature> featList = editor.getConfiguration().getFeatures();
			int featureIndex = ThreadLocalRandom.current().nextInt(0, featList.size());
			SelectableFeature selected = featList.get(featureIndex);
			FMCorePlugin.getDefault().logInfo(selected.getName());

//			for (IFeature feat : editor.getConfiguration().getFeatureModel().getFeatures()) {
//				ExtendedFeature ext = (ExtendedFeature) feat;
//				long random = ThreadLocalRandom.current().nextLong(-200, 0);
//				ext.addAttribute((new LongFeatureAttribute(ext, TEMP_TEST, "", random, false, false)));
//			}

		}
	}
}
