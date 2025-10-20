package za.co.simplitate.hotelbooking.notifications;

import za.co.simplitate.hotelbooking.dtos.NotificationTO;


public interface NotificationService {
    void sendEmail(NotificationTO notificationTO);
    void sendSms(NotificationTO notificationTO);
    void sendWhatsApp(NotificationTO notificationTO);
}
