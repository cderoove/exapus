package exapus.model;

import java.util.HashSet;
import java.util.Set;

public class Observable {

	protected Set<IDeltaListener> listeners;

	public Observable() {
		listeners = new HashSet<IDeltaListener>();
	}

	public void addListener(IDeltaListener l) {
		listeners.add(l);
	}

	public boolean removeListener(IDeltaListener l) {
		return listeners.remove(l);
	}

	public void fire(DeltaEvent event) {
		for (IDeltaListener l : listeners)
			l.delta(event);
	}

	public void fireUpdate(Object added) {
		fire(new AddDeltaEvent(added));
	}

	public void fireRemove(Object removed) {
		fire(new RemoveDeltaEvent(removed));
	}

}