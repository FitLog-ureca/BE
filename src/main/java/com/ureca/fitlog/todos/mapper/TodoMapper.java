package com.ureca.fitlog.todos.mapper;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface TodoMapper {

    void insertTodo(TodoRequestDTO dto);
    Boolean getIsCompletedById(Long todoId);
    int updateTodoCompletion(@Param("todoId") Long todoId, @Param("isCompleted") Boolean isCompleted);
    int updateTodo(TodoRequestDTO dto);
    void updateTodoRepsOnly(Map<String, Object> params);

    int countSetsByDateAndExercise(@Param("date") LocalDate date,
                                   @Param("exerciseId") Long exerciseId);

    /** is_done 관련 추가 */
    void updateTodosDoneStatus(@Param("date") LocalDate date, @Param("isDone") boolean isDone);
    int existsTodosDoneTrueByDate(@Param("date") LocalDate date);

    /** todo_id 기준으로 해당 행 삭제 */
    int deleteTodoById(@Param("todoId") Long todoId);

    /** 삭제된 todo의 date, exercise_id 조회 */
    Map<String, Object> findDateAndExerciseIdByTodoId(@Param("todoId") Long todoId);

    /** 특정 날짜 + 운동종목(exercise_id)의 sets_number 임시 음수화 */
    void tempNegateSetsNumbers(@Param("date") LocalDate date, @Param("exerciseId") Long exerciseId);

    /** 특정 날짜 + 운동종목(exercise_id)의 sets_number 재정렬 */
    void reorderSetsNumbers(@Param("date") LocalDate date, @Param("exerciseId") Long exerciseId);

    /** 세트별 휴식시간 기록 메서드 추가 */
    int updateRestTime(@Param("todoId") Long todoId,
                       @Param("userId") Long userId,
                       @Param("restTime") Integer restTime);

    int resetRestTime(@Param("todoId") Long todoId, @Param("userId") Long userId);

    // 참고용
    int countTodosByDate(@Param("date") LocalDate date);
    int countTodosByDateAndNotCompleted(@Param("date") LocalDate date);
}
