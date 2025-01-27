package com.ewallet.userservice.services;

import com.ewallet.userservice.dtos.EmailRequest;
import com.ewallet.userservice.dtos.TicketBookingResponseDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Objects;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final Environment environment;
    private final ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    public MailService(JavaMailSender javaMailSender, Environment environment, ThymeleafViewResolver thymeleafViewResolver) {
        this.javaMailSender = javaMailSender;
        this.environment= environment;
        this.thymeleafViewResolver = thymeleafViewResolver;
    }

    @Async
    public void triggerMail(EmailRequest emailRequest){

        MimeMessagePreparator mimeMessagePreparator=new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, MULTIPART_MODE_RELATED, "UTF-8");
                mimeMessageHelper.setFrom(Objects.requireNonNull(environment.getProperty("java.mail.sender.from.email.address")));
                mimeMessageHelper.setTo(emailRequest.getTo());
                mimeMessageHelper.setText(emailRequest.getBody());
                mimeMessageHelper.setSubject(emailRequest.getSubject());

            }
        };
        javaMailSender.send(mimeMessagePreparator);

    }

    @Async
    public void triggerTicketConfirmationMail(String email, ModelAndView modelAndView, TicketBookingResponseDto ticketBookingResponseDto){
        //html to string
        String htmlContent= "";
        try {
            Context context=new Context();
            context.setVariables(modelAndView.getModel());
            StringWriter stringWriter=new StringWriter();
            thymeleafViewResolver.getTemplateEngine().process(modelAndView.getViewName(), context, stringWriter);
            htmlContent = stringWriter.toString();
        } catch (RuntimeException runtimeException){
            log.error(runtimeException.getMessage());
        }
        byte[] content=null;
        try(ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream(); ){
            PdfRendererBuilder pdfRendererBuilder=new PdfRendererBuilder();
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.withHtmlContent(htmlContent,null);
            pdfRendererBuilder.toStream(byteArrayOutputStream);
            pdfRendererBuilder.run();

            byteArrayOutputStream.flush();

            content = byteArrayOutputStream.toByteArray();

        } catch (Exception e){
            log.error(e.getMessage());
        }
        if(content==null||content.length==0){
            throw new RuntimeException("pdf generation failed");
        }
        EmailRequest emailRequest=EmailRequest.builder().
                to(email)
                .message("Hi user, please find you ticket attached in the mail.")
                .subject("Ticket Confirmation")
                .build();
        sendMailWithAttachments(emailRequest,content,ticketBookingResponseDto.getReservationId());
    }

    public void sendMailWithAttachments(EmailRequest emailRequest, byte[] content,String fileName){

            MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_RELATED, "UTF-8");
                    mimeMessageHelper.setFrom("jhabooksdhn@gmail.com");
                    mimeMessageHelper.setTo(emailRequest.getTo());
                    mimeMessageHelper.setText(emailRequest.getMessage());
                    mimeMessageHelper.setSubject(emailRequest.getSubject());
//                    mimeMessageHelper.setCc(emailRequest.getCcSet().toArray(new String[0]));


//                    mimeMessageHelper.addAttachment(fileName+"ticket.pdf",new ByteArrayDataSource(content, MediaType.APPLICATION_PDF_VALUE));
                    mimeMessageHelper.addAttachment(fileName+"ticket.pdf",new ByteArrayResource(content));
             /*       Arrays.stream(multipartFiles).parallel().forEach(multipartFile -> {
                        try {
                            mimeMessageHelper.addAttachment(Objects.requireNonNull(multipartFile.getOriginalFilename()),
                                    new ByteArrayDataSource(multipartFile.getBytes(), "application/octet-stream"));
                        } catch (MessagingException | IOException e) {
                            LOGGER.error("Exception occurred while attaching file : {}", e.getClass().getSimpleName());
                        }
                    });*/
                }};

                    javaMailSender.send(mimeMessagePreparator);
                }



}
