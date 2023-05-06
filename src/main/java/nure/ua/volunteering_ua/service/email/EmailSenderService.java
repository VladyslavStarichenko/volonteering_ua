package nure.ua.volunteering_ua.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailSenderService {

  private final JavaMailSender mailSender;

  @Autowired
  public EmailSenderService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendMail(
          String toEmail,
          String subject,
          String body
                       ){
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("goldsilver1234512345@gmail.com");
    message.setTo(toEmail);
    message.setText(body);
    message.setSubject(subject);

    mailSender.send(message);

    log.info("Mail was sent successfully");
  }
}
