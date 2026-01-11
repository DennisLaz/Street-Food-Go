package gr.hua.dit.StreetFoodGo.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order entity
 */
@Entity
@Table(
        name="orders",
        indexes = {
                @Index(name="idx_order_status", columnList = "status"),
                @Index(name="idx_order_customer", columnList = "customer_id"),
                @Index(name="idx_order_restaurant", columnList = "restaurant_id"),
                @Index(name="idx_order_queued_at", columnList = "queued_at"),
        }
)
public final class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(
            name = "order_seq",
            sequenceName = "order_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long orderId;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name= "customer_id" , nullable = false , foreignKey = @ForeignKey(name="fk_order_customer"))
    private Person customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name="restaurant_id", nullable = false,foreignKey = @ForeignKey(name = "fk_order_restaurant"))
    private Person restaurant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false , length = 16)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "queued_at", nullable = false, updatable = false)
    private Instant queuedAt;

    @Column(name = "in_Progress_at")
    private Instant inProgressAt;

    @Column(name = "completed_At")
    private Instant completedAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(MenuItem menuItem) {

        for (OrderItem item : items) {
            if (item.getMenuItem().getMenuItemId().equals(menuItem.getMenuItemId())) {
                item.increaseQuantity();
                return;
            }
        }

        OrderItem newItem = new OrderItem(this, menuItem);
        items.add(newItem);
    }

    public void removeItem(Long menuItemId) {
        items.removeIf(item ->
                item.getMenuItem().getMenuItemId().equals(menuItemId)
        );
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order(){}

    public Order(Long orderId, Person customer, Person restaurant, OrderStatus status, Instant queuedAt, Instant inProgressAt, Instant completedAt) {
        this.orderId = orderId;
        this.customer = customer;
        this.restaurant = restaurant;
        this.status = status;
        this.queuedAt = queuedAt;
        this.inProgressAt = inProgressAt;
        this.completedAt = completedAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public Person getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Person restaurant) {
        this.restaurant = restaurant;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(Instant queuedAt) {
        this.queuedAt = queuedAt;
    }

    public Instant getInProgressAt() {
        return inProgressAt;
    }

    public void setInProgressAt(Instant inProgressAt) {
        this.inProgressAt = inProgressAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public boolean isActive() {
        return status != OrderStatus.COMPLETED
                && status != OrderStatus.CANCELLED;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Order{");
        sb.append("orderId=").append(orderId);
        sb.append(", customer=").append(customer);
        sb.append(", restaurant=").append(restaurant);
        sb.append(", status=").append(status);
        sb.append(", queuedAt=").append(queuedAt);
        sb.append(", inProgressAt=").append(inProgressAt);
        sb.append(", completedAt=").append(completedAt);
        sb.append('}');
        return sb.toString();
    }
}

