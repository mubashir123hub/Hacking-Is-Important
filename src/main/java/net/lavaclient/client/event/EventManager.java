package net.lavaclient.client.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manager for handling and dispatching events
 */
public class EventManager {
    /**
     * Map of event classes to registered listeners
     */
    private final Map<Class<? extends Event>, List<EventData>> registeredEvents = new HashMap<>();
    
    /**
     * Registers a listener object
     * @param listener The listener object
     */
    public void register(Object listener) {
        // Find all methods with EventTarget annotation
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventTarget.class) && method.getParameterCount() == 1) {
                // Get the event class
                Class<?> eventClass = method.getParameterTypes()[0];
                
                // Ensure it's an event
                if (Event.class.isAssignableFrom(eventClass)) {
                    // Get the event priority
                    EventTarget annotation = method.getAnnotation(EventTarget.class);
                    
                    // Make the method accessible
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    
                    // Get or create the event list
                    @SuppressWarnings("unchecked")
                    Class<? extends Event> castedEventClass = (Class<? extends Event>) eventClass;
                    
                    List<EventData> eventList = registeredEvents.computeIfAbsent(castedEventClass, k -> new CopyOnWriteArrayList<>());
                    
                    // Add the event data
                    eventList.add(new EventData(listener, method, annotation.priority()));
                    
                    // Sort by priority
                    eventList.sort(Comparator.comparingInt(EventData::getPriority));
                }
            }
        }
    }
    
    /**
     * Unregisters a listener object
     * @param listener The listener object
     */
    public void unregister(Object listener) {
        // Remove all event data for this listener
        for (List<EventData> eventList : registeredEvents.values()) {
            eventList.removeIf(eventData -> eventData.getListener() == listener);
        }
        
        // Remove empty lists
        registeredEvents.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
    
    /**
     * Posts an event
     * @param event The event
     */
    public void post(Event event) {
        // Get the event list
        List<EventData> eventList = registeredEvents.get(event.getClass());
        
        // If no listeners, return
        if (eventList == null) {
            return;
        }
        
        // Notify all listeners
        for (EventData data : eventList) {
            try {
                data.invoke(event);
            } catch (Exception e) {
                System.err.println("Error posting event: " + e.getMessage());
                e.printStackTrace();
            }
            
            // If the event is cancelled, stop processing
            if (event.isCancelled()) {
                break;
            }
        }
    }
    
    /**
     * Class to hold event data
     */
    private static class EventData {
        private final Object listener;
        private final Method method;
        private final int priority;
        
        /**
         * Constructor
         * @param listener Listener object
         * @param method Listener method
         * @param priority Event priority
         */
        public EventData(Object listener, Method method, int priority) {
            this.listener = listener;
            this.method = method;
            this.priority = priority;
        }
        
        /**
         * Gets the listener object
         * @return Listener object
         */
        public Object getListener() {
            return listener;
        }
        
        /**
         * Gets the event priority
         * @return Event priority
         */
        public int getPriority() {
            return priority;
        }
        
        /**
         * Invokes the event method
         * @param event The event
         * @throws Exception If an error occurs
         */
        public void invoke(Event event) throws Exception {
            method.invoke(listener, event);
        }
    }
}
