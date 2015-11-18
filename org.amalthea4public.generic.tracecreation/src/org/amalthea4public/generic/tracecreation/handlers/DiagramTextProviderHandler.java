package org.amalthea4public.generic.tracecreation.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.amalthea4public.generic.tracecreation.metamodel.trace.adapter.TraceCreationHelper;
import org.amalthea4public.generic.tracecreation.metamodel.trace.adapter.TraceMetamodelAdapter;
import org.amalthea4public.generic.tracecreation.metamodel.trace.adapter.TracePersistenceAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;

public class DiagramTextProviderHandler implements DiagramTextProvider {

	@Override
	public String getDiagramText(IEditorPart editor, ISelection input) {		
		TracePersistenceAdapter persistenceAdapter = TraceCreationHelper.getTracePersistenceAdapter().get();
		TraceMetamodelAdapter metamodelAdapter = TraceCreationHelper.getTraceMetamodelAdapter().get();

		EcoreEditor eeditor = EcoreEditor.class.cast(editor);
		List<Object> selectedModels = TraceCreationHelper.extractSelectedElements(eeditor.getSelection());
		List<EObject> firstModelElements = null;
		List<EObject> secondModelElements = null;

		ResourceSet resourceSet = new ResourceSetImpl();
		if(selectedModels.size() > 0 && selectedModels.get(0) instanceof EObject)
			resourceSet = ((EObject) selectedModels.get(0)).eResource().getResourceSet();
		
		Optional<EObject> traceModel = persistenceAdapter.getTraceModel(resourceSet);
		
		if(selectedModels.size() == 1 && selectedModels.get(0) instanceof EObject){
			
			EObject selectedEObject = (EObject) selectedModels.get(0);
			
			Map<EObject, List<EObject>> traces = metamodelAdapter.getConnectedElements(selectedEObject, traceModel);
			
			List<String> traceLabels = new ArrayList<>();
			List<EObject> connectedElements = new ArrayList<>();
			traces.keySet().forEach(k -> {
				traces.get(k).forEach(e -> {
					traceLabels.add(TraceCreationHelper.getIdentifier(k));
					connectedElements.add(e);
				});
			});
			
			return VisualizationHelper.createNeighboursView(connectedElements, traceLabels, selectedEObject);
		}
		else if (selectedModels.size() == 2) {
			firstModelElements = TraceCreationHelper.linearize(selectedModels.get(0));
			secondModelElements = TraceCreationHelper.linearize(selectedModels.get(1));
		}else {
			firstModelElements = selectedModels.stream()
					      					   .flatMap(r -> TraceCreationHelper.linearize(r).stream())
					      					   .collect(Collectors.toList());
			
			secondModelElements = firstModelElements;
		}
		
		String umlString = VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements);
		
		return umlString;
	}
	
	@Override
	public boolean supportsEditor(IEditorPart editor) {
		return editor instanceof EcoreEditor;
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		return true;
	}
}
