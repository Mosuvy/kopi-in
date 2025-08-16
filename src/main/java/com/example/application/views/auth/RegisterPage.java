package com.example.application.views.auth;

import com.example.application.dao.UserDAO;
import com.example.application.models.Users;
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
import java.sql.Timestamp;

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
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        getStyle().set("min-height", "100vh");

        createRegisterLayout();
    }

    private void createRegisterLayout() {
        VerticalLayout formSection = createFormSection();
        VerticalLayout brandingSection = createBrandingSection();

        HorizontalLayout mainContainer = new HorizontalLayout(formSection, brandingSection);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("border-radius", "20px");
        mainContainer.getStyle().set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.setWidth("900px");
        mainContainer.setHeight("520px");

        add(mainContainer);
    }

    private VerticalLayout createBrandingSection() {
        VerticalLayout brandingSection = new VerticalLayout();
        brandingSection.setWidth("400px");
        brandingSection.setHeight("100%");
        brandingSection.setAlignItems(FlexComponent.Alignment.CENTER);
        brandingSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        brandingSection.getStyle().set("background", "linear-gradient(135deg, #A0522D 0%, #CD853F 50%, #DEB887 100%)");
        brandingSection.getStyle().set("color", "white");
        brandingSection.setPadding(true);

        Image Kopi_in = new Image("/images/logo_kopi-in_dark.png", "Kopi.in Logo");
        Kopi_in.setWidth("180px");
        Kopi_in.getStyle().set("margin-bottom", "20px");

        Paragraph tagline = new Paragraph("Bergabung dengan Kopi.in!");
        tagline.getStyle().set("font-size", "24px");
        tagline.getStyle().set("opacity", "0.9");
        tagline.getStyle().set("text-align", "center");
        tagline.getStyle().set("margin", "0 0 10px 0");
        tagline.getStyle().set("text-shadow", "1px 1px 2px rgba(0, 0, 0, 0.3)");

        VerticalLayout features = new VerticalLayout();
        features.setSpacing(false);
        features.setAlignItems(FlexComponent.Alignment.START);
        features.getStyle().set("gap", "4px");

        String[] featureList = {
                "Dapatkan Promo Eksklusif",
                "Loyalty Points Setiap Pembelian",
                "Pre-order Menu Favorit",
                "Notifikasi Menu & Event Terbaru"
        };

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("60px");
        coffeeIcon.getStyle().set("color", "#8B4513");
        coffeeIcon.getStyle().set("margin-top", "15px");

        for (String feature : featureList) {
            HorizontalLayout featureItem = new HorizontalLayout();
            featureItem.setAlignItems(FlexComponent.Alignment.CENTER);
            featureItem.setSpacing(false);
            featureItem.getStyle().set("gap", "8px");

            Icon checkIcon = new Icon(VaadinIcon.CHECK);
            checkIcon.setSize("14px");
            checkIcon.getStyle().set("color", "#8B4513");

            Paragraph featureText = new Paragraph(feature);
            featureText.getStyle().set("margin", "0");
            featureText.getStyle().set("font-size", "13px");
            featureText.getStyle().set("opacity", "0.95");
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
        formSection.setPadding(false);
        formSection.getStyle().set("padding", "20px");

        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("300px");
        formContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        formContainer.setSpacing(false);

        H2 formTitle = new H2("Buat Akun Baru");
        formTitle.getStyle().set("color", "#3C1810");
        formTitle.getStyle().set("font-weight", "600");
        formTitle.getStyle().set("margin", "0 0 2px 0");
        formTitle.getStyle().set("font-size", "24px");

        Paragraph formSubtitle = new Paragraph("Daftar untuk menikmati pengalaman Kopi.in");
        formSubtitle.getStyle().set("color", "#8B4513");
        formSubtitle.getStyle().set("margin", "0 0 10px 0");
        formSubtitle.getStyle().set("font-size", "12px");

        createFormFields(formContainer);
        createLoginLink(formContainer);
        createRegisterButton(formContainer);

        formSection.add(formTitle, formSubtitle, formContainer);
        return formSection;
    }

    private void createFormFields(VerticalLayout container) {
        Span usernameLabel = new Span("Username");
        usernameLabel.getStyle().set("color", "#3C1810");
        usernameLabel.getStyle().set("font-weight", "500");
        usernameLabel.getStyle().set("font-size", "12px");
        usernameLabel.getStyle().set("margin-bottom", "2px");
        usernameLabel.getStyle().set("display", "block");
        usernameLabel.getStyle().set("text-align", "left");
        usernameLabel.getStyle().set("width", "100%");
        usernameLabel.getStyle().set("align-self", "flex-start");

        usernameField = new TextField();
        usernameField.setWidth("100%");
        usernameField.getStyle().set("margin-bottom", "4px");
        usernameField.getStyle().set("--lumo-border-radius", "8px");
        usernameField.getStyle().set("height", "32px");

        Span emailLabel = new Span("Email");
        emailLabel.getStyle().set("color", "#3C1810");
        emailLabel.getStyle().set("font-weight", "500");
        emailLabel.getStyle().set("font-size", "12px");
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
        confirmPasswordField.getStyle().set("margin-bottom", "8px");
        confirmPasswordField.getStyle().set("--lumo-border-radius", "8px");
        confirmPasswordField.getStyle().set("height", "32px");

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
        loginLayout.getStyle().set("margin-bottom", "6px");
        loginLayout.setSpacing(false);

        Span loginText = new Span("Sudah Punya Akun? ");
        loginText.getStyle().set("color", "#666");
        loginText.getStyle().set("font-size", "12px");
        loginText.getStyle().set("margin-right", "4px");

        Button loginLink = new Button("Login disini");
        loginLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        loginLink.getStyle().set("color", "#007bff");
        loginLink.getStyle().set("text-decoration", "none");
        loginLink.getStyle().set("font-size", "12px");
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

        loginLink.addClickListener(event -> {
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
        registerButton.getStyle().set("height", "36px");
        registerButton.getStyle().set("font-weight", "600");
        registerButton.getStyle().set("font-size", "14px");
        registerButton.getStyle().set("cursor", "pointer");
        registerButton.getStyle().set("transition", "all 0.3s ease");

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

        registerButton.addClickListener(event -> handleRegister());
        container.add(registerButton);
    }

    private void handleRegister() {
        String username = usernameField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Notification.show("Semua field harus diisi!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Notification.show("Password tidak cocok!");
            return;
        }

        UserDAO userDAO = new UserDAO();

        // Validasi password sebelum disimpan
        if (!userDAO.validatePassword(password)) {
            Notification.show("Password harus minimal 8 karakter dan mengandung huruf besar, huruf kecil, angka, dan karakter khusus",
                            5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Password akan di-hash di UserDAO
        newUser.setRole("customer");
        newUser.setIs_active(1);
        newUser.setCreated_at(new Timestamp(System.currentTimeMillis()));

        boolean success = userDAO.createUsers(newUser);

        if (success) {
            Notification.show("Registrasi berhasil!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("login"));
        } else {
            Notification.show("Registrasi gagal!", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
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