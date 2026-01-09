package gr.hua.dit.StreetFoodGo.web.ui.model;

import gr.hua.dit.StreetFoodGo.core.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;

public record OpenOrderForm(
        @NotNull @Positive Long restaurantId,
        @NotNull @NotBlank ArrayList<@Size(max=1000) OrderItem>orderItems
        ) {

}
