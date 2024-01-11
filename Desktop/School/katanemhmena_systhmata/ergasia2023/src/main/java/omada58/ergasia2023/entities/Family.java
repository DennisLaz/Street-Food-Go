package omada58.ergasia2023.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "family")
public class Family {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @Column
    private String area;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // το id του γιατρου δε χρειάζεται στον constructor καθώς κατά τη δημιουργία ενός
    // instance οικογένειας δεν έχει ανατεθεί ακόμα γιατρός
    public Family(String area) {
        this.area = area;
    }

    // αδειο constructor
    public Family() {
    }

    // setters and getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    // για debugging
    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", area='" + area + '\'' +
                ", doctor=" + doctor +
                '}';
    }
}
