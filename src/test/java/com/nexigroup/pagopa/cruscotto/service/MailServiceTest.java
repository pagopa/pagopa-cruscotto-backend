package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.reflect.Field;
import java.util.Locale;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MessageSource messageSource;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private MailService mailService;

    @Mock
    private MimeMessage mimeMessage;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        mailService = new MailService(javaMailSender, messageSource, templateEngine);

        // Set private fields via reflection
        Field baseUrlField = MailService.class.getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(mailService, "http://localhost");

        Field fromField = MailService.class.getDeclaredField("from");
        fromField.setAccessible(true);
        fromField.set(mailService, "noreply@example.com");

        lenient().when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }


    @Test
    void testSendEmail_success() throws Exception {
        mailService.sendEmail("test@example.com", "Subject", "Hello", false, true);

        verify(javaMailSender, times(1)).send(mimeMessageCaptor.capture());
        MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        // Can't inspect MimeMessage easily, just verify send called
    }

    @Test
    void testSendEmail_mailException() throws Exception {
        doThrow(new MailException("Error") {}).when(javaMailSender).send(any(MimeMessage.class));

        mailService.sendEmail("test@example.com", "Subject", "Hello", false, true);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmailFromTemplate_success() {
        AuthUser user = new AuthUser();
        user.setEmail("user@example.com");
        user.setLangKey("en");
        user.setLogin("user1");

        when(templateEngine.process(eq("template"), any(Context.class))).thenReturn("HTML_CONTENT");
        when(messageSource.getMessage(eq("title.key"), eq(null), any(Locale.class))).thenReturn("Title");

        mailService.sendEmailFromTemplate(user, "template", "title.key");

        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmailFromTemplate_noEmail() {
        AuthUser user = new AuthUser();
        user.setEmail(null);
        user.setLogin("user1");

        mailService.sendEmailFromTemplate(user, "template", "title.key");

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendPasswordResetMail() {
        AuthUser user = new AuthUser();
        user.setEmail("reset@example.com");
        user.setLangKey("en");
        user.setLogin("user1");

        when(templateEngine.process(eq("mail/passwordResetEmail"), any(Context.class))).thenReturn("HTML_CONTENT");
        when(messageSource.getMessage(eq("email.reset.title"), eq(null), any(Locale.class))).thenReturn("Reset Password");

        mailService.sendPasswordResetMail(user);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}
