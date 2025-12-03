package clinica.backend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Service // 1. Marcamos como Servicio de Spring
public class EmailService {

    private static final String APPLICATION_NAME = "Clinica Dental";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // 2. Inyectamos las claves de forma segura desde application.properties
    @Value("${gmail.client.id}")
    private String CLIENT_ID;
    
    @Value("${gmail.client.secret}")
    private String CLIENT_SECRET;
    
    @Value("${gmail.client.refreshtoken}")
    private String REFRESH_TOKEN;
    
    @Value("${gmail.from.email}")
    private String EMAIL_FROM;
    
    private Gmail gmailService; // Cache para el servicio

    private Gmail getGmailService() throws IOException, GeneralSecurityException {
        if (this.gmailService != null) {
            return this.gmailService;
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build();
        
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.refreshToken();

        this.gmailService = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return this.gmailService;
    }

    /**
     * Este es el método que tus otros servicios llamarán.
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            Gmail service = getGmailService();
            MimeMessage mimeMessage = createHtmlEmail(to, EMAIL_FROM, subject, htmlContent);
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mimeMessage.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send("me", message).execute();
            
            // 3. Usamos un Logger en lugar de System.out
            logger.info("Correo HTML enviado a " + to + " con Asunto: " + subject);

        } catch (Exception e) {
            logger.error("Error al enviar email con Google API OAuth: " + e.getMessage(), e);
            this.gmailService = null; // Resetea el servicio si falla
        }
    }

    private MimeMessage createHtmlEmail(String to, String from, String subject, String htmlContent) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from, "Clínica Sonrisa Plena"));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, "UTF-8");
        email.setContent(htmlContent, "text/html; charset=utf-8");
        
        return email;
    }
}