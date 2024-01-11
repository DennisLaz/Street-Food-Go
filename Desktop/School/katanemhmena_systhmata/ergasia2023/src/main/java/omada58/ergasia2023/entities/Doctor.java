package omada58.ergasia2023.entities;

import jakarta.persistence.*;
import org.hibernate.type.descriptor.jdbc.JdbcTypeFamilyInformation;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String area;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Family> families;


    public void addFamily(Family family) {
        families.add(family);
        family.setDoctor(this);
    }

    // constructor γιατρού ως user
    public Doctor(String firstName, String lastName, String username, String password, String area) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.area = area;
    }

    // setters and getters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    // το jpa χρειαζεται αδειο constructor(?)
    public Doctor() {
    }

    // toString για debugging
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
