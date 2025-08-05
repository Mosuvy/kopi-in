package com.example.application.views.customer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@PageTitle("Profile")
@Route(value = "profile")
public class ProfilPage extends VerticalLayout {

    private TextField nameField;
    private DatePicker birthDateField;
    private TextArea addressField;
    private Div profilePlaceholder;
    private Image profileImage;
    private boolean photoUploaded = false;

    public ProfilPage() {
        setSizeFull();
        setHeight("100vh");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        getStyle()
                .set("background-color", "#4E342E")
                .set("min-height", "100vh")
                .set("width", "100vw")
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0");

        // Card container
        Div card = new Div();
        card.getStyle()
                .set("background-color", "white")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)")
                .set("border-radius", "12px")
                .set("padding", "15px")
                .set("max-width", "320px")
                .set("width", "100%")
                .set("text-align", "center");

        H2 title = new H2("Profil Saya");
        title.getStyle().set("margin-bottom", "20px");

        // Placeholder lingkaran abu-abu
        profilePlaceholder = new Div();
        profilePlaceholder.setWidth("100px");
        profilePlaceholder.setHeight("100px");
        profilePlaceholder.getStyle()
                .set("border-radius", "50%")
                .set("background-color", "#e0e0e0")
                .set("margin", "0 auto 10px auto");

        // Foto profil
        profileImage = new Image();
        profileImage.setWidth("100px");
        profileImage.setHeight("100px");
        profileImage.getStyle()
                .set("border-radius", "50%")
                .set("object-fit", "cover")
                .set("margin-bottom", "10px");
        profileImage.setVisible(false);

        // Upload foto
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setAutoUpload(true);
        upload.getStyle().set("margin-bottom", "20px");

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                profileImage.setSrc(new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(bytes)));
                profileImage.setVisible(true);
                profilePlaceholder.setVisible(false);
                photoUploaded = true;
                Notification.show("Foto profil berhasil diunggah!");
                upload.clearFileList();
                upload.getElement().executeJs(
                        "this.shadowRoot.querySelector('vaadin-upload-file-list').hidden = true;"
                );
            } catch (IOException e) {
                Notification.show("Gagal mengunggah foto: " + e.getMessage());
            }
        });

        // Field input
        nameField = new TextField("Nama");
        nameField.setPlaceholder("Masukkan nama Anda");

        birthDateField = new DatePicker("Tanggal Lahir");
        birthDateField.setPlaceholder("Pilih tanggal lahir");

        addressField = new TextArea("Alamat");
        addressField.setMaxLength(255);
        addressField.setHeight("80px");
        addressField.setPlaceholder("Masukkan alamat lengkap");

        // Tombol simpan
        Button saveButton = new Button("Simpan", event -> {
            if (nameField.isEmpty() || birthDateField.isEmpty() || addressField.isEmpty()) {
                Notification.show("Mohon lengkapi semua field yang diperlukan!", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            if (!photoUploaded) {
                Notification.show("Mohon upload foto profil terlebih dahulu!", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            Notification.show("Profil berhasil disimpan!", 3000, Notification.Position.TOP_CENTER);
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-top", "20px");

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, birthDateField, addressField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        formLayout.setWidthFull();

        // Tambahkan semua ke card
        card.add(title, profilePlaceholder, profileImage, upload, formLayout, saveButton);

        // Tambahkan card ke layout utama
        add(card);
    }
}