package org.example.expert.domain.activitylog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     *  REQUIRES_NEW로 매니저 등록이 실패해도 별개의 Transaction 에서 로그를남긴다. 에러발생시 에러를삼켜 비지느시로직에 영향이 가지않게한다.
     * @param activityType
     * @param logMessage
     * @param targetId
     * @param status
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(ActivityType activityType, String logMessage, Long targetId,String status) {
        try {
            ActivityLog activityLog = new ActivityLog(activityType,logMessage,targetId,status);
            activityLogRepository.save(activityLog);
            log.info("로그 저장 성공 : {}", logMessage );
        } catch (Exception e) {
            log.error("로그저장 실패: {}", logMessage, e);
        }
    }
}
