package gr.hua.dit.StreetFoodGo.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(
        name = "menu_items",
        indexes = {
                @Index(name = "idx_menu_item_menu", columnList = "menu_id"),
                @Index(name = "idx_menu_item_available", columnList = "available")
        }
)
public final class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_menu")
    )
    private Menu menu;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    public MenuItem() {}

    public MenuItem(Long menuItemId, Menu menu, String name, String description, BigDecimal price, boolean available) {
        this.id = menuItemId;
        this.menu = menu;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public Long getMenuItemId() {
        return id;
    }

    public void setMenuItemId(Long menuItemId) {
        this.id = menuItemId;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
