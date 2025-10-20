package za.co.simplitate.hotelbooking.services;

import za.co.simplitate.hotelbooking.dtos.LoginRequest;
import za.co.simplitate.hotelbooking.dtos.RegistrationRequest;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.dtos.UserTO;
import za.co.simplitate.hotelbooking.entities.User;

public interface UserService {

    Response registerUser(RegistrationRequest registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    User getCurrentLoggedInUser();
    Response updateOwnAccount(UserTO userTO);
    Response getBookingHistory();
    Response deleteOwnAccount();
}
