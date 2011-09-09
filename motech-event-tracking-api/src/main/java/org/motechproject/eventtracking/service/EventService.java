package org.motechproject.eventtracking.service;

import java.util.Observable;
import java.util.Observer;

import org.motechproject.eventtracking.domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventService extends Observable{
	public void publishEvent(Event event) {
		this.setChanged();
		this.notifyObservers(event);
	}
	
	public void subscribeEvent(Observer observer) {
		this.addObserver(observer);
	}

}
