package com.example.application.views.kasir;

import com.example.application.models.Products;
import com.example.application.models.Categories;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.value.ValueChangeMode;

@PageTitle("Products | Kopi.in")
@Route(value = "kasir/products", layout = MainLayout.class)
public class ProductView extends VerticalLayout {
    private Grid<Products> productGrid;
    private TextField searchField;
    private ComboBox<String> categoryFilter;

    public ProductView() {
        addClassName("product-view");
        setSizeFull();
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.getStyle()
              .set("background-color", "#6B4423")
              .set("padding", "1rem")
              .set("color", "white");
        
        H3 title = new H3("Product Catalog");
        header.add(title);

        // Filter section
        HorizontalLayout filterSection = new HorizontalLayout();
        filterSection.setWidthFull();
        filterSection.setPadding(true);

        searchField = new TextField("Search Products");
        searchField.setPlaceholder("Enter product name");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        categoryFilter = new ComboBox<>("Category");
        categoryFilter.setPlaceholder("Select category");
        // TODO: Load categories from database
        categoryFilter.setItems("Coffee", "Non-Coffee", "Food", "Snacks");

        Button refreshBtn = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshBtn.getStyle()
                 .set("background-color", "#8B593E")
                 .set("color", "white");

        filterSection.add(searchField, categoryFilter, refreshBtn);

        // Product grid
        productGrid = new Grid<>(Products.class);
        productGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        productGrid.setColumns("name", "category_id", "price", "description", "stock");
        
        // Status column with colored badges
        productGrid.addComponentColumn(product -> {
            String status = product.getIs_active() == 1 ? "Available" : "Not Available";
            Button statusBadge = new Button(status);
            String color = product.getIs_active() == 1 ? "green" : "red";
            statusBadge.getStyle()
                      .set("background-color", color)
                      .set("color", "white")
                      .set("border-radius", "20px")
                      .set("padding", "5px 10px");
            return statusBadge;
        }).setHeader("Status");

        // Add components to main layout
        add(header, filterSection, productGrid);

        // Event handlers
        searchField.addValueChangeListener(e -> filterProducts());
        categoryFilter.addValueChangeListener(e -> filterProducts());
        refreshBtn.addClickListener(e -> refreshProducts());

        // Initial load
        refreshProducts();
    }

    private void filterProducts() {
        String searchTerm = searchField.getValue();
        String category = categoryFilter.getValue();

        // TODO: Implement actual filtering logic
    }

    private void refreshProducts() {
        // TODO: Implement product refresh from database
    }

    private void viewProductDetails(Products product) {
        // TODO: Implement product details view
    }
} 