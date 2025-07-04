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

            Long todoId = (Long) joinPoint.getArgs()[1];
            ManagerSaveRequest request = (ManagerSaveRequest) joinPoint.getArgs()[2];

            String message = String.format("매니저 등록 시도 Todo ID: %d, 담당자 ID: %d",
                    todoId, request.getManagerUserId());

            activityLogService.saveLog(ActivityType.MANAGER_REGISTRATION_ATTEMPT, message, todoId, "ATTEMPT");
            log.info("AOP Before {}", message);
        } catch (Exception e) {
            log.error("매니저 저장 시도 로깅 실패: {}", e.getMessage(), e);
        }
    }

    // 매니저 등록 메서드 성공 후 로그 기록
    @AfterReturning(pointcut = "managerSavePointcut()")
    public void logManagerSaveSuccess(JoinPoint joinPoint) {
        try {
            Long todoId = (Long) joinPoint.getArgs()[1];

            String message = String.format("매니저 등록 성공  Todo ID: %d", todoId);

            activityLogService.saveLog(ActivityType.MANAGER_REGISTRATION_SUCCESS, message, todoId, "SUCCESS");
            log.info("AOP AfterReturning {}", message);
        } catch (Exception e) {
            log.error("Failed to log manager save success: {}", e.getMessage(), e);
        }
    }
    //매니저 등록 메서드 실패시 로그출력
    @AfterThrowing(pointcut = "managerSavePointcut()",throwing = "ex")
    public void logManagerSaveFail(JoinPoint joinPoint, Throwable ex) {
        try {
            Long todoId = (Long) joinPoint.getArgs()[1];
            ManagerSaveRequest request = (ManagerSaveRequest) joinPoint.getArgs()[2];

            String message = String.format("매니저등록실패 - TodoId : %d 담당자 id:, 에러: %s",
            todoId, request,ex.getMessage());

            activityLogService.saveLog(ActivityType.MANAGER_REGISTRATION_FAIL, message, todoId, "Failed");
            log.info("AOP AfterThrowing {}", message);

        } catch (Exception e) {
            log.error("AOP 단계에서 매니저 등록 실패 로그를 남기는데 실패 :{}",e.getMessage());
        }
    }


}
