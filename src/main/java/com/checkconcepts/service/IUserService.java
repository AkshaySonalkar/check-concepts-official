package com.checkconcepts.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import com.checkconcepts.web.dto.UserDto;
import com.checkconcepts.persistence.model.PasswordResetToken;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.persistence.model.VerificationToken;
import com.checkconcepts.persistence.model.NewLocationToken;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    List<String> getUsersFromSessionRegistry();

    NewLocationToken isNewLoginLocation(String username, String ip);

    String isValidNewLocationToken(String token);

    void addUserLocation(User user, String ip);
}
