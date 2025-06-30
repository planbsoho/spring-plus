package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.example.expert.domain.activitylog.ActivityLogService;
import org.example.expert.domain.activitylog.ActivityType;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Aspect
public class ManagerAccessLoggingAspect {

    private final ActivityLogService activityLogService;


    @Pointcut("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    private void managerSavePointcut() {}

    // 매니저 등록 메서드 실행 전 로그 기록
    @Before("managerSavePointcut()")
    public void logManagerSaveAttempt(JoinPoint joinPoint) {
        try {
            // 메서드 인자에서 필요한 정보 추출
            // saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest)
            Long todoId = (Long) joinPoint.getArgs()[1]; // save 메서드의 두 번째 인자
            ManagerSaveRequest request = (ManagerSaveRequest) joinPoint.getArgs()[2]; // save 메서드의 세 번째 인자

            String logMessage = String.format("매니저 등록 시도 Todo ID: %d, 담당자 ID: %d",
                    todoId, request.getManagerUserId());

            activityLogService.saveLog(ActivityType.MANAGER_REGISTRATION_ATTEMPT, logMessage, todoId, "ATTEMPT");
            log.info("AOP Before {}", logMessage);
        } catch (Exception e) {
            log.error("매니저 저장 시도 로깅 실패: {}", e.getMessage(), e);
        }
    }

    // 매니저 등록 메서드 성공 후 로그 기록
    @AfterReturning(pointcut = "managerSavePointcut()")
    public void logManagerSaveSuccess(JoinPoint joinPoint) {
        try {
            Long todoId = (Long) joinPoint.getArgs()[1];


            String logMessage = String.format("매니저 등록 성공  Todo ID: %d", todoId);

            activityLogService.saveLog(ActivityType.MANAGER_REGISTRATION_SUCCESS, logMessage, todoId, "SUCCESS");
            log.info("AOP AfterReturning {}", logMessage);
        } catch (Exception e) {
            log.error("Failed to log manager save success: {}", e.getMessage(), e);
        }
    }


}
