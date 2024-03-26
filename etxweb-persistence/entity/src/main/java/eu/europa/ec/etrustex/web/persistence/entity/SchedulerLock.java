package eu.europa.ec.etrustex.web.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "EW_SCHEDULER_LOCK")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerLock {

    @Id
    private String name;

    @Column(name = "LOCKED_BY")
    private String lockedBy;

    @Column(name = "LOCKED_AT")
    private Date lockedAt;

    @Column(name = "LOCK_UNTIL")
    private Date lockUntil;



}
