package com.example.application.views.auth;

import com.vaadin.flow.component.HasElement;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterPage extends HorizontalLayout {

    private TextField usernameField;
    private EmailField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button registerButton;

    public RegisterPage() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Modern coffee-themed background gradient
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        getStyle().set("min-height", "100vh");

        createRegisterLayout();
    }

    private void createRegisterLayout() {
        // Left side - Register form
        VerticalLayout formSection = createFormSection();

        // Right side - Branding section
        VerticalLayout brandingSection = createBrandingSection();

        // Main container
        HorizontalLayout mainContainer = new HorizontalLayout(formSection, brandingSection);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("border-radius", "20px");
        mainContainer.getStyle().set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.setWidth("900px");
        mainContainer.setHeight("520px"); // Keep original container size

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

        // Logo icon
        Image Kopi_in = new Image("/images/logo_kopi-in_dark.png", "Kopi.in Logo");
        Kopi_in.setWidth("180px"); // Reduced from 200px
        Kopi_in.getStyle().set("margin-bottom", "20px"); // Reduced from 30px

        // Tagline
        Paragraph tagline = new Paragraph("Bergabung dengan Kopi.in!");
        tagline.getStyle().set("font-size", "24px"); // Reduced from 28px
        tagline.getStyle().set("opacity", "0.9");
        tagline.getStyle().set("text-align", "center");
        tagline.getStyle().set("margin", "0 0 10px 0"); // Reduced from 15px
        // Text shadow untuk readability pada background yang lebih terang
        tagline.getStyle().set("text-shadow", "1px 1px 2px rgba(0, 0, 0, 0.3)");

        // Feature highlights
        VerticalLayout features = new VerticalLayout();
        features.setSpacing(false); // Remove spacing
        features.setAlignItems(FlexComponent.Alignment.START);
        features.getStyle().set("gap", "4px"); // Very small gap between items

        String[] featureList = {
                "Dapatkan Promo Eksklusif",
                "Loyalty Points Setiap Pembelian",
                "Pre-order Menu Favorit",
                "Notifikasi Menu & Event Terbaru"
        };

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("60px"); // Reduced from 80px
        // Warna icon disesuaikan dengan tema yang lebih cerah
        coffeeIcon.getStyle().set("color", "#8B4513");
        coffeeIcon.getStyle().set("margin-top", "15px"); // Reduced from 30px

        for (String feature : featureList) {
            HorizontalLayout featureItem = new HorizontalLayout();
            featureItem.setAlignItems(FlexComponent.Alignment.CENTER);
            featureItem.setSpacing(false);
            featureItem.getStyle().set("gap", "8px");

            Icon checkIcon = new Icon(VaadinIcon.CHECK);
            checkIcon.setSize("14px"); // Reduced from 16px
            // Warna check icon disesuaikan
            checkIcon.getStyle().set("color", "#8B4513");

            Paragraph featureText = new Paragraph(feature);
            featureText.getStyle().set("margin", "0");
            featureText.getStyle().set("font-size", "13px"); // Reduced from 14px
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
        formSection.setPadding(false); // Remove padding
        formSection.getStyle().set("padding", "20px"); // Manual padding control

        // Form container
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("300px"); // Further reduced to fit better
        formContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        formContainer.setSpacing(false); // Remove default spacing for tighter control

        // Header
        H2 formTitle = new H2("Buat Akun Baru");
        formTitle.getStyle().set("color", "#3C1810");
        formTitle.getStyle().set("font-weight", "600");
        formTitle.getStyle().set("margin", "0 0 2px 0"); // Much tighter
        formTitle.getStyle().set("font-size", "24px"); // Further reduced

        Paragraph formSubtitle = new Paragraph("Daftar untuk menikmati pengalaman Kopi.in");
        formSubtitle.getStyle().set("color", "#8B4513");
        formSubtitle.getStyle().set("margin", "0 0 10px 0"); // Tighter spacing
        formSubtitle.getStyle().set("font-size", "12px"); // Further reduced

        // Form fields
        createFormFields(formContainer);

        // Login link
        createLoginLink(formContainer);

        // Register button
        createRegisterButton(formContainer);

        formSection.add(formTitle, formSubtitle, formContainer);
        return formSection;
    }

    private void createFormFields(VerticalLayout container) {
        // Username field
        Span usernameLabel = new Span("Username");
        usernameLabel.getStyle().set("color", "#3C1810");
        usernameLabel.getStyle().set("font-weight", "500");
        usernameLabel.getStyle().set("font-size", "12px"); // Further reduced
        usernameLabel.getStyle().set("margin-bottom", "2px");
        usernameLabel.getStyle().set("display", "block");
        usernameLabel.getStyle().set("text-align", "left");
        usernameLabel.getStyle().set("width", "100%");
        usernameLabel.getStyle().set("align-self", "flex-start");

        usernameField = new TextField();
        usernameField.setWidth("100%");
        usernameField.getStyle().set("margin-bottom", "4px"); // Much tighter spacing
        usernameField.getStyle().set("--lumo-border-radius", "8px");
        usernameField.getStyle().set("height", "32px"); // Much smaller

        // Email field
        Span emailLabel = new Span("Email");
        emailLabel.getStyle().set("color", "#3C1810");
        emailLabel.getStyle().set("font-weight", "500");
        emailLabel.getStyle().set("font-size", "12px"); // Further reduced
        emailLabel.getStyle().set("margin-bottom", "2px");
        emailLabel.getStyle().set("display", "block");
        emailLabel.getStyle().set("text-align", "left");
        emailLabel.getStyle().set("width", "100%");
        emailLabel.getStyle().set("align-self", "flex-start");

        emailField = new EmailField();
        emailField.setWidth("100%");
        emailField.getStyle().set("margin-bottom", "4px");
        emailField.getStyle().set("--lumo-border-radius", "8px");
        emailField.getStyle().set("height", "32px");

        // Password field
        Span passwordLabel = new Span("Password");
        passwordLabel.getStyle().set("color", "#3C1810");
        passwordLabel.getStyle().set("font-weight", "500");
        passwordLabel.getStyle().set("font-size", "12px");
        passwordLabel.getStyle().set("margin-bottom", "2px");
        passwordLabel.getStyle().set("display", "block");
        passwordLabel.getStyle().set("text-align", "left");
        passwordLabel.getStyle().set("width", "100%");
        passwordLabel.getStyle().set("align-self", "flex-start");

        passwordField = new PasswordField();
        passwordField.setWidth("100%");
        passwordField.getStyle().set("margin-bottom", "4px");
        passwordField.getStyle().set("--lumo-border-radius", "8px");
        passwordField.getStyle().set("height", "32px");

        // Confirm Password field
        Span confirmPasswordLabel = new Span("Konfirmasi Password");
        confirmPasswordLabel.getStyle().set("color", "#3C1810");
        confirmPasswordLabel.getStyle().set("font-weight", "500");
        confirmPasswordLabel.getStyle().set("font-size", "12px");
        confirmPasswordLabel.getStyle().set("margin-bottom", "2px");
        confirmPasswordLabel.getStyle().set("display", "block");
        confirmPasswordLabel.getStyle().set("text-align", "left");
        confirmPasswordLabel.getStyle().set("width", "100%");
        confirmPasswordLabel.getStyle().set("align-self", "flex-start");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setWidth("100%");
        confirmPasswordField.getStyle().set("margin-bottom", "8px"); // Slightly more space before link
        confirmPasswordField.getStyle().set("--lumo-border-radius", "8px");
        confirmPasswordField.getStyle().set("height", "32px");

        // Apply focus styles to all fields
        HasElement[] fields = {usernameField, emailField, passwordField, confirmPasswordField};
        for (HasElement field : fields) {
            field.getElement().executeJs(
                    "this.style.setProperty('--lumo-primary-color', '#007bff');" +
                            "this.addEventListener('focus', () => {" +
                            "  this.style.setProperty('--lumo-primary-color', '#007bff');" +
                            "});"
            );
        }

        container.add(usernameLabel, usernameField,
                emailLabel, emailField, passwordLabel, passwordField,
                confirmPasswordLabel, confirmPasswordField);
    }

    private void createLoginLink(VerticalLayout container) {
        HorizontalLayout loginLayout = new HorizontalLayout();
        loginLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        loginLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginLayout.getStyle().set("margin-bottom", "6px"); // Much tighter
        loginLayout.setSpacing(false);

        Span loginText = new Span("Sudah Punya Akun? ");
        loginText.getStyle().set("color", "#666");
        loginText.getStyle().set("font-size", "12px"); // Further reduced
        loginText.getStyle().set("margin-right", "4px");

        // Use Button instead of Anchor for clickable functionality
        Button loginLink = new Button("Login disini");
        loginLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        loginLink.getStyle().set("color", "#007bff");
        loginLink.getStyle().set("text-decoration", "none");
        loginLink.getStyle().set("font-size", "12px"); // Further reduced
        loginLink.getStyle().set("font-weight", "500");
        loginLink.getStyle().set("transition", "all 0.3s ease");
        loginLink.getStyle().set("padding", "0");
        loginLink.getStyle().set("margin", "0");
        loginLink.getStyle().set("min-height", "unset");
        loginLink.getStyle().set("background", "transparent");
        loginLink.getStyle().set("border", "none");
        loginLink.getStyle().set("outline", "none");

        // hover effect for login link
        loginLink.getElement().executeJs(
                "this.addEventListener('mouseenter', () => {" +
                        "  this.style.textDecoration = 'underline';" +
                        "  this.style.color = '#0056b3';" +
                        "});" +
                        "this.addEventListener('mouseleave', () => {" +
                        "  this.style.textDecoration = 'none';" +
                        "  this.style.color = '#007bff';" +
                        "});"
        );

        // Click handler for login link
        loginLink.addClickListener(event -> {
            // Navigate to login page
            getUI().ifPresent(ui -> ui.navigate("/"));
        });

        loginLayout.add(loginText, loginLink);
        container.add(loginLayout);
    }

    private void createRegisterButton(VerticalLayout container) {
        registerButton = new Button("Daftar Sekarang");
        registerButton.setWidth("100%");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 100%)");
        registerButton.getStyle().set("border", "none");
        registerButton.getStyle().set("border-radius", "8px");
        registerButton.getStyle().set("height", "36px"); // Much smaller
        registerButton.getStyle().set("font-weight", "600");
        registerButton.getStyle().set("font-size", "14px"); // Further reduced
        registerButton.getStyle().set("cursor", "pointer");
        registerButton.getStyle().set("transition", "all 0.3s ease");

        // Hover effect with coffee theme
        registerButton.getElement().executeJs(
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
        registerButton.addClickListener(event -> handleRegister());

        container.add(registerButton);
    }

    private void handleRegister() {
        String username = usernameField.getValue().trim();
        String email = emailField.getValue().trim();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        // Validation
        if (username.isEmpty()) {
            showErrorNotification("Username tidak boleh kosong");
            usernameField.focus();
            return;
        }

        if (email.isEmpty()) {
            showErrorNotification("Email tidak boleh kosong");
            emailField.focus();
            return;
        }

        if (!isValidEmail(email)) {
            showErrorNotification("Format email tidak valid");
            emailField.focus();
            return;
        }

        if (password.isEmpty()) {
            showErrorNotification("Password tidak boleh kosong");
            passwordField.focus();
            return;
        }

        if (password.length() < 6) {
            showErrorNotification("Password minimal 6 karakter");
            passwordField.focus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorNotification("Konfirmasi password tidak cocok");
            confirmPasswordField.clear();
            confirmPasswordField.focus();
            return;
        }

        // Show loading state
        registerButton.setText("Mendaftarkan...");
        registerButton.setEnabled(false);

        // Simulate registration process
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                try {
                    Thread.sleep(1500); // Simulate processing
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Reset button
                registerButton.setText("Daftar Sekarang");
                registerButton.setEnabled(true);

                // Simple validation (replace with actual registration logic)
                if (isUsernameAvailable(username)) {
                    showSuccessNotification("Akun berhasil dibuat! Silakan login.");
                    clearForm();
                    redirectToLogin();
                } else {
                    showErrorNotification("Username sudah digunakan, silakan pilih yang lain");
                    usernameField.focus();
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isUsernameAvailable(String username) {
        // Simple demo validation - replace with actual database check
        return !("admin".equals(username) || "user".equals(username) || "demo".equals(username));
    }

    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void redirectToLogin() {
        // Redirect to login page after successful registration
        getUI().ifPresent(ui -> ui.navigate("/"));
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