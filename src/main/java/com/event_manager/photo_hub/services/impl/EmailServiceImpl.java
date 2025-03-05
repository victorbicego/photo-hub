package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private static final String COMPANY_NAME = "COMPANY X";
  private static final String SUBJECT = "subject";
  private static final String TITLE = "title";
  private static final String GREETING = "greeting";
  private static final String CODE = "code";
  private static final String BODY_TEMPLATE = "body";
  private static final String TEMPLATE_PATH = "emails/layout";

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Override
  public void sendResetPasswordRequestEmail(String username, String code)
      throws MessagingException {
    String subject = COMPANY_NAME + " - Redefinir Senha";
    String bodyTemplate = "emails/reset_password :: body";

    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "Redefinir Sua Senha");
    context.setVariable(GREETING, "Hello,");
    context.setVariable(CODE, code);
    context.setVariable(BODY_TEMPLATE, bodyTemplate);

    String content = templateEngine.process(TEMPLATE_PATH, context);

    sendEmail(username, subject, content);
    log.info("Password reset email sent to: {}.", username);
  }

  @Override
  public void sendConfirmationCode(String to, String code) throws MessagingException {
    String subject = COMPANY_NAME + " - Email Confirmation Code";
    String bodyTemplate = "emails/confirmation_code :: body";

    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "Welcome to " + COMPANY_NAME + "!");
    context.setVariable(GREETING, "Hello,");
    context.setVariable(CODE, code);
    context.setVariable(BODY_TEMPLATE, bodyTemplate);

    String content = templateEngine.process(TEMPLATE_PATH, context);

    sendEmail(to, subject, content);
    log.info("Confirmation code email sent to: {}.", to);
  }

  @Override
  public void resendConfirmationCode(String to, String code) throws MessagingException {
    String subject = COMPANY_NAME + " - New Confirmation Code";
    String bodyTemplate = "emails/resend_confirmation_code :: body";

    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "New Confirmation Code");
    context.setVariable(GREETING, "Hello,");
    context.setVariable(CODE, code);
    context.setVariable(BODY_TEMPLATE, bodyTemplate);

    String content = templateEngine.process(TEMPLATE_PATH, context);

    sendEmail(to, subject, content);
    log.info("Resent confirmation code email to: {}.", to);
  }
}
