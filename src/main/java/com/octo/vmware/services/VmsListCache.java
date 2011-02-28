package com.octo.vmware.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.octo.vmware.entities.VmInfo;

public class VmsListCache {
	
	private static HashMap<String, List<String>> cache = null;
	
	@SuppressWarnings("unchecked")
	public static Map<String, List<String>> get() {
		if (cache == null) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(".cache"));
				cache = (HashMap<String, List<String>>) ois.readObject();
				ois.close();
			} catch (Exception e) {
			}
		}
		if (cache == null) {
			cache = new HashMap<String, List<String>>();
		}
		return cache;
	}
	
	public static void set(String esxName, List<VmInfo> list) {
		get();
		List<String> l = new ArrayList<String>();
		for(VmInfo vm : list) {
			l.add(vm.getName());
		}
		cache.put(esxName, l);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(".cache"));
			oos.writeObject(cache);
			oos.close();
		} catch (Exception e) {
		}
	}

}
