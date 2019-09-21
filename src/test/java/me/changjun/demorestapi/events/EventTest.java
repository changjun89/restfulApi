package me.changjun.demorestapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

  @Test
  public void builder() {
    Event event = Event.builder()
        .name("Iflearn Spring Rest Api")
        .description("REST API development with Spring")
        .build();
    assertThat(event).isNotNull();
  }

  @Test
  public void javaBean() {
    //given
    String name = "Event";
    String description = "Spring";

    //when
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);

    //then
    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getDescription()).isEqualTo(description);

  }

  @Test
  /*@Parameters({
      "0, 0, true",
      "100, 0, false",
      "0, 100, false"
  })*/
  //@Parameters(method = "paramsForTestFree")
  @Parameters
  public void testFree(int basePrice, int maxPrice, boolean isFree) {
    //given
    Event event = Event.builder()
        .basePrice(basePrice)
        .maxPrice(maxPrice)
        .build();

    //when
    event.init();

    //then
    assertThat(event.isFree()).isEqualTo(isFree);
  }

  private Object[] parametersForTestFree() {
    return new Object[]{
        new Object[]{0, 0, true},
        new Object[]{100, 0, false},
        new Object[]{0, 100, false}
    };
  }

  @Test
  @Parameters
  public void testOffline(String location, boolean isOffLine) {
    //given
    Event event = Event.builder()
        .location(location)
        .build();

    //when
    event.init();

    //then
    assertThat(event.isOffline()).isEqualTo(isOffLine);

  }

  private Object[] parametersForTestOffline() {
    return new Object[]{
        new Object[]{"강남구 신사동", true},
        new Object[]{null, false},
        new Object[]{"   ", false}
    };
  }
}
