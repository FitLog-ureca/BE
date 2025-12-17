package com.ureca.fitlog.todos.mapper;

import com.ureca.fitlog.todos.dto.request.TodoCreateRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;

@Mapper
public interface TodoMapper {
    /** [CREATE] 새로운 투두리스트(운동 세트) 생성 */
    void insertTodo(TodoCreateRequestDTO dto);

    /** [CREATE] 새로운 세트항목(기존 운동 항목 기반) 생성 */
    int findMaxSetsNumberByTodoId(@Param("todoId") Long todoId,
                                  @Param("userId") Long userId);


    /** [READ] 개별 세트의 완료 여부 조회 (userId 검증 포함) */
    Boolean getIsCompletedById(@Param("todoId") Long todoId,
                                      @Param("userId") Long userId);

    /** [UPDATE] 세트의 완료 상태 토글 (userId 검증 포함) */
    int updateTodoCompletion(@Param("todoId") Long todoId,
                             @Param("userId") Long userId,
                             @Param("isCompleted") Boolean isCompleted);
//    int updateTodo(TodoCreateRequestDTO dto);
    /** [UPDATE] 세트당 횟수만 수정 (userId 검증 포함) */
    int updateTodoRepsOnly(@Param("todoId") Long todoId,
                           @Param("userId") Long userId,
                           @Param("repsTarget") int repsTarget);

    /** 세트 기록 수정 */
    int updateTodoRecord(
            @Param("todoId") Long todoId,
            @Param("userId") Long userId,
            @Param("repsTarget") Integer repsTarget,
            @Param("weight") Double weight
    );

    /** [READ] 특정 날짜와 운동 종목에 대한 투두리스트(세트) 개수 조회 */
    int countSetsByDateAndExercise(@Param("date") LocalDate date,
                                   @Param("exerciseId") Long exerciseId,
                                   @Param("userId") Long userId);

    /** 운동 완료 상태 토글 (userId 검증 포함) */
    void updateTodosDoneStatus(@Param("date") LocalDate date,
                               @Param("userId") Long userId,
                               @Param("isDone") boolean isDone);
    int existsTodosDoneTrueByDate(@Param("date") LocalDate date,
                                         @Param("userId") Long userId);

    /** [DELETE] todo_id 기준으로 해당 행 삭제 (userId 검증 포함) */
    int deleteTodoById(@Param("todoId") Long todoId,
                       @Param("userId") Long userId);

    /** 삭제된 todo의 date, exercise_id 조회 (userId 검증 포함) */
    Map<String, Object> findDateAndExerciseIdByTodoId(@Param("todoId") Long todoId,
                                                      @Param("userId") Long userId);

    /** 특정 날짜 + 운동종목(exercise_id)의 sets_number 임시 음수화 (userId 필터링) */
    void tempNegateSetsNumbers(@Param("date") LocalDate date,
                               @Param("exerciseId") Long exerciseId,
                               @Param("userId") Long userId);

    /** 특정 날짜 + 운동종목(exercise_id)의 sets_number 재정렬 (userId 필터링) */
    void reorderSetsNumbers(@Param("date") LocalDate date,
                            @Param("exerciseId") Long exerciseId,
                            @Param("userId") Long userId);

    /** 세트별 휴식시간 기록 */
    int updateRestTime(@Param("todoId") Long todoId,
                       @Param("userId") Long userId,
                       @Param("restTime") Integer restTime);

    /** 기록된 휴식시간 초기화 */
    int resetRestTime(@Param("todoId") Long todoId,
                      @Param("userId") Long userId);

    // 참고용 (userId 필터링 추가)
    int countTodosByDate(@Param("date") LocalDate date,
                                @Param("userId") Long userId);

    int countTodosByDateAndNotCompleted(@Param("date") LocalDate date,
                                               @Param("userId") Long userId);
}