package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class MenuCreateForm {

    @NotBlank
    private String title;

    @Valid
    private List<MenuItemForm> items = new ArrayList<>();

    public MenuCreateForm() {
        items.add(new MenuItemForm()); // τουλάχιστον ένα item
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuItemForm> getItems() {
        return items;
    }

    public void setItems(List<MenuItemForm> items) {
        this.items = items;
    }
}
