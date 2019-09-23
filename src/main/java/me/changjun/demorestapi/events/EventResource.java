package me.changjun.demorestapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class EventResource extends Resource<Event> {

  public EventResource(Event event, Link... links) {
    super(event, links);
    add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
  }
}
