package asia.ptyin.sqloj.course.service;

import asia.ptyin.sqloj.course.CourseDto;
import asia.ptyin.sqloj.course.CourseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface CourseService
{
    List<CourseEntity> getCurrentUserCourseList(Authentication authentication);
    CourseEntity getCourseByUuid(UUID uuid);

    boolean openCourse(CourseDto courseDto);
}