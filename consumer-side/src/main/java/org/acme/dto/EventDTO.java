package org.acme.dto;


public class EventDTO {

    private String eventId;
    private String description;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId='" + eventId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
