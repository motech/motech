package org.motechproject.server;

import java.util.Arrays;

import org.motechproject.event.EventType;
import org.motechproject.event.EventTypeRegistry;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author yyonkov
 * 
 */
public class Activator implements BundleActivator {
	private static Logger logger = LoggerFactory.getLogger(Activator.class);
	private ServiceTracker tracker;
	private EventListener listener;

	@Override
	public void start(BundleContext context) throws Exception {
//		this.tracker = new ServiceTracker(context, HttpService.class.getName(),
//				null) {
//
//			@Override
//			public Object addingService(ServiceReference reference) {
//				Object service = super.addingService(reference);
//				// TODO add service
//				return service;
//			}
//
//			@Override
//			public void removedService(ServiceReference reference,
//					Object service) {
//				// TODO remove service
//				super.removedService(reference, service);
//			}
//
//		};
//		this.tracker.open();
		listener = new ScheduleAppointmentReminderHandler();
		EventType eventtype = new ScheduleAppointmentReminderEventType();
		EventTypeRegistry.getInstance().add(eventtype);
		EventListenerRegistry.getInstance().registerListener(listener, Arrays.asList(eventtype) );
	}

	@Override
	public void stop(BundleContext context) throws Exception {
//		this.tracker.close();
//		EventListenerRegistry.getInstance().unregister...
	}

}
