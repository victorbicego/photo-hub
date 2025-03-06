package com.event_manager.photo_hub.services.impl;

import com.event_manager.photo_hub.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private static final String COMPANY_NAME = "COMPANY X";
  private static final String SUBJECT = "subject";
  private static final String TITLE = "title";
  private static final String GREETING = "greeting";
  private static final String CODE = "code";

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Override
  public void sendResetPasswordRequestEmail(String username, String code)
      throws MessagingException {
    String subject = COMPANY_NAME + " - Redefinir Senha";
    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "Redefinir Sua Senha");
    context.setVariable(GREETING, "Olá,");
    context.setVariable(CODE, code);

    // Processa diretamente o template reset_password.html
    String content = templateEngine.process("reset_password", context);

    sendEmail(username, subject, content);
    log.info("Password reset email sent to: {}.", username);
  }

  @Override
  public void sendConfirmationCode(String to, String code) throws MessagingException {
    String subject = COMPANY_NAME + " - Email Confirmation Code";
    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "Bem-vindo ao " + COMPANY_NAME + "!");
    context.setVariable(GREETING, "Olá,");
    context.setVariable(CODE, code);

    // Processa diretamente o template confirmation_code.html
    String content = templateEngine.process("confirmation_code", context);

    sendEmail(to, subject, content);
    log.info("Confirmation code email sent to: {}.", to);
  }

  @Override
  public void resendConfirmationCode(String to, String code) throws MessagingException {
    String subject = COMPANY_NAME + " - Novo Código de Confirmação";
    Context context = new Context();
    context.setVariable(SUBJECT, subject);
    context.setVariable(TITLE, "Novo Código de Confirmação");
    context.setVariable(GREETING, "Olá,");
    context.setVariable(CODE, code);

    // Processa diretamente o template resend_confirmation_code.html
    String content = templateEngine.process("resend_confirmation_code", context);

    sendEmail(to, subject, content);
    log.info("Resent confirmation code email to: {}.", to);
  }

  private void sendEmail(String email, String subject, String content) throws MessagingException {
    System.out.println("-------------------");
    System.out.println(email);
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(email);
    helper.setSubject(subject);
    helper.setText(content, true);
    mailSender.send(message);
    log.info("Email sent successfully to: {}.", email);
  }
}
