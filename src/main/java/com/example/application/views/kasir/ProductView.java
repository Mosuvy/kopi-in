package com.example.application.views.kasir;

import com.example.application.dao.ProductDAO;
import com.example.application.dao.CategoryDAO;
import com.example.application.models.Products;
import com.example.application.models.Categories;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.NumberRenderer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

@PageTitle("Products | Kopi.in")
@Route(value = "kasir/products", layout = MainLayout.class)
public class ProductView extends VerticalLayout {
    private Grid<Products> productGrid;
    private TextField searchField;
    private ComboBox<Categories> categoryFilter;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public ProductView() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();

        addClassName("product-view");
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)");

        add(createHeader(), createMainContent());
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #795548 50%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "25px 30px")
                .set("margin-bottom", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Background pattern overlay
        Div pattern = new Div();
        pattern.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-image", "radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 1px, transparent 1px)")
                .set("background-size", "30px 30px")
                .set("opacity", "0.4");
        header.getElement().appendChild(pattern.getElement());

            H2 title = new H2("Product Catalog");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        Span timeText = new Span(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeText.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8");

        header.add(title, timeText);
        return header;
    }

    private Component createMainContent() {
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("1200px");
        mainContainer.setWidth("100%");
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);

        // Filter Card
        VerticalLayout filterCard = new VerticalLayout();
        filterCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px 25px 15px");

        HorizontalLayout filterSection = new HorizontalLayout();
        filterSection.setWidthFull();
        filterSection.setAlignItems(FlexComponent.Alignment.BASELINE);
        filterSection.setSpacing(true);

        searchField = new TextField("Search Products");
        searchField.setPlaceholder("Enter product name");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        categoryFilter = new ComboBox<>("Category");
        categoryFilter.setPlaceholder("Select category");
        categoryFilter.setItems(categoryDAO.getAllCategories());
        categoryFilter.setItemLabelGenerator(Categories::getName);
        categoryFilter.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        Button refreshBtn = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshBtn.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #A0522D)")
                .set("color", "white")
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
                .set("transition", "all 0.3s ease");

        refreshBtn.getElement().addEventListener("mouseenter", e ->
            refreshBtn.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 15px rgba(139, 69, 19, 0.3)")
        );

        refreshBtn.getElement().addEventListener("mouseleave", e ->
            refreshBtn.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
        );

        filterSection.add(searchField, categoryFilter, refreshBtn);
        filterCard.add(filterSection);

        // Products Grid Card
        VerticalLayout gridCard = new VerticalLayout();
        gridCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px");

        // Enhanced grid styling
        productGrid = new Grid<>();
        productGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        productGrid.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.05)");

        // Configure grid columns
        productGrid.addColumn(Products::getName)
                .setHeader("Product Name")
                .setFlexGrow(2)
                .setSortable(true);

        productGrid.addColumn(product -> {
            Categories category = categoryDAO.getCategoryById(product.getCategory_id());
            return category != null ? category.getName() : "";
        })
                .setHeader("Category")
                .setFlexGrow(1)
                .setSortable(true);

        // Format price with currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("id").setRegion("ID").build());
        productGrid.addColumn(new NumberRenderer<>(
                Products::getPrice,
                currencyFormat))
                .setHeader("Price")
                .setFlexGrow(1)
                .setSortable(true);
        
        // Status column with enhanced styling
        productGrid.addComponentColumn(product -> {
            String status = product.getIs_active() == 1 ? "Available" : "Not Available";
            Button statusBadge = new Button(status);
            String color = product.getIs_active() == 1 ? "#2E7D32" : "#D32F2F";
            statusBadge.getStyle()
                    .set("background", color)
                    .set("color", "white")
                    .set("border-radius", "20px")
                    .set("padding", "5px 15px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("box-shadow", "0 2px 8px " + color + "40");
            return statusBadge;
        }).setHeader("Status").setFlexGrow(1);

        gridCard.add(productGrid);
        mainContainer.add(filterCard, gridCard);

        // Event handlers
        searchField.addValueChangeListener(e -> filterProducts());
        categoryFilter.addValueChangeListener(e -> filterProducts());
        refreshBtn.addClickListener(e -> refreshProducts());

        // Initial load
        refreshProducts();

        return mainContainer;
    }

    private void filterProducts() {
        String searchTerm = searchField.getValue().toLowerCase().trim();
        Categories selectedCategory = categoryFilter.getValue();

        List<Products> allProducts = productDAO.getListProduct();
        
        // Apply filters
        List<Products> filteredProducts = allProducts.stream()
                .filter(product -> 
                    (searchTerm.isEmpty() || 
                     product.getName().toLowerCase().contains(searchTerm) ||
                     product.getDescription().toLowerCase().contains(searchTerm)) &&
                    (selectedCategory == null || 
                     product.getCategory_id().equals(selectedCategory.getId())))
                .toList();

        productGrid.setItems(filteredProducts);

        // Show feedback if no results
        if (filteredProducts.isEmpty() && (!searchTerm.isEmpty() || selectedCategory != null)) {
            Notification notification = new Notification(
                "No products found matching your criteria", 
                3000, 
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            notification.open();
        }
    }

    private void refreshProducts() {
        // Clear filters
        searchField.clear();
        categoryFilter.clear();
        
        // Refresh grid
        List<Products> products = productDAO.getListProduct();
        productGrid.setItems(products);

        // Show success notification
        Notification notification = new Notification(
            "Products refreshed successfully", 
            3000, 
            Notification.Position.MIDDLE
        );
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void viewProductDetails(Products product) {
        // TODO: Implement product details view
    }
} 