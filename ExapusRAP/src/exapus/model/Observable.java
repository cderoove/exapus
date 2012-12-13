package exapus.model;

import java.util.Set;

public class Observable {

	protected Set<IDeltaListener> listeners;

	public Observable() {
		super();
	}

	public void addListener(IDeltaListener l) {
		listeners.add(l);
	}

	public boolean removeListener(IDeltaListener l) {
		return listeners.remove(l);
	}

	public void fireUpdate(Object added) {
		DeltaEvent event = new DeltaEvent(added);
		for (IDeltaListener l : listeners)
			l.add(event);
	}

	public void fireRemove(Object removed) {
		DeltaEvent event = new DeltaEvent(removed);
		for (IDeltaListener l : listeners)
			l.remove(event);
	}

}