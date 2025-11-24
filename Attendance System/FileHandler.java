import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private final Path studentsPath;
    private final Path attendancePath;

    public FileHandler(String studentsFile, String attendanceFile) {
        this.studentsPath = Paths.get(studentsFile);
        this.attendancePath = Paths.get(attendanceFile);
        try {
            if (Files.notExists(studentsPath)) Files.createFile(studentsPath);
            if (Files.notExists(attendancePath)) Files.createFile(attendancePath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize data files", e);
        }
    }

    public synchronized void appendLine(Path path, String line) {
        try {
            Files.write(path, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to " + path, e);
        }
    }

    public synchronized List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException nf) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + path, e);
        }
    }

    public void addStudentCsv(String csvLine) {
        appendLine(studentsPath, csvLine);
    }

    public List<String> readStudentsCsv() {
        return readAllLines(studentsPath);
    }

    public void addAttendanceCsv(String csvLine) {
        appendLine(attendancePath, csvLine);
    }

    public List<String> readAttendanceCsv() {
        return readAllLines(attendancePath);
    }

    public Path getStudentsPath() { return studentsPath; }
    public Path getAttendancePath() { return attendancePath; }
}
