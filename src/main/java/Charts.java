import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Charts {
    private final AttendanceDatabase attendanceDatabase;
    private final StudentDatabase studentDatabase;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE"); //for bar labels

    public Charts(AttendanceDatabase attendanceDatabase, StudentDatabase studentDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.studentDatabase = studentDatabase;
    }

    public ObservableList<XYChart.Data<String, Number>> getStudentDataBarChart(int studentId, LocalDate start, LocalDate end) throws SQLException {
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        int minutes;
        while (!start.isAfter(end)) {
            minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == studentId && record.getSignInTime().toLocalDate().equals(start) && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            data.add(new XYChart.Data<>(start.format(formatter), minutes/60.0));
            start = start.plusDays(1);
        }
        return data;
    }

    public ObservableList<XYChart.Data<String, Number>> getAllStudentDataBarChart(LocalDate start, LocalDate end) throws SQLException {
        List<Student> students = studentDatabase.getAllStudents();
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        int minutes;
        for (Student student : students) {
            minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId() && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            data.add(new XYChart.Data<>(student.getName(), minutes/60.0));
        }
        return data;
    }

    public ObservableList<PieChart.Data> getAllStudentDataPieChart(LocalDate start, LocalDate end) throws SQLException {
        List<Student> students = studentDatabase.getAllStudents();
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<PieChart.Data> slices = FXCollections.observableArrayList();
        int total = 0;
        for (AttendanceRecord record : records) {
            if(record.getSignOutTime() != null) {
                total += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
            }
        }
        if (total == 0) {
            return slices;
        }
        for (Student student : students) {
            int minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId() && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            slices.add(new PieChart.Data(student.getName() + " " + String.format("%.2f", (minutes*100.0)/total), minutes/60.0));
        }
        return slices;
    }
}
