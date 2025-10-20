package za.co.simplitate.hotelbooking.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.dtos.NotificationTO;
import za.co.simplitate.hotelbooking.entities.Notification;
import za.co.simplitate.hotelbooking.enums.NotificationType;
import za.co.simplitate.hotelbooking.repositories.NotificationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;

    private final NotificationRepository notificationRepository;


    /**
     * Sends an email notification asynchronously using the provided notification details.
     *
     * @param notificationTO
     */
    @Override
    @Async
    public void sendEmail(NotificationTO notificationTO) {
        log.info("sendEmail: {}", notificationTO);
        SimpleMailMessage simpleMailMessage = createSimpleEmailMessage(notificationTO);
        javaMailSender.send(simpleMailMessage);
        persistInDatabase(notificationTO);
    }

    private static SimpleMailMessage createSimpleEmailMessage(NotificationTO notificationTO) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(notificationTO.recipient());
        simpleMailMessage.setSubject(notificationTO.subject());
        simpleMailMessage.setText(notificationTO.body());
        return simpleMailMessage;
    }

    private void persistInDatabase(NotificationTO notificationTO) {
        Notification notificationEntity = Notification.builder()
                .recipient(notificationTO.recipient())
                .subject(notificationTO.subject())
                .body(notificationTO.body())
                .bookingReference(notificationTO.bookingReference())
                .notificationType(NotificationType.EMAIL)
                .build();
        notificationRepository.save(notificationEntity);
    }

    @Override
    public void sendSms(NotificationTO notificationTO) {
        //TODO: implement later
    }

    @Override
    public void sendWhatsApp(NotificationTO notificationTO) {
        //TODO: implement later
    }
}
