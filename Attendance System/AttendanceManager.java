import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceManager {
    private final FileHandler fh;

    public AttendanceManager(FileHandler fh) {
        this.fh = fh;
    }

    public void markAttendanceBatch(Map<String, Boolean> marks, LocalDate date) {
        // Prevent duplicate marking for same student+date by design choice: allow duplicates (append),
        // but here we will append; retrieving reports will consider all entries.
        for (Map.Entry<String, Boolean> e : marks.entrySet()) {
            AttendanceRecord r = new AttendanceRecord(e.getKey(), date, e.getValue());
            fh.addAttendanceCsv(r.toCsv());
        }
    }

    public List<AttendanceRecord> getAllRecords() {
        return fh.readAttendanceCsv().stream()
                .filter(line -> !line.trim().isEmpty())
                .map(AttendanceRecord::fromCsv)
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getRecordsByStudent(String studentId) {
        return getAllRecords().stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getRecordsByDate(LocalDate date) {
        return getAllRecords().stream()
                .filter(r -> r.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public AttendanceStats computeStatsForStudent(String studentId) {
        List<AttendanceRecord> list = getRecordsByStudent(studentId);
        int total = list.size();
        long present = list.stream().filter(AttendanceRecord::isPresent).count();
        return new AttendanceStats(studentId, total, (int) present);
    }

    public Map<String, AttendanceStats> computeAllStats() {
        Map<String, List<AttendanceRecord>> byStudent = getAllRecords().stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getStudentId, LinkedHashMap::new, Collectors.toList()));
        Map<String, AttendanceStats> stats = new LinkedHashMap<>();
        for (Map.Entry<String, List<AttendanceRecord>> e : byStudent.entrySet()) {
            int total = e.getValue().size();
            int present = (int) e.getValue().stream().filter(AttendanceRecord::isPresent).count();
            stats.put(e.getKey(), new AttendanceStats(e.getKey(), total, present));
        }
        return stats;
    }

    public static class AttendanceStats {
        public final String studentId;
        public final int totalDays;
        public final int presentDays;

        public AttendanceStats(String studentId, int totalDays, int presentDays) {
            this.studentId = studentId;
            this.totalDays = totalDays;
            this.presentDays = presentDays;
        }

        public double percentage() {
            return totalDays == 0 ? 0.0 : (presentDays * 100.0) / totalDays;
        }
    }
}
