package com.ureca.fitlog.todos.mapper;

import com.ureca.fitlog.todos.dto.TodoCreateRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;

@Mapper
public interface TodoMapper {
    /** [CREATE] 새로운 투두리스트(운동 세트) 생성 */
    void insertTodo(TodoCreateRequestDTO dto);

    /** [READ]
     * 개별 세트의 완료 여부 조회
     * 세트의 완료 상태 토글
     * */
    Boolean getIsCompletedById(Long todoId);
    int updateTodoCompletion(@Param("todoId") Long todoId, @Param("isCompleted") Boolean isCompleted);


    /** [UPDATE]
     * 투두 전체 수정
     * 세트당 횟수만 수정
     * */
//    int updateTodo(TodoCreateRequestDTO dto);
    void updateTodoRepsOnly(Map<String, Object> params);

    /** [READ] 특정 날짜와 운동 종목에 대한 투두리스트(세트) 개수 조회 */
    int countSetsByDateAndExercise(@Param("date") LocalDate date,
                                   @Param("exerciseId") Long exerciseId,
                                   @Param("userId") Long userId);

    /** 운동 완료 상태 토글 */
    void updateTodosDoneStatus(@Param("date") LocalDate date, @Param("isDone") boolean isDone);
    int existsTodosDoneTrueByDate(@Param("date") LocalDate date);

    /** [DELETE]
     * todo_id 기준으로 해당 행 삭제
     * 삭제된 todo의 date, exercise_id 조회
     * 특정 날짜 + 운동종목(exercise_id)의 sets_number 임시 음수화
     * 특정 날짜 + 운동종목(exercise_id)의 sets_number 재정렬
     * */
    int deleteTodoById(@Param("todoId") Long todoId);
    Map<String, Object> findDateAndExerciseIdByTodoId(@Param("todoId") Long todoId);
    void tempNegateSetsNumbers(@Param("date") LocalDate date, @Param("exerciseId") Long exerciseId);
    void reorderSetsNumbers(@Param("date") LocalDate date, @Param("exerciseId") Long exerciseId);

    /** 세트별 휴식시간 기록 */
    int updateRestTime(@Param("todoId") Long todoId,
                       @Param("userId") Long userId,
                       @Param("restTime") Integer restTime);

    /** 기록된 휴식시간 초기화 */
    int resetRestTime(@Param("todoId") Long todoId, @Param("userId") Long userId);

    // 참고용
    int countTodosByDate(@Param("date") LocalDate date);
    int countTodosByDateAndNotCompleted(@Param("date") LocalDate date);
}
