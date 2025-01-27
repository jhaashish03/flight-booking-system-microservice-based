package com.ewallet.userservice.controllers;

import com.ewallet.userservice.dtos.AccountCreationDto;
import com.ewallet.userservice.entities.Account;
import com.ewallet.userservice.exceptions.UserNotFoundException;
import com.ewallet.userservice.mapper.AccountMapper;
import com.ewallet.userservice.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/"+AccountController.VERSION+"/accounts")
public class AccountController {

    protected static final String VERSION="v1";


    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final LoadBalancerClient loadBalancerClient;
    public AccountController(AccountService accountService, AccountMapper accountMapper, LoadBalancerClient loadBalancerClient) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.loadBalancerClient = loadBalancerClient;
    }

    @GetMapping("/reg-pass")
    public String regPass(Model model) {
        model.addAttribute("accountCreationDto", new AccountCreationDto());
        return "registration";
    }

    @PostMapping("/register")
    public String registerPassenger(@ModelAttribute @Valid AccountCreationDto accountCreationDto, Model model) {

       accountService.registerNewAccount(accountCreationDto);

        model.addAttribute("account",  Account.builder().userName(accountCreationDto.getUserName()).build());
       return "verifyEmail";
    }
    @PostMapping("/verifyOtp/{userName}")
    public String verifyOtp(@PathVariable  String userName,String otp,Model model) {

        model.addAttribute("account", Account.builder().userName(userName).build());
      return   accountService.verifyOtp(userName,otp)? "redirect:account/login" :"emailVerificationFailed";
    }


    @PostMapping("/resend-otp/{userName}")
    public String resendOtp(@PathVariable String userName,Model model) throws UserNotFoundException {
        model.addAttribute("account", Account.builder().userName(userName).build());
      accountService.resendOtp(userName);
      return "verifyEmail";
    }

    @PostMapping("/verifyEmail/{userName}")
    public String verifyEmail(@PathVariable String userName, Model model) throws UserNotFoundException {

        model.addAttribute("account", Account.builder().userName(userName).build());
        accountService.resendOtp(userName);
        return "verifyEmail";
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        return "customLogin";
    }

    @GetMapping("/home")
    @ResponseBody
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome");
    }
    
    @GetMapping("/login/success")
    public String loginSuccess(Model model,@AuthenticationPrincipal Account account) {
        return account.getAuthorities().stream().findFirst().get().getAuthority().equals("ADMIN") ? "redirect:/v1/admins/" : "redirect:/v1/users/";
    }
    
    
    
    
    

    
    
    
    


}
