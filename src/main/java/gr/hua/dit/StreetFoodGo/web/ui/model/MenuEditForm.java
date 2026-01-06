package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class MenuEditForm {

    @NotBlank
    private String title;

    private List<MenuItemUpdateForm> items = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuItemUpdateForm> getItems() {
        return items;
    }

    public void setItems(List<MenuItemUpdateForm> items) {
        this.items = items;
    }
}

