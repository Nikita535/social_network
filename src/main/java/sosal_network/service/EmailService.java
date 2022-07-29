package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * Class EmailService - класс для основных операций над почтой
 **/
@Service
public class EmailService {

    /**
     * Bean класса mailSender
     **/
    @Autowired
    private JavaMailSender mailSender;


    /**
     * Метод sendSimpleMessage для отправки сообщения
     * param To - почта на которую отправляется сообщение
     * param text - текст самого сообщения
     * author - Nekit,Nikita
     **/
    @Async
    public void sendSimpleMessage(String To, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(To);
        helper.setText(text, true);
        helper.setSubject("Сообщение от команды разработчиков");
        mailSender.send(mimeMessage);

    }
}
