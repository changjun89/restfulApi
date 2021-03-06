package me.changjun.demorestapi.events;

import me.changjun.demorestapi.accounts.Account;
import me.changjun.demorestapi.accounts.AccountRepository;
import me.changjun.demorestapi.accounts.AccountRole;
import me.changjun.demorestapi.accounts.AccountService;
import me.changjun.demorestapi.common.AppProperties;
import me.changjun.demorestapi.common.BaseControllerTest;
import me.changjun.demorestapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AppProperties appProperties;

    @Before
    public void setup() {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API DEVELOPMENT With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 18, 12, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 12, 40))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 21, 12, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남구 신사동")
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("link to update an existing")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime")
                                        .description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime")
                                        .description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")

                        ),

                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime")
                                        .description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime")
                                        .description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline")
                                        .description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing"),
                                fieldWithPath("_links.profile.href").description("profile")
                        )
                        )
                );
    }


    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        //given
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientName(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUserName())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password")
        );

        String resultString = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private Account createAccount() {
        Set roles = new HashSet();
        roles.add(AccountRole.USER);
        roles.add(AccountRole.ADMIN);
        String userName = appProperties.getUserUserName();
        String password = appProperties.getUserPassword();
        Account account = Account.builder()
                .email(userName)
                .password(password)
                .accountRoles(roles)
                .build();

        return accountService.saveAccount(account);
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 상용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bed_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API DEVELOPMENT With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 18, 12, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 12, 40))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 21, 12, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남구 신사동")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("입력 값이 비어있는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bed_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bed_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API DEVELOPMENT With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 18, 12, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 12, 40))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 19, 12, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남구 신사동")
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].field").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.events").exists());
    }

    @Test
    @TestDescription("30개의 이벤트중에 페이지 사이즈10으로 2번재페이지를 가지온다")
    public void eventsList() throws Exception {
        IntStream.range(0, 30).forEach(this::makeEventData);

        mockMvc.perform(get("/api/events")
                .param("size", "10")
                .param("page", "2")
                .param("sort", "name,desc")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events-list"
                ));
    }

    @Test
    @TestDescription("30개의 이벤트중에 페이지 사이즈10으로 2번재페이지를 가지온다")
    public void eventsListWithAuthentication() throws Exception {
        IntStream.range(0, 30).forEach(this::makeEventData);

        mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .param("size", "10")
                .param("page", "2")
                .param("sort", "name,desc")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events-list"
                ));
    }

    private Event makeEventData(int index) {
        Event event = Event.builder().name("event " + index)
                .description("event description " + index)
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 18, 12, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 12, 40))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 21, 12, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남구 신사동")
                .free(false)
                .offline(false)
                .build();

        return eventRepository.save(event);
    }


    private Event makeEventData(int index, Account account) {
        Event event = Event.builder().name("event " + index)
                .description("event description " + index)
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 18, 12, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 12, 40))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 21, 12, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남구 신사동")
                .free(false)
                .offline(false)
                .manager(account)
                .build();

        return eventRepository.save(event);
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        Account account = this.createAccount();
        Event event = this.makeEventData(100, account);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("get-an-event"
                ));
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //given
        Account account = this.createAccount();
        Event event = makeEventData(200, account);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        String name = "update Event";
        eventDto.setName(name);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 이벤트를 수정 실패")
    public void updateEventWithEmptyInput() throws Exception {
        Event event = makeEventData(200);
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못되어있는경우 이벤트를 수정 실패")
    public void updateEventWithWrongInput() throws Exception {
        Event event = makeEventData(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(200000);
        eventDto.setMaxPrice(100);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않는 이벤트를 수정 실패")
    public void updateEventWith404() throws Exception {
        Event event = makeEventData(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/123123")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("없는 이벤트 조회하기")
    public void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/404"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}

