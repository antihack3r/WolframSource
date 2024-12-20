/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import net.wolframclient.event.Event;
import net.wolframclient.event.EventRegistryEntry;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;

public final class EventRegistry {
    private static final EventRegistry instance = new EventRegistry();
    private final HashMap<Class, ArrayList<EventRegistryEntry>> map = new HashMap();

    public static EventRegistry getInstance() {
        return instance;
    }

    public void registerListener(Listener listener) {
        Method[] methodArray = listener.getClass().getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            Method method = methodArray[n2];
            if (method.isAnnotationPresent(EventTarget.class) && method.getParameterTypes().length == 1) {
                this.register(method, listener);
            }
            ++n2;
        }
    }

    private void register(Method method, Listener listener) {
        Class<?> eventType = method.getParameterTypes()[0];
        byte priority = method.getAnnotation(EventTarget.class).priority();
        EventRegistryEntry entry = new EventRegistryEntry(listener, method, priority);
        if (!this.map.containsKey(eventType)) {
            this.map.put(eventType, new ArrayList<EventRegistryEntry>(Arrays.asList(entry)));
            return;
        }
        ArrayList<EventRegistryEntry> entries = this.map.get(eventType);
        entries.add(entry);
        entries.sort(Comparator.comparing(EventRegistryEntry::getPriority));
    }

    public void unregisterListener(Listener listener) {
        Iterator<ArrayList<EventRegistryEntry>> itr = this.map.values().iterator();
        while (itr.hasNext()) {
            ArrayList<EventRegistryEntry> entries = itr.next();
            entries.removeIf(e -> e.getListener().equals(listener));
            if (!entries.isEmpty()) continue;
            itr.remove();
        }
    }

    public void fire(Event event) {
        ArrayList<EventRegistryEntry> entries = this.map.get(event.getClass());
        if (entries == null) {
            return;
        }
        for (EventRegistryEntry entry : entries) {
            try {
                entry.getMethod().invoke((Object)entry.getListener(), event);
            }
            catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
            }
            catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}

