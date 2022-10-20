package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.user.User;
import com.zancheema.classroom.user.UserRepository;

import java.util.Optional;

public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final UserRepository userRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository, ClassroomMapper classroomMapper, UserRepository userRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomMapper = classroomMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ClassroomInfo> findClassroomById(long classroomId) {
        return classroomRepository.findById(classroomId)
                .map(classroomMapper::toClassroomBody);
    }

    @Override
    public Optional<Teacher> findTeacher(long classroomId) {
        return this.findClassroomById(classroomId)
                .map(ClassroomInfo::teacher);
    }

    @Override
    public Optional<ClassroomStudents> findClassroomStudents(long classroomId) {
        return classroomRepository.findById(classroomId)
                .map(classroomMapper::getClassroomStudents);
    }

    @Override
    public Optional<ClassroomInfo> createClassroom(ClassroomCreationPayload payload) {
        Optional<User> teacher = userRepository.findById(payload.getTeacherId());
        if (teacher.isEmpty()) return Optional.empty();

        Classroom classroom = classroomMapper.toClassroom(payload.getTitle(), payload.getSubject());
        classroom.setTeacher(teacher.get());

        Classroom savedClassroom = classroomRepository.save(classroom);

        ClassroomInfo classroomInfo = classroomMapper.toClassroomBody(savedClassroom);

        return Optional.of(classroomInfo);
    }

    @Override
    public Optional<ClassroomStudent> addStudentToClassroom(ClassroomStudent payload) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(payload.getClassId());
        Optional<User> optionalStudent = userRepository.findById(payload.getStudentId());

        if (optionalClassroom.isEmpty() || optionalStudent.isEmpty()) {
            return Optional.empty();
        }

        Classroom classroom = optionalClassroom.get();
        classroom.getStudents().add(optionalStudent.get());
        classroomRepository.save(classroom);

        return Optional.of(payload);
    }

    @Override
    public Optional<ClassroomInfo> updateClassroom(long classroomId, UpdateClassroomPayload payload) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        Optional<User> optionalTeacher = userRepository.findById(payload.getTeacherId());

        if (optionalClassroom.isEmpty() || optionalTeacher.isEmpty()) {
            return Optional.empty();
        }

        Classroom classroom = optionalClassroom.get();
        classroom.setTeacher(optionalTeacher.get());
        classroom.setTitle(payload.getTitle());
        classroom.setSubject(payload.getSubject());

        Classroom updatedClassroom = classroomRepository.save(classroom);

        ClassroomInfo classroomInfo = classroomMapper.toClassroomBody(updatedClassroom);
        return Optional.of(classroomInfo);
    }

    @Override
    public Optional<AttendingClassrooms> findAttendingClassrooms(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<ClassroomQuizzesInfo> findClassroomQuizInfos(long classroomId) {
        return Optional.empty();
    }

    @Override
    public Optional<ClassroomQuiz> findClassroomQuiz(long classroomId, long quizId) {
        return Optional.empty();
    }
}