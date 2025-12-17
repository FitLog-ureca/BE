package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import com.ureca.fitlog.todos.dto.request.TodoCreateRequestDTO;
import com.ureca.fitlog.todos.dto.request.UpdateTodoRecordRequestDTO;
import com.ureca.fitlog.todos.dto.response.TodoCompleteResponseDTO;
import com.ureca.fitlog.todos.dto.response.TodoCreateResponseDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;
    private final com.ureca.fitlog.auth.mapper.AuthMapper authMapper;

    /**
     * 현재 로그인한 사용자의 userId를 가져오는 공통 메서드
     */
    private Long getCurrentUserId() {
        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (loginId == null) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        var user = authMapper.findByLoginId(loginId);
        if (user == null) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_USER_NOT_FOUND);
        }

        return user.getUserId();
    }

    /** Todo 생성 (세트번호 자동 증가) */
    @Transactional
    public TodoCreateResponseDTO createTodo(TodoCreateRequestDTO dto) {
        /** 기존 todos/create API 로직 */
//        Long userId = getCurrentUserId();
//        dto.setUserId(userId);
//
//        int currentCount = todoMapper.countSetsByDateAndExercise(dto.getDate(), dto.getExerciseId(), userId);
//        int nextSetNumber = currentCount + 1;
//        dto.setSetsNumber(nextSetNumber);
//
//        todoMapper.insertTodo(dto);
//
//        return TodoCreateResponseDTO.builder()
//                .todoId(dto.getTodoId())
//                .exerciseId(dto.getExerciseId())
//                .setsNumber(dto.getSetsNumber())
//                .repsTarget(dto.getRepsTarget())
//                .weight(dto.getWeight())
//                .date(dto.getDate())
//                .isCompleted(false)
//                .message("투두가 성공적으로 생성되었습니다.")
//                .build();

        /** todos/ 서비스 로직 */
        Long userId = getCurrentUserId();
        dto.setUserId(userId);

        // 🔥 핵심: 운동 항목 생성 시 항상 Set 1
        dto.setSetsNumber(1);

        todoMapper.insertTodo(dto);

        return TodoCreateResponseDTO.builder()
                .todoId(dto.getTodoId())
                .exerciseId(dto.getExerciseId())
                .setsNumber(1)
                .repsTarget(dto.getRepsTarget())
                .weight(dto.getWeight())
                .date(dto.getDate())
                .isCompleted(false)
                .message("운동 항목이 생성되었습니다.")
                .build();
    }

    @Transactional
    public TodoCreateResponseDTO createWorkout(TodoCreateRequestDTO req) {
        Long userId = getCurrentUserId();

        // 1️⃣ Set 1 생성 (workout_id 임시 값)
        TodoCreateRequestDTO dto = TodoCreateRequestDTO.builder()
                .userId(userId)
                .exerciseId(req.getExerciseId())
                .date(req.getDate())
                .setsNumber(1)
                .workoutId(0L) // 임시
                .build();

        todoMapper.insertTodo(dto); // todoId 생성

        // 2️⃣ workout_id = 자기 todoId
        todoMapper.updateWorkoutId(dto.getTodoId(), dto.getTodoId(), userId);

        return TodoCreateResponseDTO.builder()
                .todoId(dto.getTodoId())
                .workoutId(dto.getTodoId())
                .setsNumber(1)
                .date(req.getDate())
                .message("운동 항목이 생성되었습니다.")
                .build();
    }

    /** todos/{todoId}/sets 서비스 로직 */
    @Transactional
    public TodoCreateResponseDTO addSet(Long todoId) {
        Long userId = getCurrentUserId();

        // 1️⃣ workout_id 조회 (todoId는 Set1)
        Long workoutId =
                todoMapper.findWorkoutIdByTodoId(todoId, userId);

        if (workoutId == null) {
            throw new BusinessException(
                    ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION
            );
        }

        // 2️⃣ 다음 세트 번호
        int nextSetNumber =
                todoMapper.findMaxSetsNumberByWorkoutId(workoutId, userId) + 1;

        // 3️⃣ 기준 정보 조회
        Map<String, Object> info =
                todoMapper.findDateAndExerciseIdByTodoId(todoId, userId);

        LocalDate date =
                ((java.sql.Date) info.get("date")).toLocalDate();
        Long exerciseId =
                ((Number) info.get("exercise_id")).longValue();

        // 4️⃣ insert
        TodoCreateRequestDTO dto = TodoCreateRequestDTO.builder()
                .userId(userId)
                .workoutId(workoutId)
                .exerciseId(exerciseId)
                .date(date)
                .setsNumber(nextSetNumber)
                .build();

        todoMapper.insertTodo(dto);

        return TodoCreateResponseDTO.builder()
                .todoId(dto.getTodoId())
                .workoutId(workoutId)
                .setsNumber(nextSetNumber)
                .date(date)
                .isCompleted(false)
                .message("세트가 추가되었습니다.")
                .build();
    }

    /** 개별 세트 완료 토글 (todoId만으로 true/false 자동 반전) */
    public TodoCompleteResponseDTO updateTodoCompletion(Long todoId) {
        Long userId = getCurrentUserId();

        Boolean currentStatus = todoMapper.getIsCompletedById(todoId, userId);
        if (currentStatus == null) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }

        Boolean newStatus = !currentStatus;
        int updated = todoMapper.updateTodoCompletion(todoId, userId, newStatus);

        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }

        return TodoCompleteResponseDTO.builder()
                .todoId(todoId)
                .isCompleted(newStatus)
                .message(newStatus ? "세트가 완료 처리되었습니다." : "세트 완료가 해제되었습니다.")
                .build();
    }

    /** 현재 is_done 상태를 반전시켜 저장 */
    @Transactional
    public boolean toggleTodosDoneStatus(LocalDate date) {
        Long userId = getCurrentUserId();

        // 현재 상태 조회 (해당 사용자의 투두만)
        boolean currentStatus = todoMapper.existsTodosDoneTrueByDate(date, userId) > 0;

        // 반전된 상태로 업데이트
        boolean newStatus = !currentStatus;
        todoMapper.updateTodosDoneStatus(date, userId, newStatus);

        return newStatus;
    }

    /** 세트당 수행횟수(reps_target)만 수정 */
    @Transactional
    public void updateTodoRepsOnly(Long todoId, int repsTarget) {
        Long userId = getCurrentUserId();

        // 유효성 검증
        if (repsTarget <= 0) {
            throw new BusinessException(ExceptionStatus.TODO_VALIDATION_REPS_TARGET_INVALID);
        }

        int updated = todoMapper.updateTodoRepsOnly(todoId, userId, repsTarget);
        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }
    }

    /** 세트 기록 수정 */
    @Transactional
    public void updateTodoRecord(Long todoId, UpdateTodoRecordRequestDTO request) {
        Long userId = getCurrentUserId();

        if (request.getRepsTarget() != null && request.getRepsTarget() <= 0) {
            throw new BusinessException(ExceptionStatus.TODO_VALIDATION_REPS_TARGET_INVALID);
        }

        if (request.getWeight() != null && request.getWeight() < 0) {
            throw new BusinessException(ExceptionStatus.TODO_VALIDATION_WEIGHT_INVALID);
        }

        int updated = todoMapper.updateTodoRecord(
                todoId,
                userId,
                request.getRepsTarget(),
                request.getWeight()
        );

        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }
    }

    /** 투두 삭제 */
    public Map<String, Object> deleteTodoById(Long todoId) {
        Long userId = getCurrentUserId();
        int deleted = todoMapper.deleteTodoById(todoId, userId);
        Map<String, Object> response = new HashMap<>();
        if (deleted > 0) {
            response.put("todoId", todoId);
            response.put("message", "TodoList가 성공적으로 삭제되었습니다.");
        } else {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }
        return response;
    }

    /** 투두 삭제 후 sets_number 재정렬 */
    @Transactional
    public void deleteTodoAndReorder(Long todoId) {
        Long userId = getCurrentUserId();

        // 삭제 대상의 date, exercise_id 조회 (본인 소유 확인)
        Map<String, Object> info = todoMapper.findDateAndExerciseIdByTodoId(todoId, userId);
        if (info == null || info.isEmpty()) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }

        Object dateObj = info.get("date");
        LocalDate date = (dateObj instanceof LocalDate)
                ? (LocalDate) dateObj
                : ((Date) dateObj).toLocalDate();

        Long exerciseId = ((Number) info.get("exercise_id")).longValue();

        // 삭제
        todoMapper.deleteTodoById(todoId, userId);
        // 임시 음수화 (UNIQUE 제약 피하기) - 해당 사용자의 투두만
        todoMapper.tempNegateSetsNumbers(date, exerciseId, userId);
        // 세트번호 재정렬 - 해당 사용자의 투두만
        todoMapper.reorderSetsNumbers(date, exerciseId, userId);
    }

    /** 휴식 시간 기록 */
    public Map<String, Object> updateRestTime(Long todoId, Integer restTime) {
        Long userId = getCurrentUserId();

        // 유효성 검증
        if (restTime == null || restTime < 0 || restTime > 7200) {
            throw new BusinessException(ExceptionStatus.TODO_VALIDATION_REST_TIME_INVALID);
        }

        // 해당 todo가 본인 소유인지 검증하면서 업데이트
        int updated = todoMapper.updateRestTime(todoId, userId, restTime);

        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 성공 응답
        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("restTime", restTime);
        response.put("message", "휴식시간이 업데이트되었습니다.");

        return response;
    }

    /** 휴식시간 초기화 */
    public void resetRestTime(Long todoId) {
        Long userId = getCurrentUserId();

        // 해당 todo가 본인 소유인지 확인하며 초기화
        int updated = todoMapper.resetRestTime(todoId, userId);
        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION);
        }
    }
}