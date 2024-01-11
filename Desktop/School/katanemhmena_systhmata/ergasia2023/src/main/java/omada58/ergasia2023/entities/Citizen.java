package omada58.ergasia2023.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "citizen")
public class Citizen {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String fistName;
    @Column
    private String lastName;
    @Column
    private String username;
    @Column
    private String password;

    // αποθήκευση της οικογένειας στην κλαση citizen μέσω του ξένου κλειδιου
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    // Πλήρες constructor. Χρησιμοποιείται για τη δημιουργία user-τυπου citizen
    public Citizen(String fistName, String lastName, String username, String password, Family family) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.family = family;
    }

    // Περιορισμένος constructor. Χρησιμοποιείται για τη δημιουργία family member-τύπου citizen
    public Citizen(String fistName, String lastName, Family family) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.family = family;
    }

    // κενό constructor
    public Citizen() {
    }

    // getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFistName() {
        return fistName;
    }

    public void setFistName(String fistName) {
        this.fistName = fistName;
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }


    // toString για debugging

    @Override
    public String toString() {
        return "Citizen{" +
                "id=" + id +
                ", fistName='" + fistName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", family=" + family +
                '}';
    }
}

