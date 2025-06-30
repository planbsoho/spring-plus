package org.example.expert.domain.activitylog;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@NoArgsConstructor
@Entity
@Table(name = "log")
public class ActivityLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType actionType;

    @Column(nullable = false)
    private String logMessage;

    @Column
    private Long targetId;

    @Column
    private String status;

    public ActivityLog(ActivityType actionType, String logMessage, Long targetId, String status) {
        this.actionType = actionType;
        this.logMessage = logMessage;
        this.targetId = targetId;
        this.status = status;
    }

}
