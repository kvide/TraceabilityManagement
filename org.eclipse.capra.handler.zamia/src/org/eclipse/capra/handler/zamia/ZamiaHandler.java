/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Scilla Systems - Handler for zamiaCAD
 *******************************************************************************/
package org.eclipse.capra.handler.zamia;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.zamia.ASTNode;

/**
 * Handler to allow tracing to and from VHDL design entities. Uses zamiaCAD as
 * the foundation.
 */
public class ZamiaHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		return selection instanceof ASTNode;
	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		ASTNode astNode = (ASTNode) selection;
		String uri = Long.toString(astNode.getDBID());
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), uri, uri);
		return wrapper;
	}

	@Override
	public Object resolveArtifact(EObject artifact) {
		// TODO Auto-generated method stub
		return null;
	}

}
