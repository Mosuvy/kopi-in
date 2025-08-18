package com.example.application.views.auth;

import com.example.application.dao.UserDAO;
import com.example.application.models.Users;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Login - Kopi.in")
@Route("")
public class LoginPage extends HorizontalLayout {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginPage() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Modern coffee-themed background gradient
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        getStyle().set("min-height", "100vh");

        createLoginLayout();
    }

    private void createLoginLayout() {
        // Left side - Branding section
        VerticalLayout brandingSection = createBrandingSection();

        // Right side - Login form
        VerticalLayout formSection = createFormSection();

        // Main container - matching RegisterPage dimensions
        HorizontalLayout mainContainer = new HorizontalLayout(brandingSection, formSection);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("border-radius", "20px");
        mainContainer.getStyle().set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.setWidth("900px");
        mainContainer.setHeight("520px"); // Changed from 600px to match RegisterPage

        add(mainContainer);
    }

    private VerticalLayout createBrandingSection() {
        VerticalLayout brandingSection = new VerticalLayout();
        brandingSection.setWidth("400px");
        brandingSection.setHeight("100%");
        brandingSection.setAlignItems(FlexComponent.Alignment.CENTER);
        brandingSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // Warna coklat yang lebih cerah untuk branding section
        brandingSection.getStyle().set("background", "linear-gradient(135deg, #A0522D 0%, #CD853F 50%, #DEB887 100%)");
        brandingSection.getStyle().set("color", "white");
        brandingSection.setPadding(true);

        // Logo icon - matching RegisterPage size
        Image Kopi_in = new Image("/images/logo_kopi-in_dark.png", "Kopi.in Logo");
        Kopi_in.setWidth("180px"); // Reduced from 200px to match RegisterPage
        Kopi_in.getStyle().set("margin-bottom", "20px"); // Reduced from 30px to match RegisterPage

        // Tagline - matching RegisterPage size
        Paragraph tagline = new Paragraph("Kopiin Aja!");
        tagline.getStyle().set("font-size", "24px"); // Reduced from 28px to match RegisterPage
        tagline.getStyle().set("opacity", "0.9");
        tagline.getStyle().set("text-align", "center");
        tagline.getStyle().set("margin", "0 0 10px 0"); // Reduced from 15px to match RegisterPage
        // Text shadow untuk readability pada background yang lebih terang
        tagline.getStyle().set("text-shadow", "1px 1px 2px rgba(0, 0, 0, 0.3)");

        // Feature highlights
        VerticalLayout features = new VerticalLayout();
        features.setSpacing(false); // Remove spacing to match RegisterPage
        features.setAlignItems(FlexComponent.Alignment.START);
        features.getStyle().set("gap", "4px"); // Very small gap between items to match RegisterPage

        String[] featureList = {
                "Harga Murah & Terjangkau",
                "Rasa Kopi yang Khas & Nikmat",
                "Tempat Nyaman & Instagramable",
                "Pelayanan Cepat & Ramah"
        };

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("60px"); // Reduced from 80px to match RegisterPage
        // Warna icon disesuaikan dengan tema yang lebih cerah
        coffeeIcon.getStyle().set("color", "#8B4513");
        coffeeIcon.getStyle().set("margin-top", "15px"); // Reduced from 30px to match RegisterPage

        for (String feature : featureList) {
            HorizontalLayout featureItem = new HorizontalLayout();
            featureItem.setAlignItems(FlexComponent.Alignment.CENTER);
            featureItem.setSpacing(false);
            featureItem.getStyle().set("gap", "8px");

            Icon checkIcon = new Icon(VaadinIcon.CHECK);
            checkIcon.setSize("14px"); // Reduced from 16px to match RegisterPage
            // Warna check icon disesuaikan
            checkIcon.getStyle().set("color", "#8B4513");

            Paragraph featureText = new Paragraph(feature);
            featureText.getStyle().set("margin", "0");
            featureText.getStyle().set("font-size", "13px"); // Reduced from 14px to match RegisterPage
            featureText.getStyle().set("opacity", "0.95");
            // Text shadow untuk readability
            featureText.getStyle().set("text-shadow", "1px 1px 2px rgba(0, 0, 0, 0.2)");

            featureItem.add(checkIcon, featureText);
            features.add(featureItem);
        }

        brandingSection.add(Kopi_in, tagline, features, coffeeIcon);
        return brandingSection;
    }

    private VerticalLayout createFormSection() {
        VerticalLayout formSection = new VerticalLayout();
        formSection.setWidth("500px");
        formSection.setHeight("100%");
        formSection.setAlignItems(FlexComponent.Alignment.CENTER);
        formSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        formSection.setPadding(false); // Remove padding to match RegisterPage
        formSection.getStyle().set("padding", "20px"); // Manual padding control to match RegisterPage

        // Form container - matching RegisterPage width
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("300px"); // Reduced from 350px to match RegisterPage
        formContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        formContainer.setSpacing(false); // Remove default spacing for tighter control

        // Header - matching RegisterPage sizes
        H2 formTitle = new H2("Selamat Datang");
        formTitle.getStyle().set("color", "#3C1810");
        formTitle.getStyle().set("font-weight", "600");
        formTitle.getStyle().set("margin", "0 0 2px 0"); // Much tighter to match RegisterPage
        formTitle.getStyle().set("font-size", "24px"); // Reduced from 32px to match RegisterPage

        Paragraph formSubtitle = new Paragraph("Masuk ke akun Kopi.in Anda");
        formSubtitle.getStyle().set("color", "#8B4513");
        formSubtitle.getStyle().set("margin", "0 0 10px 0"); // Reduced from 25px to match RegisterPage
        formSubtitle.getStyle().set("font-size", "12px"); // Reduced from 16px to match RegisterPage

        // Form fields
        createFormFields(formContainer);

        // Register link
        createRegisterLink(formContainer);

        // Login button
        createLoginButton(formContainer);

        formSection.add(formTitle, formSubtitle, formContainer);
        return formSection;
    }

    private void createFormFields(VerticalLayout container) {
        // Username field - matching RegisterPage styling
        Span usernameLabel = new Span("Username");
        usernameLabel.getStyle().set("color", "#3C1810");
        usernameLabel.getStyle().set("font-weight", "500");
        usernameLabel.getStyle().set("font-size", "12px"); // Reduced from 14px to match RegisterPage
        usernameLabel.getStyle().set("margin-bottom", "2px"); // Changed from -8px to match RegisterPage
        usernameLabel.getStyle().set("display", "block");
        usernameLabel.getStyle().set("text-align", "left");
        usernameLabel.getStyle().set("width", "100%");
        usernameLabel.getStyle().set("align-self", "flex-start");

        usernameField = new TextField();
        usernameField.setWidth("100%");
        usernameField.getStyle().set("margin-bottom", "4px"); // Changed from 20px to match RegisterPage
        usernameField.getStyle().set("--lumo-border-radius", "8px");
        usernameField.getStyle().set("height", "32px"); // Reduced from 50px to match RegisterPage

        // Password field - matching RegisterPage styling
        Span passwordLabel = new Span("Password");
        passwordLabel.getStyle().set("color", "#3C1810");
        passwordLabel.getStyle().set("font-weight", "500");
        passwordLabel.getStyle().set("font-size", "12px"); // Reduced from 14px to match RegisterPage
        passwordLabel.getStyle().set("margin-bottom", "2px"); // Changed from -8px to match RegisterPage
        passwordLabel.getStyle().set("display", "block");
        passwordLabel.getStyle().set("text-align", "left");
        passwordLabel.getStyle().set("width", "100%");
        passwordLabel.getStyle().set("align-self", "flex-start");

        passwordField = new PasswordField();
        passwordField.setWidth("100%");
        passwordField.getStyle().set("margin-bottom", "8px"); // Changed from 15px to match RegisterPage
        passwordField.getStyle().set("--lumo-border-radius", "8px");
        passwordField.getStyle().set("height", "32px"); // Reduced from 50px to match RegisterPage

        usernameField.getElement().executeJs(
                "this.style.setProperty('--lumo-primary-color', '#007bff');" +
                        "this.addEventListener('focus', () => {" +
                        "  this.style.setProperty('--lumo-primary-color', '#007bff');" +
                        "});"
        );

        passwordField.getElement().executeJs(
                "this.style.setProperty('--lumo-primary-color', '#007bff');" +
                        "this.addEventListener('focus', () => {" +
                        "  this.style.setProperty('--lumo-primary-color', '#007bff');" +
                        "});"
        );

        container.add(usernameLabel, usernameField, passwordLabel, passwordField);
    }

    private void createRegisterLink(VerticalLayout container) {
        HorizontalLayout registerLayout = new HorizontalLayout();
        registerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        registerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        registerLayout.getStyle().set("margin-bottom", "6px"); // Changed from 20px to match RegisterPage
        registerLayout.setSpacing(false);

        Span registerText = new Span("Belum Punya Akun? ");
        registerText.getStyle().set("color", "#666");
        registerText.getStyle().set("font-size", "12px"); // Reduced from 14px to match RegisterPage
        registerText.getStyle().set("margin-right", "4px");

        // Use Button instead of Anchor for clickable functionality
        Button registerLink = new Button("Register disini");
        registerLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        registerLink.getStyle().set("color", "#007bff");
        registerLink.getStyle().set("text-decoration", "none");
        registerLink.getStyle().set("font-size", "12px"); // Reduced from 14px to match RegisterPage
        registerLink.getStyle().set("font-weight", "500");
        registerLink.getStyle().set("transition", "all 0.3s ease");
        registerLink.getStyle().set("padding", "0");
        registerLink.getStyle().set("margin", "0");
        registerLink.getStyle().set("min-height", "unset");
        registerLink.getStyle().set("background", "transparent");
        registerLink.getStyle().set("border", "none");
        registerLink.getStyle().set("outline", "none");

        // hover effect for register link
        registerLink.getElement().executeJs(
                "this.addEventListener('mouseenter', () => {" +
                        "  this.style.textDecoration = 'underline';" +
                        "  this.style.color = '#0056b3';" +
                        "});" +
                        "this.addEventListener('mouseleave', () => {" +
                        "  this.style.textDecoration = 'none';" +
                        "  this.style.color = '#007bff';" +
                        "});"
        );

        // Click handler for register link
        registerLink.addClickListener(event -> {
            // Navigate to register page
            getUI().ifPresent(ui -> ui.navigate("register"));
        });

        registerLayout.add(registerText, registerLink);
        container.add(registerLayout);
    }

    private void createLoginButton(VerticalLayout container) {
        loginButton = new Button("Login");
        loginButton.setWidth("100%");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 100%)");
        loginButton.getStyle().set("border", "none");
        loginButton.getStyle().set("border-radius", "8px");
        loginButton.getStyle().set("height", "36px"); // Reduced from 50px to match RegisterPage
        loginButton.getStyle().set("font-weight", "600");
        loginButton.getStyle().set("font-size", "14px"); // Reduced from 16px to match RegisterPage
        loginButton.getStyle().set("cursor", "pointer");
        loginButton.getStyle().set("transition", "all 0.3s ease");

        // Hover effect with coffee theme
        loginButton.getElement().executeJs(
                "this.addEventListener('mouseenter', () => {" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 10px 25px rgba(139, 69, 19, 0.3)';" +
                        "});" +
                        "this.addEventListener('mouseleave', () => {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );

        // Click handler
        loginButton.addClickListener(event -> handleLogin());

        container.add(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getValue().trim();
        String password = passwordField.getValue();

        // Validation
        if (username.isEmpty()) {
            showErrorNotification("Please enter your username");
            usernameField.focus();
            return;
        }

        if (password.isEmpty()) {
            showErrorNotification("Please enter your password");
            passwordField.focus();
            return;
        }

        // Show loading state
        loginButton.setText("Logging in...");
        loginButton.setEnabled(false);

        // Simulate login process
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                try {
                    Thread.sleep(1000); // Simulate processing
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Reset button
                loginButton.setText("Login");
                loginButton.setEnabled(true);

                // Simple validation (replace with actual authentication)
                Users user = validateCredentials(username, password);
                if (user != null) {
                    showSuccessNotification("Welcome back to Kopi.in!");

                    VaadinSession.getCurrent().setAttribute("user", user);

                    // Redirect based on role
                    String role = user.getRole().toLowerCase();
                    getUI().ifPresent(u -> {
                        switch (role) {
                            case "admin":
                                ui.navigate("admin/dashboard");
                                break;
                            case "kasir":
                                ui.navigate("kasir");
                                break;
                            case "customer":
                                ui.navigate("customer");
                                break;
                            default:
                                ui.navigate("login"); // fallback
                                break;
                        }
                    });

                } else {
                    showErrorNotification("Invalid username or password");
                    passwordField.clear();
                    passwordField.focus();
                }
            });
        });
    }

    private Users validateCredentials(String username, String password) {
        UserDAO userDAO = new UserDAO();
        Users user = userDAO.login(username, password);
        if (user != null && user.getIs_active() == 1) {
            return user;
        }
        return null;
    }

    private void redirectToDashboard() {
        // Redirect to main dashboard
        getUI().ifPresent(ui -> ui.navigate("dashboard"));
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}