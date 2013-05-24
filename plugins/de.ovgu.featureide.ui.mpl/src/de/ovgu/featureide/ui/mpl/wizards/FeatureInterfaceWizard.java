/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
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
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.ui.mpl.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.ovgu.featureide.ui.mpl.MPLUIPlugin;

/**
 * A wizard for creating MPL interfaces.
 * 
 * @author Reimar Schroeter
 */
public class FeatureInterfaceWizard extends Wizard implements IWorkbenchWizard {
	public static final String ID = MPLUIPlugin.PLUGIN_ID + ".wizards.InterfaceWizard";

	private InterfacePage interfacePage;
	private ChooseFolderPage folderPage;
	
	public FeatureInterfaceWizard(String title) {
		super();
		setWindowTitle(title);
	}

	@Override
	public void addPages() {
		interfacePage = new InterfacePage();
		folderPage = new ChooseFolderPage();
		addPage(interfacePage);
		addPage(folderPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		return true;
	}
	
	public int getConfigLimit() {
		return interfacePage.getConfigLimit();
	}
	
	public int getViewLevel() {
		return interfacePage.getViewLevel();
	}
	
	public String getViewName() {
		return interfacePage.getViewName();
	}
	
	public String getFolderName(){
		return folderPage.getFolderName();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}
