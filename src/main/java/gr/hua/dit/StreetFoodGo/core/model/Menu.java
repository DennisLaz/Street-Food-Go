package gr.hua.dit.StreetFoodGo.core.model;

import gr.hua.dit.StreetFoodGo.web.ui.model.MenuItemRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "menus",
        indexes = {
                @Index(name = "idx_menu_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_menu_active", columnList = "active")
        }
)
public final class Menu {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "menu_seq"
    )
    @SequenceGenerator(
            name = "menu_seq",
            sequenceName = "menu_seq",
            allocationSize = 1
    )
    private Long menuId;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "restaurant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_restaurant")
    )
    private Person restaurant;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "menu",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MenuItem> items;

    public Menu() {}

    public Menu(Long menuId, Person restaurant, String title, boolean active, Instant createdAt) {
        this.menuId = menuId;
        this.restaurant = restaurant;
        this.title = title;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Person getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Person restaurant) {
        this.restaurant = restaurant;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(@NotEmpty List<MenuItem> items) {
        this.items = items;
    }

    public void addItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }

        if (items == null) {
            items = new ArrayList<>();
        }

        // Σύνδεση του item με αυτό το menu
        item.setMenu(this);

        // Προσθήκη στη λίστα του menu
        items.add(item);
    }

    public void removeItem(MenuItem item) {
        items.remove(item);
        item.setMenu(null);
    }
}
