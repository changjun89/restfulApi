package me.changjun.demorestapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.changjun.demorestapi.accounts.Account;

import javax.persistence.*;
import java.time.LocalDateTime;
import me.changjun.demorestapi.accounts.AccountSerializer;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(value = EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    public void init() {
        initPrice();
        initOffline();
    }

    private void initOffline() {
        if (this.location != null && !this.location.trim().isEmpty()) {
            this.offline = true;
            return;
        }
        this.offline = false;
    }

    private void initPrice() {
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
            return;
        }
        this.free = false;
    }
}
