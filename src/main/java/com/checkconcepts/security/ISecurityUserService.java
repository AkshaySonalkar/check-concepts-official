package com.checkconcepts.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
