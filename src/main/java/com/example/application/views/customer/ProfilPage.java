package com.example.application.views.customer;

import com.example.application.dao.UserDAO;
import com.example.application.models.Customer;
import com.example.application.models.Users;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@PageTitle("Profile - kopi-In")
@Route(value = "profile", layout = AppLayoutNavbar.class)
public class ProfilPage extends VerticalLayout {

    private final UserDAO userDAO;

    private TextField nameField;
    private TextField phoneNumField;
    private DatePicker birthDateField;
    private TextArea addressField;
    private Div profilePlaceholder;
    private Image profileImage;
    private byte[] uploadedImageBytes;
    private String uploadedFileName;
    private String customerId;
    private boolean isEditMode = false;

    public ProfilPage(UserDAO userDAO) {
        this.userDAO = userDAO;
        initializeUI();
        loadCustomerData();
    }

    private void initializeUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%)")
                .set("min-height", "calc(100vh - var(--app-layout-navbar-height))");

        // Main card container
        Div card = new Div();
        card.getStyle()
                .set("background-color", "white")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border-radius", "16px")
                .set("padding", "2rem")
                .set("max-width", "500px")
                .set("width", "90%")
                .set("margin", "2rem auto")
                .set("position", "relative");

        // [Bagian dekorasi dan header tetap sama...]

        // Profile picture section
        Div profileContainer = new Div();
        profileContainer.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("margin-bottom", "1.5rem");

        profilePlaceholder = new Div();
        profilePlaceholder.setWidth("150px");
        profilePlaceholder.setHeight("150px");
        profilePlaceholder.getStyle()
                .set("border-radius", "50%")
                .set("background", "linear-gradient(145deg, #ffffff, #e6e6e6)")
                .set("box-shadow", "5px 5px 15px #d9d9d9, -5px -5px 15px #ffffff")
                .set("margin", "0 auto 1rem auto")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("border", "3px solid white");

        profileImage = new Image();
        profileImage.setWidth("100%");
        profileImage.setHeight("100%");
        profileImage.getStyle()
                .set("object-fit", "cover")
                .set("position", "absolute")
                .set("cursor", "pointer")
                .set("left", "0")
                .set("top", "0");

        profilePlaceholder.add(profileImage);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/webp");
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setAutoUpload(true);

        upload.setUploadButton(new Button("Ubah Foto Profil", VaadinIcon.UPLOAD.create()));
        upload.getUploadButton().getStyle()
                .set("background", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("border-radius", "20px")
                .set("padding", "0.5rem 1rem");

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                uploadedImageBytes = inputStream.readAllBytes();
                uploadedFileName = event.getFileName();

                profileImage.setSrc(new StreamResource(uploadedFileName,
                        () -> new ByteArrayInputStream(uploadedImageBytes)));
                profileImage.setVisible(true);

                Notification.show("Foto profil berhasil diunggah!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                upload.clearFileList();
            } catch (IOException e) {
                Notification.show("Gagal mengunggah foto: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        profileContainer.add(profilePlaceholder, upload);

        // Form fields
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setWidthFull();

        nameField = new TextField("Nama Lengkap");
        nameField.setPlaceholder("Masukkan nama lengkap");
        nameField.setPrefixComponent(VaadinIcon.USER.create());
        nameField.setRequired(true);

        phoneNumField = new TextField("Nomor Telepon");
        phoneNumField.setPlaceholder("Masukkan nomor telepon");
        phoneNumField.setPrefixComponent(VaadinIcon.PHONE.create());

        birthDateField = new DatePicker("Tanggal Lahir");
        birthDateField.setPlaceholder("Pilih tanggal lahir");
        birthDateField.setPrefixComponent(VaadinIcon.CALENDAR.create());
        birthDateField.setRequired(true);

        addressField = new TextArea("Alamat");
        addressField.setMaxLength(255);
        addressField.setHeight("100px");
        addressField.setPlaceholder("Masukkan alamat lengkap");
        addressField.setPrefixComponent(VaadinIcon.HOME.create());
        addressField.setRequired(true);
        addressField.getStyle().set("grid-column", "span 2");

        formLayout.add(nameField, phoneNumField, addressField, birthDateField);

        // Save button
        Button saveButton = new Button("Simpan Perubahan", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("margin-top", "1.5rem")
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("padding", "1rem");

        saveButton.addClickListener(event -> saveCustomer());

        card.add(profileContainer, formLayout, saveButton);
        add(card);
    }

    private void loadCustomerData() {
        Users user = (Users) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("Anda harus login terlebih dahulu", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("login");
            return;
        }

        Customer customer = userDAO.getCustomerByUserId(user.getId());
        if (customer != null) {
            isEditMode = true;
            customerId = customer.getId();

            nameField.setValue(customer.getFull_name());
            phoneNumField.setValue(customer.getPhone_number() != null ? customer.getPhone_number() : "");
            addressField.setValue(customer.getAddress());

            if (customer.getBirth_date() != null) {
                LocalDate birthDate = customer.getBirth_date().toLocalDate();
                birthDateField.setValue(birthDate);
            }

            if (customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
                try {
                    // Path baru untuk membaca gambar
                    Path imagePath = Paths.get("src", "main", "resources", "META-INF", "resources", "images", "customer", customer.getAvatar());
                    if (Files.exists(imagePath)) {
                        byte[] imageBytes = Files.readAllBytes(imagePath);
                        profileImage.setSrc(new StreamResource(customer.getAvatar(),
                                () -> new ByteArrayInputStream(imageBytes)));
                        profileImage.setVisible(true);
                    } else {
                        // Fallback untuk file yang sudah ada di classpath
                        InputStream is = getClass().getResourceAsStream("/META-INF/resources/images/customer/" + customer.getAvatar());
                        if (is != null) {
                            profileImage.setSrc(new StreamResource(customer.getAvatar(),
                                    () -> is));
                            profileImage.setVisible(true);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            isEditMode = false;
        }
    }

    private void saveCustomer() {
        if (nameField.isEmpty() || birthDateField.isEmpty() || addressField.isEmpty()) {
            Notification.show("Harap lengkapi semua field yang diperlukan", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!isEditMode && uploadedImageBytes == null) {
            Notification.show("Harap unggah foto profil terlebih dahulu", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Users user = (Users) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("Sesi tidak valid, silakan login kembali", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("login");
            return;
        }

        String storagePath;
        if (new File("src/main/resources").exists()) {
            storagePath = "src/main/resources/META-INF/resources/images/customer";
        } else {
            storagePath = System.getProperty("user.home") + "/.yourapp/images/customer";
        }
        Path uploadDir = Paths.get(storagePath);

        try {
            Customer customer = new Customer();
            if (isEditMode) {
                customer.setId(customerId);
            } else {
                customer.setId(UUID.randomUUID().toString());
            }

            customer.setUser_id(user.getId());
            customer.setFull_name(nameField.getValue());
            customer.setPhone_number(phoneNumField.getValue());
            customer.setAddress(addressField.getValue());
            customer.setBirth_date(java.sql.Date.valueOf(birthDateField.getValue()));

            // Handle avatar
            if (uploadedImageBytes != null) {
                String avatarFileName = UUID.randomUUID().toString() +
                        uploadedFileName.substring(uploadedFileName.lastIndexOf("."));

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // Save file
                Path filePath = uploadDir.resolve(avatarFileName);
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                    fos.write(uploadedImageBytes);
                }

                customer.setAvatar(avatarFileName);
            } else if (isEditMode) {
                // Keep existing avatar if not changed
                Customer existingCustomer = userDAO.getCustomerByUserId(user.getId());
                if (existingCustomer != null) {
                    customer.setAvatar(existingCustomer.getAvatar());
                }
            }

            boolean success;
            if (isEditMode) {
                success = userDAO.updateCustomer(customer);
            } else {
                success = userDAO.createCustomer(customer);
            }

            if (success) {
                Notification.show("Profil berhasil disimpan!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                if (!isEditMode) {
                    isEditMode = true;
                    customerId = customer.getId();
                }
            } else {
                Notification.show("Gagal menyimpan profil", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Terjadi kesalahan: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}