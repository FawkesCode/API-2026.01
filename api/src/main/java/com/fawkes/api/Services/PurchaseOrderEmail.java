
package com.fawkes.api.Services;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class PurchaseOrderEmail{


    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(
        String toEmail,
        String Username,
        String itemName, 
        BigDecimal itemPrice,
        Date date ){

        String emailBody = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirmação de Solicitação de Compra</title>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); border: 1px solid #e0e0e0; }
                    .header { background-color: #2c3e50; color: #ffffff; padding: 20px; text-align: center; }
                    .header h2 { margin: 0; font-size: 22px; }
                    .content { padding: 30px; color: #333333; line-height: 1.6; }
                    .details-table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    .details-table td { padding: 12px; border-bottom: 1px solid #eeeeee; }
                    .details-table td.label { font-weight: bold; color: #555555; width: 30%; }
                    .details-table td.value { color: #222222; }
                    .footer { background-color: #f8f9fa; text-align: center; padding: 15px; font-size: 12px; color: #777777; border-top: 1px solid #eeeeee; }
                </style>
                </head>
                <body>

                <div class="container">
                    <div class="header">
                        <h2>Solicitação de Compra de Ativo</h2>
                    </div>

                    <div class="content">
                        <p>Olá, uma nova solicitação de compra de ativo foi registrada no sistema e está aguardando processamento.</p>
                        <p>Confira os detalhes abaixo:</p>

                        <table class="details-table">
                            <tr>
                                <td class="label">Solicitante:</td>
                                <td class="value">{{userName}}</td>
                            </tr>
                            <tr>
                                <td class="label">Ativo:</td>
                                <td class="value">{{itemName}}</td>
                            </tr>
                            <tr>
                                <td class="label">Valor:</td>
                                <td class="value">R$ {{itemPrice}}</td>
                            </tr>
                            <tr>
                                <td class="label">Data/Hora:</td>
                                <td class="value">{{requestDate}}</td>
                            </tr>
                        </table>

                        <p>Se notar alguma divergência nos dados, por favor, entre em contato com o suporte interno imediatamente.</p>
                    </div>

                    <div class="footer">
                        <p>Este é um e-mail automático enviado pelo Sistema de Gestão de Ativos. Por favor, não responda.</p>
                    </div>
                </div>

                </body>
                </html>
                """;

                String formatedEmailMessage = emailBody
                        .replace("{{userName}}",Username)
                        .replace("{{itemName}}",itemName)
                        .replace("{{itemPrice}}",itemPrice.toString())
                        .replace("{{requestDate}}",date.toString());
        try {
        MimeMessage orderAdviser = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(orderAdviser, true, "UTF-8");               
        
        message.setFrom("avisoswelogic@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Solicitação de compra");
        message.setText(formatedEmailMessage,true);
        
        mailSender.send(orderAdviser);
        
            
        } catch (Exception e) {
            throw new RuntimeException("erro ao enviar email: " + e);
        }
       


    }

    
}