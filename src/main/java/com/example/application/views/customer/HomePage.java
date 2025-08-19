package com.example.application.views.customer;

import com.example.application.dao.FeedbackDAO;
import com.example.application.dao.CategoryDAOC;
import com.example.application.dao.ProductDAOC;
import com.example.application.models.*;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.*;
import java.util.stream.Collectors;

@PageTitle("kopi-In")
@Route(value = "customer", layout = AppLayoutNavbar.class)
public class HomePage extends VerticalLayout {

    // DAO
    private final ProductDAOC productDAO = new ProductDAOC();
    private final CategoryDAOC categoryDAO = new CategoryDAOC();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    // UI Components
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoryFilter = new ComboBox<>("Pilih kategori");
    private Div menuSection;
    private TextArea feedbackInput;

    public HomePage() {
        initializeLayout();
        setupMainContent();
    }

    private void initializeLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    private void setupMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setPadding(false);
        mainContent.setSpacing(false);

        setupSearchField();
        setupCategoryFilter();

        this.menuSection = createMenuSection();

        mainContent.add(
                createHeroSection(),
                createFilterSection(),
                this.menuSection
        );

        add(mainContent, createFooter());
        setFlexGrow(1, mainContent);
    }

    // ================== Section Components ==================

    private Div createHeroSection() {
        Div heroContainer = new Div();
        heroContainer.setId("hero-section");
        heroContainer.addClassName("hero-section");

        Image bgImage = new Image(
                "https://images.unsplash.com/photo-1447933601403-0c6688de566e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2061&q=80",
                "Background kopi"
        );
        bgImage.addClassName("hero-bg");

        VerticalLayout heroContent = new VerticalLayout();
        heroContent.addClassName("hero-content");

        H1 mainTitle = new H1("kopiIn");
        mainTitle.addClassName("hero-title");

        Span subtitle = new Span("Pesan kopi dengan mudah dan cepat");
        subtitle.addClassName("hero-subtitle");

        Button orderButton = createHeroButton();
        heroContent.add(mainTitle, subtitle, orderButton);
        heroContainer.add(bgImage, heroContent);

        return heroContainer;
    }

    private Button createHeroButton() {
        Button button = new Button("Pesan Sekarang");
        button.addClassName("order-button");

        button.getElement().executeJs(
                "this.addEventListener('mouseenter', function() {" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 6px 20px rgba(205, 133, 63, 0.6)';" +
                        "});" +
                        "this.addEventListener('mouseleave', function() {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = '0 4px 15px rgba(205, 133, 63, 0.4)';" +
                        "});"
        );

        button.addClickListener(event -> scrollToFilterSection());
        return button;
    }

    private void scrollToFilterSection() {
        UI.getCurrent().getPage().executeJs(
                "document.getElementById('filter-section').scrollIntoView({" +
                        "  behavior: 'smooth'," +
                        "  block: 'start'" +
                        "});"
        );
    }

    private Div createFilterSection() {
        Div filterSection = new Div();
        filterSection.setId("filter-section");
        filterSection.setClassName("filter-section");

        Button removeFilterButton = createRemoveFilterButton();

        HorizontalLayout filterLayout = new HorizontalLayout(
                searchField,
                categoryFilter,
                removeFilterButton
        );
        filterLayout.setSpacing(true);
        filterLayout.setAlignItems(FlexComponent.Alignment.END);

        filterSection.add(filterLayout);
        return filterSection;
    }

    private void setupSearchField() {
        searchField.setPlaceholder("Cari produk...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.addClassName("search-input");
        searchField.addValueChangeListener(e -> applyFilters());
    }

    private void setupCategoryFilter() {
        List<CategoriesC> categories = categoryDAO.getListCategories();

        List<String> categoryNames = categories.stream()
                .map(CategoriesC::getName)
                .collect(Collectors.toList());
        categoryNames.add(0, "Semua");

        categoryFilter.setItems(categoryNames);
        categoryFilter.setValue("Semua");
        categoryFilter.setWidth("200px");
        categoryFilter.addClassName("kategori-dropdown");
        categoryFilter.addValueChangeListener(e -> applyFilters());
    }

    private Button createRemoveFilterButton() {
        Button button = new Button(VaadinIcon.CLOSE_SMALL.create());
        button.getElement().setProperty("title", "Hapus filter kategori");
        button.getStyle()
                .set("margin-left", "5px")
                .set("color", "white")
                .set("background-color", "red")
                .set("border", "none")
                .set("border-radius", "5px")
                .set("padding", "0 5px")
                .set("cursor", "pointer");

        button.addClickListener(e -> {
            searchField.clear();
            categoryFilter.setValue("Semua");
        });

        return button;
    }

    private Div createMenuSection() {
        Div section = new Div();
        section.setId("menu-section");
        section.addClassName("menu-section");

        List<ProductsC> activeProducts = getActiveProducts();
        Map<String, List<ProductsC>> productsByCategory = groupProductsByCategory(activeProducts);

        updateMenuContent(section, productsByCategory);
        return section;
    }

    private void updateMenuContent(Div menuContainer, Map<String, List<ProductsC>> productsByCategory) {
        menuContainer.removeAll();

        if (productsByCategory == null || productsByCategory.isEmpty()) {
            menuContainer.add(new Paragraph("Produk tidak ditemukan."));
            return;
        }

        categoryDAO.getListCategories().forEach(category -> {
            List<ProductsC> categoryProducts = productsByCategory.get(category.getId());
            if (categoryProducts != null && !categoryProducts.isEmpty()) {
                menuContainer.add(
                        createCategoryTitle(category.getName()),
                        createProductGrid(categoryProducts)
                );
            }
        });
    }

    private List<ProductsC> getActiveProducts() {
        return productDAO.getListProduct().stream()
                .filter(p -> p.getIs_active() != null && p.getIs_active() == 1)
                .collect(Collectors.toList());
    }

    private Map<String, List<ProductsC>> groupProductsByCategory(List<ProductsC> products) {
        return products.stream()
                .collect(Collectors.groupingBy(ProductsC::getCategory_id));
    }

    private H2 createCategoryTitle(String categoryName) {
        H2 title = new H2(categoryName);
        title.addClassName("kategori-title");
        return title;
    }

    private Div createProductGrid(List<ProductsC> products) {
        Div grid = new Div();
        grid.addClassName("product-grid");

        products.forEach(product ->
                grid.add(createProductCard(
                        product.getId(),
                        product.getName(),
                        product.getImage_url(),
                        product.getPrice(),
                        product.getCategory_id()
                ))
        );

        return grid;
    }

    private void applyFilters() {
        List<ProductsC> filteredProducts = getFilteredProducts(
                searchField.getValue(),
                getSelectedCategory()
        );
        Map<String, List<ProductsC>> groupedProducts = groupProductsByCategory(filteredProducts);
        updateMenuContent(this.menuSection, groupedProducts);
    }

    private List<ProductsC> getFilteredProducts(String searchTerm, String categoryName) {
        List<ProductsC> products = getActiveProducts();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            products = filterBySearchTerm(products, searchTerm);
        }

        if (categoryName != null && !categoryName.equals("Semua")) {
            products = filterByCategory(products, categoryName);
        }

        return products;
    }

    private List<ProductsC> filterBySearchTerm(List<ProductsC> products, String term) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(term.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<ProductsC> filterByCategory(List<ProductsC> products, String categoryName) {
        String categoryId = categoryDAO.getListCategories().stream()
                .filter(c -> c.getName().equals(categoryName))
                .map(CategoriesC::getId)
                .findFirst()
                .orElse(null);

        if (categoryId == null) return products;

        return products.stream()
                .filter(p -> p.getCategory_id().equals(categoryId))
                .collect(Collectors.toList());
    }

    private String getSelectedCategory() {
        String selected = categoryFilter.getValue();
        return selected != null ? selected : "Semua";
    }

    // ================== Product Card ==================

    private Div createProductCard(String id, String name, String imageUrl, double price, String category) {
        Div card = new Div();
        card.addClassName("product-card");
        card.getElement().setAttribute("product-id", id);
        card.addClassName("kategori-" + category.toLowerCase().replaceAll("\\s+", "-"));

        Image img = createProductImage(name, imageUrl);
        H3 title = createProductTitle(name);
        Span priceSpan = createProductPrice(price);
        Button addButton = createAddToCartButton(id, name, price, imageUrl);

        card.add(img, title, priceSpan, addButton);
        return card;
    }

    private Image createProductImage(String altText, String imageUrl) {
        Image img = new Image();
        img.addClassName("product-image");
        img.setAlt(altText);
        img.setSrc(imageUrl == null || imageUrl.isEmpty()
                ? "https://placehold.co/400?text=No+Image"
                : "images/products/" + imageUrl);
        return img;
    }

    private H3 createProductTitle(String name) {
        H3 title = new H3(name);
        title.addClassName("product-title");
        return title;
    }

    private Span createProductPrice(double price) {
        Span priceSpan = new Span("Rp " + String.format("%,.0f", price));
        priceSpan.addClassName("product-price");
        return priceSpan;
    }

    private Button createAddToCartButton(String productId, String productName, double price, String imageUrl) {
        Button button = new Button("Masukkan Keranjang");
        button.addClassName("add-to-cart-button");

        button.addClickListener(event -> {
            addToCart(productId, productName, price, imageUrl);
            showSuccessNotification(productName + " ditambahkan ke keranjang");
        });

        return button;
    }

    private void addToCart(String productId, String productName, double price, String imageUrl) {
        List<CartItem> cartItems = getCartItems();

        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {
            cartItems.add(new CartItem(productId, productName, price, 1, imageUrl));
        }

        updateSessionCart(cartItems);
        updateNavbarCart();
    }

    private List<CartItem> getCartItems() {
        List<CartItem> cart = (List<CartItem>) VaadinSession.getCurrent().getAttribute("cart");
        return cart != null ? new ArrayList<>(cart) : new ArrayList<>();
    }

    private void updateSessionCart(List<CartItem> cartItems) {
        VaadinSession.getCurrent().setAttribute("cart", cartItems);
    }

    private void updateNavbarCart() {
        UI.getCurrent().access(() -> {
            UI.getCurrent().getChildren()
                    .filter(AppLayoutNavbar.class::isInstance)
                    .map(AppLayoutNavbar.class::cast)
                    .findFirst()
                    .ifPresent(AppLayoutNavbar::updateCart);
        });
    }

    // ================== Footer ==================

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassName("footer");

        VerticalLayout footerContent = new VerticalLayout();
        footerContent.addClassName("kopi-footer-content");
        footerContent.setSpacing(true);
        footerContent.setPadding(true);
        footerContent.setWidthFull();
        footerContent.setAlignItems(Alignment.CENTER);

        footerContent.add(
                createFooterBody(),
                createCopyright()
        );

        footer.add(footerContent);
        return footer;
    }

    private HorizontalLayout createFooterBody() {
        HorizontalLayout body = new HorizontalLayout();
        body.addClassName("footer-body");
        body.add(
                createInfoSection(),
                createFeedbackSection()
        );
        return body;
    }

    private Div createCopyright() {
        Div copyright = new Div();
        copyright.addClassName("footer-copyright");
        copyright.add(new Span("© 2025 KopiIn. All rights reserved."));
        return copyright;
    }

    private VerticalLayout createInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.setWidth("50%");

        section.add(
                createAppTitle(),
                createTagline(),
                createContactInfo()
        );

        return section;
    }

    private H1 createAppTitle() {
        H1 title = new H1("kopi-In");
        title.addClassName("footer-info-title");
        return title;
    }

    private Span createTagline() {
        Span tagline = new Span("Pesan kopi dengan cepat tanpa harus ke kasir.");
        tagline.addClassName("footer-info-sub");
        return tagline;
    }

    private Span createContactInfo() {
        Span contact = new Span("Kontak: kopi@kopiin.com | 0821-1338-7838");
        contact.addClassName("footer-info-contact");
        return contact;
    }

    private VerticalLayout createFeedbackSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.setWidth("45%");

        // The feedbackInput is now a field, initialized here
        this.feedbackInput = createFeedbackInput();

        section.add(
                createFeedbackTitle(),
                this.feedbackInput,
                createSubmitButton()
        );

        return section;
    }

    private H3 createFeedbackTitle() {
        H3 title = new H3("Kirim Masukan Anda");
        title.getStyle()
                .set("margin-top", "0")
                .set("color", "white");
        return title;
    }

    private TextArea createFeedbackInput() {
        TextArea input = new TextArea();
        input.setPlaceholder("Pesan kamu...");
        input.setWidthFull();
        input.setHeight("100px");
        input.addClassName("footer-feedback-textarea");
        return input;
    }

    private Button createSubmitButton() {
        Button button = new Button("Kirim Feedback");
        button.addClassName("footer-feedback-button");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(this::handleFeedbackSubmission);
        return button;
    }

    private void handleFeedbackSubmission(ClickEvent<Button> event) {
        if (feedbackInput == null) return;

        String message = feedbackInput.getValue().trim();
        if (message.isEmpty()) {
            showErrorNotification("Masukkan pesan feedback terlebih dahulu");
            return;
        }

        Users currentUser = (Users) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null) {
            showErrorNotification("Anda harus login untuk mengirim feedback.");
            return; // Don't clear the input if user is not logged in
        }

        boolean success = submitFeedback(message, currentUser.getId());
        if (success) {
            feedbackInput.clear();
            showSuccessNotification("Terima kasih atas feedback Anda!");
        } else {
            showErrorNotification("Gagal mengirim feedback. Silakan coba lagi.");
        }
    }

    private boolean submitFeedback(String message, String userId) {
        Feedback feedback = new Feedback();
        feedback.setMessage(message);
        feedback.setStatus("unread");
        feedback.setUser_id(userId);
        return feedbackDAO.createFeedback(feedback);
    }

    // ================== Notification Helpers ==================

    private void showSuccessNotification(String message) {
        Notification notification = new Notification(message, 3000);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void showErrorNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}