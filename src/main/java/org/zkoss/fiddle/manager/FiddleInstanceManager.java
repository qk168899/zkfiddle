package org.zkoss.fiddle.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.fiddle.model.FiddleInstance;

/**
 * we store this data in system wild and not persisting.
 * 
 * @author tony
 * 
 */
public class FiddleInstanceManager {

	private Map<String, FiddleInstance> instancesByName = new HashMap<String, FiddleInstance>();

	private Map<String, List<FiddleInstance>> instancesByVersion = new HashMap<String, List<FiddleInstance>>();

	private static FiddleInstanceManager _instance;

	private String latest = "5.0.7";
	
	private FiddleInstanceManager() {

	}

	/**
	 * @throws IllegalArgumentException
	 *             instance and instance path can't be null
	 * @param instance
	 */

	public FiddleInstance getFiddleInstance(String name) {
		return checkDate(instancesByName.get(name));

	}

	public FiddleInstance getFiddleInstanceForLastestVersion() {
		return getFiddleInstanceByVersion(latest);
	}
	
	public FiddleInstance getFiddleInstanceByVersion(String version) {

		for (FiddleInstance fi : getVersionList(version)) {
			FiddleInstance inst = checkDate(fi);
			if (inst != null)
				return inst;
		}
		return null;

	}

	private long checkTime = 1000 * 60 * 5;

	private List<FiddleInstance> getVersionList(String ver) {
		if (instancesByVersion.containsKey(ver)) {
			return instancesByVersion.get(ver);
		} else {
			List<FiddleInstance> list = new ArrayList<FiddleInstance>();
			instancesByVersion.put(ver, list);
			return list;
		}
	}

	private FiddleInstance checkDate(FiddleInstance os) {
		if (os == null)
			return os;

		Date d = new Date();
		long diff = os.getLastUpdate().getTime() - d.getTime();
		if (diff > checkTime) {
			removeInstacne(os.getName());
			return null;
		}
		return os;
	}

	private void removeInstacne(String name){
		
		FiddleInstance ins = instancesByName.get(name);
		instancesByName.remove(name);
		
		getVersionList(ins.getZKVersion()).remove(ins);
		
	}

	public void addFiddleInstance(FiddleInstance instance) {
		if (instance == null || instance.getPath() == null) {
			throw new IllegalArgumentException("instance and instance path can't be null ");
		}

		if (!instance.getName().matches("[0-9a-zA-Z_$]+")) {
			throw new IllegalArgumentException("instance name should match ([0-9a-zA-Z_$]+) ");
		}

		instancesByName.put(instance.getName(), instance);

		getVersionList(instance.getZKVersion()).add(instance);

		Date d = new Date();
		long checkTime = 1000 * 60 * 5;

		for (String name : instancesByName.keySet()) {

			long diff = instancesByName.get(name).getLastUpdate().getTime() - d.getTime();
			if (diff > checkTime) {
				removeInstacne(name);
			}
		}

	}

	/**
	 * @return
	 */
	public Map<String, FiddleInstance> listFiddleInstances() {
		return Collections.unmodifiableMap(instancesByName);
	}

	public static FiddleInstanceManager getInstance() {
		if (_instance == null)
			_instance = new FiddleInstanceManager();

		return _instance;
	}

}