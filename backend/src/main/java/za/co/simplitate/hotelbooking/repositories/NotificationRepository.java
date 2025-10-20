package za.co.simplitate.hotelbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.simplitate.hotelbooking.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
