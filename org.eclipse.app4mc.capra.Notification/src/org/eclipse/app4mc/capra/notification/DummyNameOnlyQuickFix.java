package org.eclipse.app4mc.capra.notification;

	import java.io.IOException;

import org.eclipse.app4mc.capra.GenericArtifactMetamodel.ArtifactWrapper;
import org.eclipse.app4mc.capra.GenericArtifactMetamodel.ArtifactWrapperContainer;
import org.eclipse.app4mc.capra.GenericArtifactMetamodel.GenericArtifactMetamodelFactory;
import org.eclipse.app4mc.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.app4mc.capra.core.helpers.ExtensionPointHelper;
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
	 * 
	 * @author Michael Warne
	 *
	 */	

public class DummyNameOnlyQuickFix implements IMarkerResolution {
	
		private URI uri;
		private TracePersistenceAdapter tracePersistenceAdapter;
		private ResourceSet resourceSet;
		private EObject awc;
		private String label;
		private ArtifactWrapper art;
		private Resource resourceForArtifacts;
		private ArtifactWrapperContainer container;
//		private String movedToPath;
		private String fullPath;
		private String fileName;

		DummyNameOnlyQuickFix(String label) {
			this.label = label;
		}
		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void run(IMarker marker) {
			try {
				fullPath = (String) marker.getAttribute("DeltaFullPath");
//				movedToPath = (String)marker.getAttribute("DeltaMovedToPath");
				fileName = (String)marker.getAttribute("FileName");
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			resourceSet = new ResourceSetImpl();
			tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
			awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
			art = GenericArtifactMetamodelFactory.eINSTANCE.createArtifactWrapper();
			uri = EcoreUtil.getURI(awc);
			resourceForArtifacts = resourceSet.createResource(uri);
			EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
			container = (ArtifactWrapperContainer) awc;
			int counter = -1;
			for (ArtifactWrapper aw : list) {
				counter ++;
				String s = aw.getUri().replace("<{", "/");
				s = s.substring(1);
				s = s.replace("<", "/");
				s = s.replace("{", "/");
				s = "/" + s;
				if(s.equals(fullPath)){
					art.setArtifactHandler(aw.getArtifactHandler());
					art.setName(fileName);
					art.setUri(aw.getUri());
					break;
				}												
			}					

			if(art.getUri() != null ){
				container.getArtifacts().set(counter, art);
				resourceForArtifacts.getContents().add(container);
				try {
					resourceForArtifacts.save(null);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			try {
				marker.delete();
			} catch (CoreException e) {
				
				e.printStackTrace();
			}
		}
	}

