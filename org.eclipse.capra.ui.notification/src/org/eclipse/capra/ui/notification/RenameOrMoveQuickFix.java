/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.notification;

import java.io.IOException;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * Renames the associated artifact wrapper in the artifact model to reflect
 * changes in the original resource represented by the wrapper. If the original
 * resource has been renamed or moved, the respective wrapper if renamed to
 * reflect the new name/location of file.
 * 
 * @author Michael Warne
 */
public class RenameOrMoveQuickFix implements IMarkerResolution {

	private String label;

	RenameOrMoveQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		URI uri;
		TracePersistenceAdapter tracePersistenceAdapter;
		ResourceSet resourceSet;
		EObject awc;
		Resource resourceForArtifacts;
		ArtifactWrapperContainer container;

		String movedToPath = null;
		String oldFileName = null;
		String newFileName = null;

		try {
			movedToPath = (String) marker.getAttribute("DeltaMovedToPath");
			oldFileName = (String) marker.getAttribute("oldFileName");
			newFileName = (String) marker.getAttribute("newFileName");
		} catch (CoreException e) {
			e.printStackTrace();
		}

		resourceSet = new ResourceSetImpl();
		tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		uri = EcoreUtil.getURI(awc);
		resourceForArtifacts = resourceSet.createResource(uri);
		EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
		container = (ArtifactWrapperContainer) awc;
		for (ArtifactWrapper aw : list) {
			if (aw.getName().equals(oldFileName)) {
				aw.setName(newFileName);
				aw.setUri(movedToPath);
			}
		}

		resourceForArtifacts.getContents().add(container);
		try {
			resourceForArtifacts.save(null);
		} catch (IOException e) {

			e.printStackTrace();
		}
		try {
			marker.delete();
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}
}
