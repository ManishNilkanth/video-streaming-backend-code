package com.Modification2.modification2.service;

import com.Modification2.modification2.Dto.UserInfoDTO;
import com.Modification2.modification2.Repository.UserRepository;
import com.Modification2.modification2.module.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserRepository userRepository;
    
    @Value("auth0.userinfoEndpoint")
    private String userinfoEndpoint;
    public String registerUser(String tokenValue)
    { 
        //cal to the userinfo Endpoint
         
       HttpRequest httpRequest= HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userinfoEndpoint))
                .setHeader("Authorization",String.format("Bearer %s",tokenValue))
                .build();

        HttpClient httpClient= HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        
        try{
            HttpResponse<String> responseString = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            String body = responseString.body();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            UserInfoDTO userInfoDTO = objectMapper.readValue(body, UserInfoDTO.class);

            Optional<User> userBySubject = userRepository.findBySub(userInfoDTO.getSub());
            if(userBySubject.isPresent())
            {
                return userBySubject.get().getId();
            }else{
                User user=new User();
                user.setFullName(userInfoDTO.getName());
                user.setLastName(userInfoDTO.getFamilyName());
                user.setEmailAddress(userInfoDTO.getEmail());
                user.setName(userInfoDTO.getGivenName());

               return userRepository.save(user).getId();
            }
        }catch (Exception exception)
        {
            throw new RuntimeException("Exception occurred while registering user",exception);
        }
    }
}
