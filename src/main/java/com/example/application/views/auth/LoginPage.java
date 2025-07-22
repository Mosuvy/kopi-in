package com.example.application.views.auth;

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

@Route("login")
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

        // Main container
        HorizontalLayout mainContainer = new HorizontalLayout(brandingSection, formSection);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("border-radius", "20px");
        mainContainer.getStyle().set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.setWidth("900px");
        mainContainer.setHeight("600px");

        add(mainContainer);
    }

    private VerticalLayout createBrandingSection() {
        VerticalLayout brandingSection = new VerticalLayout();
        brandingSection.setWidth("400px");
        brandingSection.setHeight("100%");
        brandingSection.setAlignItems(FlexComponent.Alignment.CENTER);
        brandingSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        brandingSection.getStyle().set("background", "linear-gradient(135deg, #3C1810 0%, #5D2914 100%)");
        brandingSection.getStyle().set("color", "white");
        brandingSection.setPadding(true);

        // Logo icon
//        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
//        coffeeIcon.setSize("80px");
//        coffeeIcon.getStyle().set("color", "#D2691E");
//        coffeeIcon.getStyle().set("margin-bottom", "30px");
        Image coffeeIcon = new Image("/images/logo_kopi-in.png", "Kopi.in Logo");
        coffeeIcon.setWidth("120px");
        coffeeIcon.getStyle().set("margin-bottom", "30px");

        // Brand name
        H1 brandName = new H1("Kopi.in");
        brandName.getStyle().set("font-size", "48px");
        brandName.getStyle().set("font-weight", "700");
        brandName.getStyle().set("color", "#FFFAFA");
        brandName.getStyle().set("margin", "0 0 20px 0");
        brandName.getStyle().set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");

        // Tagline
        Paragraph tagline = new Paragraph("Modern Coffee Shop Management");
        tagline.getStyle().set("font-size", "18px");
        tagline.getStyle().set("opacity", "0.8");
        tagline.getStyle().set("text-align", "center");
        tagline.getStyle().set("margin", "0 0 40px 0");

        // Feature highlights
        VerticalLayout features = new VerticalLayout();
        features.setSpacing(true);
        features.setAlignItems(FlexComponent.Alignment.START);

        String[] featureList = {
                "Inventory Management",
                "Sales Analytics",
                "Customer Management",
                "Real-time Reporting"
        };

        for (String feature : featureList) {
            HorizontalLayout featureItem = new HorizontalLayout();
            featureItem.setAlignItems(FlexComponent.Alignment.CENTER);

            Icon checkIcon = new Icon(VaadinIcon.CHECK);
            checkIcon.setSize("16px");
            checkIcon.getStyle().set("color", "#D2691E");

            Paragraph featureText = new Paragraph(feature);
            featureText.getStyle().set("margin", "0");
            featureText.getStyle().set("font-size", "14px");
            featureText.getStyle().set("opacity", "0.9");

            featureItem.add(checkIcon, featureText);
            features.add(featureItem);
        }

        brandingSection.add(coffeeIcon, brandName, tagline, features);
        return brandingSection;
    }

    private VerticalLayout createFormSection() {
        VerticalLayout formSection = new VerticalLayout();
        formSection.setWidth("500px");
        formSection.setHeight("100%");
        formSection.setAlignItems(FlexComponent.Alignment.CENTER);
        formSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        formSection.setPadding(true);

        // Form container
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("350px");
        formContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        // Header
        H2 formTitle = new H2("Welcome Back");
        formTitle.getStyle().set("color", "#3C1810");
        formTitle.getStyle().set("font-weight", "600");
        formTitle.getStyle().set("margin", "0 0 10px 0");
        formTitle.getStyle().set("font-size", "32px");

        Paragraph formSubtitle = new Paragraph("Sign in to your account");
        formSubtitle.getStyle().set("color", "#8B4513");
        formSubtitle.getStyle().set("margin", "0 0 40px 0");
        formSubtitle.getStyle().set("font-size", "16px");

        // Form fields
        createFormFields(formContainer);

        // Login button
        createLoginButton(formContainer);

        formSection.add(formTitle, formSubtitle, formContainer);
        return formSection;
    }

    private void createFormFields(VerticalLayout container) {
        // Username field
        usernameField = new TextField("Username");
        usernameField.setWidth("100%");
        usernameField.getStyle().set("margin-bottom", "20px");
        usernameField.getStyle().set("--lumo-border-radius", "8px");
        usernameField.getStyle().set("height", "50px");

        // Password field
        passwordField = new PasswordField("Password");
        passwordField.setWidth("100%");
        passwordField.getStyle().set("margin-bottom", "30px");
        passwordField.getStyle().set("--lumo-border-radius", "8px");
        passwordField.getStyle().set("height", "50px");

        // Focus styling with coffee theme
        usernameField.getElement().executeJs(
                "this.addEventListener('focus', () => this.style.boxShadow = '0 0 0 2px #D2691E33');" +
                        "this.addEventListener('blur', () => this.style.boxShadow = 'none');"
        );

        passwordField.getElement().executeJs(
                "this.addEventListener('focus', () => this.style.boxShadow = '0 0 0 2px #D2691E33');" +
                        "this.addEventListener('blur', () => this.style.boxShadow = 'none');"
        );

        container.add(usernameField, passwordField);
    }

    private void createLoginButton(VerticalLayout container) {
        loginButton = new Button("Sign In");
        loginButton.setWidth("100%");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 100%)");
        loginButton.getStyle().set("border", "none");
        loginButton.getStyle().set("border-radius", "8px");
        loginButton.getStyle().set("height", "50px");
        loginButton.getStyle().set("font-weight", "600");
        loginButton.getStyle().set("font-size", "16px");
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
        loginButton.setText("Signing in...");
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
                loginButton.setText("Sign In");
                loginButton.setEnabled(true);

                // Simple validation (replace with actual authentication)
                if (validateCredentials(username, password)) {
                    showSuccessNotification("Welcome back to Kopi.in!");
                    redirectToDashboard();
                } else {
                    showErrorNotification("Invalid username or password");
                    passwordField.clear();
                    passwordField.focus();
                }
            });
        });
    }

    private boolean validateCredentials(String username, String password) {
        // Simple demo validation - replace with actual authentication logic
        return ("admin".equals(username) && "admin123".equals(password)) ||
                ("user".equals(username) && "user123".equals(password)) ||
                ("demo".equals(username) && "demo123".equals(password));
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