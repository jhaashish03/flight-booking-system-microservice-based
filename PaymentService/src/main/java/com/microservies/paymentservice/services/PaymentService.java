package com.microservies.paymentservice.services;

import com.microservies.paymentservice.dtos.PaymentLinkResponseDto;
import com.microservies.paymentservice.dtos.PaymentRequestDto;
import com.microservies.paymentservice.dtos.PaymentStatus;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

@Service
public class PaymentService {

    private final Environment environment;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> paymentRequestDtoRedisTemplate;
    private final HashMap<String,PaymentStatus> stringPaymentStatusHashMap=new HashMap<>();
    public PaymentService(Environment environment, StringRedisTemplate stringRedisTemplate, RedisTemplate<String, Object> paymentRequestDtoRedisTemplate) {
        this.environment = environment;
        this.stringRedisTemplate = stringRedisTemplate;
        this.paymentRequestDtoRedisTemplate = paymentRequestDtoRedisTemplate;
        stringPaymentStatusHashMap.put("paid",PaymentStatus.PAID);
        stringPaymentStatusHashMap.put("cancelled",PaymentStatus.CANCELLED);
        stringPaymentStatusHashMap.put("expired",PaymentStatus.EXPIRED);
        stringPaymentStatusHashMap.put("created",PaymentStatus.CREATED);
    }


    public PaymentLinkResponseDto createPaymentLink(PaymentRequestDto  paymentRequestDto)  {


        RazorpayClient razorpayClient= null;
        try {
            razorpayClient = new RazorpayClient(environment.getProperty("razorpay.merchant-id"),environment.getProperty("razorpay.merchant-secret"));
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount",(paymentRequestDto.getAmount().intValue())*100+(paymentRequestDto.getAmount()-paymentRequestDto.getAmount().intValue())*100);
        paymentLinkRequest.put("currency","INR");
        paymentLinkRequest.put("accept_partial",false);
//        paymentLinkRequest.put("first_min_partial_amount",100);
        long currentTimestamp = Instant.now().getEpochSecond();

        // Add 20 minutes to the current time
        Instant newTimestamp = Instant.ofEpochSecond(currentTimestamp).plus(Duration.ofMinutes(20));

        paymentLinkRequest.put("expire_by",newTimestamp.getEpochSecond());
        paymentLinkRequest.put("reference_id",paymentRequestDto.getReferenceId());
        paymentLinkRequest.put("description","Adding amount to wallet");
        JSONObject customer = new JSONObject();
        customer.put("name",paymentRequestDto.getName());
        customer.put("contact", paymentRequestDto.getContactNumber());
        customer.put("email",paymentRequestDto.getEmail());
        paymentLinkRequest.put("customer",customer);
        JSONObject notify = new JSONObject();
        notify.put("sms",false);
        notify.put("email",false);
        paymentLinkRequest.put("notify",notify);
        paymentLinkRequest.put("reminder_enable",true);
        JSONObject notes = new JSONObject();
        notes.put("policy_name","E-wallet");
        paymentLinkRequest.put("notes",notes);
        paymentLinkRequest.put("callback_url","http://localhost:8082/payments/payment-status-capture");
        paymentLinkRequest.put("callback_method","get");

        PaymentLink payment = null;
        try {
            payment = razorpayClient.paymentLink.create(paymentLinkRequest);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        paymentRequestDtoRedisTemplate.opsForValue().set(payment.get("id"),paymentRequestDto,Duration.ofMinutes(5));
        stringRedisTemplate.opsForValue().set(paymentRequestDto.getReferenceId()+"PaymentService",payment.get("id"),Duration.ofMinutes(5));

      return   PaymentLinkResponseDto.builder().razorpayReferenceId(payment.get("id"))
                        .paymentLink(payment.get("short_url")).build();


    }
    @Transactional
    public String verifyPayment( String razorpay_payment_id,  String razorpay_payment_link_id,  String razorpay_payment_link_reference_id,  String razorpay_payment_link_status,  String  razorpay_signature) {

        String secret=environment.getProperty("razorpay.merchant-secret");
        try {
            RazorpayClient razorpayClient=new RazorpayClient(environment.getProperty("razorpay.merchant-id"),secret);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }

        PaymentRequestDto paymentRequestDto= (PaymentRequestDto) paymentRequestDtoRedisTemplate.opsForValue().get(razorpay_payment_link_id);
        assert paymentRequestDto != null;
        String razorpayPid=stringRedisTemplate.opsForValue().get(paymentRequestDto.getReferenceId()+"PaymentService");
        JSONObject options = new JSONObject();
        options.put("payment_link_reference_id", paymentRequestDto.getReferenceId());
        options.put("razorpay_payment_id", razorpay_payment_id);
        options.put("payment_link_status", razorpay_payment_link_status);
        options.put("payment_link_id", razorpayPid);
        options.put("razorpay_signature", razorpay_signature);

        assert secret != null;
        boolean status = false;
        try {
            status = Utils.verifyPaymentLink(options, secret);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }

        if(status) {
            PaymentStatus paymentStatus=stringPaymentStatusHashMap.get(razorpay_payment_link_status);
            return paymentRequestDto.getCallbackUrl()+"?status="+paymentStatus.name()+"&reference_id="+paymentRequestDto.getReferenceId();
        } else {
            return paymentRequestDto.getCallbackUrl()+"?status="+PaymentStatus.INVALID+"&reference_id="+paymentRequestDto.getReferenceId();
        }
    }
}
