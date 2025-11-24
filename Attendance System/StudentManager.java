import java.util.*;
import java.util.stream.Collectors;

public class StudentManager {
    private final FileHandler fh;

    public StudentManager(FileHandler fh) {
        this.fh = fh;
    }

    public boolean addStudent(Student s) {
        Map<String, Student> existing = getAllStudentsMap();
        if (existing.containsKey(s.getId())) return false;
        fh.addStudentCsv(s.toCsv());
        return true;
    }

    public List<Student> getAllStudents() {
        return fh.readStudentsCsv().stream()
                .filter(line -> !line.trim().isEmpty())
                .map(Student::fromCsv)
                .collect(Collectors.toList());
    }

    public Map<String, Student> getAllStudentsMap() {
        Map<String, Student> map = new LinkedHashMap<>();
        for (Student s : getAllStudents()) map.put(s.getId(), s);
        return map;
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(getAllStudentsMap().get(id));
    }
}
