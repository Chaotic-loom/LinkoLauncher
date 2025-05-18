# Events
___

An event should be constructed like this:

```java
/**
    NEW_EVENT EVENT
    <br/><br/>
 
    Details:
    <br/>
 
    Since:
    <br/>
    Author:
 */

public static final Event<NewEvent> NEW_EVENT = EventFactory.createArray(NewEvent.class, callbacks -> (param1, param2) -> {
    for (NewEvent callback : callbacks) {
        callback.onEvent(param1, param2);
    }
});

@FunctionalInterface
public interface NewEvent {
    void onEvent(Class param1, AnotherClass param2);
}
```
