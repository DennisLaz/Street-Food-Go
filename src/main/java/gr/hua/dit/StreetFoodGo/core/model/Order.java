package gr.hua.dit.StreetFoodGo.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
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

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "subject" , length = 255)
    private String subject;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 1000)
    @Column(name = "customer_content" , length = 1000)
    private String customerContent;

//it can be null
    @Size(min = 1, max = 1000)
    @Column(name = "restaurant_content" ,  length = 1000)
    private String restaurantContent;

    @CreationTimestamp
    @Column(name = "queued_at", nullable = false, updatable = false)
    private Instant queuedAt;

    @Column(name = "in_Progress_at", nullable = false, updatable = false)
    private Instant inProgressAt;

    @Column(name = "completed_At", nullable = false, updatable = false)
    private Instant completedAt;

    public Order(){}

    public Order(Long orderId, Person customer, Person restaurant, OrderStatus status, String subject, String customerContent, String restaurantContent, Instant queuedAt, Instant inProgressAt, Instant completedAt) {
        this.orderId = orderId;
        this.customer = customer;
        this.restaurant = restaurant;
        this.status = status;
        this.subject = subject;
        this.customerContent = customerContent;
        this.restaurantContent = restaurantContent;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCustomerContent() {
        return customerContent;
    }

    public void setCustomerContent(String customerContent) {
        this.customerContent = customerContent;
    }

    public String getRestaurantContent() {
        return restaurantContent;
    }

    public void setRestaurantContent(String restaurantContent) {
        this.restaurantContent = restaurantContent;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Order{");
        sb.append("orderId=").append(orderId);
        sb.append(", customer=").append(customer);
        sb.append(", restaurant=").append(restaurant);
        sb.append(", status=").append(status);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", customerContent='").append(customerContent).append('\'');
        sb.append(", restaurantContent='").append(restaurantContent).append('\'');
        sb.append(", queuedAt=").append(queuedAt);
        sb.append(", inProgressAt=").append(inProgressAt);
        sb.append(", completedAt=").append(completedAt);
        sb.append('}');
        return sb.toString();
    }
}

