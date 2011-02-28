package com.octo.vmware.utils;

import vim25.SelectionSpec;
import vim25.TraversalSpec;

public class TraversalSpecHelper {
	
	public static TraversalSpec makeTraversalSpec(String type, String path, String name, boolean skip, String [] selectionSpecs, TraversalSpec [] traversalSpecs) {
		TraversalSpec traversalSpec = new TraversalSpec();
		traversalSpec.setName(name);
		traversalSpec.setType(type);
		traversalSpec.setPath(path);
		traversalSpec.setSkip(skip);
		for(String s : selectionSpecs) {
			SelectionSpec selectionSpec = new SelectionSpec();
			selectionSpec.setName(s);
			traversalSpec.getSelectSet().add(selectionSpec);
		}
		for(TraversalSpec t : traversalSpecs) {
			traversalSpec.getSelectSet().add(t);
		}
		return traversalSpec;
	}
	

}
