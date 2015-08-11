# Outbox Questions #

What is the fundamental role of OutboxExecutionHandler? As it stands, implementing the scheduling of calls with the OutboxExecutionHandler would require writing and emitting your own scheduled events that are then handled. Should there be an easier way to schedule, or is this old functionality that is no longer delegated to the outbox module?



---


When pushing messages to the top of the priority list, creation time is the deciding factor. Priority only plays a role in the sorting if their creation dates are equal. Is this the intent of message priority? In fact, since Date creation is accurate to the millisecond, it seems very rare that priority would ever be considered.



---


Does the VxmlController need to be documented at all? I have noticed other modules, TAMA for instance, construct Vxml URL requests that the controller handles. Are there instances where an end user would be composing Vxml URLs on their own?