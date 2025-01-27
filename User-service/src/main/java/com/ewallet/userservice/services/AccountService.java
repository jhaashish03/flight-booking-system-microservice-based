package com.ewallet.userservice.services;

import com.ewallet.userservice.dtos.AccountCreationDto;
import com.ewallet.userservice.dtos.EmailRequest;
import com.ewallet.userservice.entities.Account;
import com.ewallet.userservice.entities.GrantedAuthorityImpl;
import com.ewallet.userservice.entities.Role;
import com.ewallet.userservice.exceptions.UserAlreadyExitsException;
import com.ewallet.userservice.exceptions.UserNotFoundException;
import com.ewallet.userservice.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;



    public AccountService(AccountRepository accountRepository, StringRedisTemplate stringRedisTemplate, MailService mailService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailService = mailService;

        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerNewAccount(AccountCreationDto accountCreationDto) {
        try{
           Account account= Account.builder()
                            .firstName(accountCreationDto.getFirstName())
                                    .lastName(accountCreationDto.getLastName())
                                            .authority(Set.of(new GrantedAuthorityImpl(Role.USER.name())))
                                                    .email(accountCreationDto.getEmail())
                                                            .password(passwordEncoder.encode(accountCreationDto.getPassword()))
                                                                    .userName(accountCreationDto.getUserName()).build();
            accountRepository.saveAndFlush(account);
            sendOtp(account.getEmail(),account.getUsername());
        } catch (DataIntegrityViolationException e){
            throw new UserAlreadyExitsException("User already exists");
        }

    }

    public boolean verifyOtp(String userName, String otp) {
        return Objects.equals((String) stringRedisTemplate.opsForValue().get(userName + "Otp"), otp);
    }

    @Transactional
    public void resendOtp(String userName) throws UserNotFoundException {
        Account account=accountRepository.findByUserName(userName);
        if(account!=null){
            if(stringRedisTemplate.opsForValue().get(userName + "Otp")!=null){
                stringRedisTemplate.delete(userName + "Otp");
            }

            sendOtp(account.getEmail(),account.getUsername());
        } else {
            throw new UserNotFoundException("User not found");
        }

    }
    @Async
    public void sendOtp(String email,String userName){
        String otp=RandomStringUtils.randomNumeric(6);
        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .subject("VERIFY EMAIL")
                .body("Hi user, \n\n YOUR OTP is "+ otp).build();
        mailService.triggerMail(emailRequest);
        stringRedisTemplate.opsForValue().set(userName+"Otp", otp, Duration.ofMinutes(3));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUserName(username);
    }
    @Transactional
    public void createAdmin(AccountCreationDto accountCreationDto) {
        try{
            String password = RandomStringUtils.random(6, true, true);
            Account account= Account.builder()
                    .firstName(accountCreationDto.getFirstName())
                    .lastName(accountCreationDto.getLastName())
                    .authority(Set.of(new GrantedAuthorityImpl(Role.ADMIN.name())))
                    .email(accountCreationDto.getEmail())
                    .password(passwordEncoder.encode(password))
                    .userName(accountCreationDto.getUserName()).build();
            accountRepository.saveAndFlush(account);
            EmailRequest emailRequest = EmailRequest.builder()
                    .to(account.getEmail())
                    .subject("ADMIN Account Created")
                    .body("Hi , \n\n Your new admin account is created. Find the credentials below to login. \n\\n" +
                            "Username: "+accountCreationDto.getUserName()+"\n\n+ Password: "+password+"\n\n Thanks,\n Ashish Jha").build();
            mailService.triggerMail(emailRequest);
        } catch (DataIntegrityViolationException e){
            throw new UserAlreadyExitsException("Admin already exists");
        }
    }
}
