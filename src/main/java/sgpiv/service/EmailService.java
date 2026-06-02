package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:noreply@sgpiv.com}")
    private String mailFrom;

    public void enviarSolicitudRecibida(String destinatario, String razonSocial) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(mailFrom);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Solicitud de radicación recibida");
        mensaje.setText(
                "Estimado/a representante de " + razonSocial + ",\n\n" +
                        "Su solicitud de radicación fue recibida correctamente y se encuentra en revisión.\n\n" +
                        "Le notificaremos cuando haya novedades.\n\n" +
                        "Parque Industrial - SGPIV"
        );
        mailSender.send(mensaje);
    }

    public void enviarSolicitudAprobada(String destinatario, String razonSocial) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(mailFrom);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Solicitud de radicación aprobada");
        mensaje.setText(
                "Estimado/a representante de " + razonSocial + ",\n\n" +
                        "¡Su solicitud de radicación fue aprobada! Ya puede gestionar su proyecto en el sistema.\n\n" +
                        "Parque Industrial - SGPIV"
        );
        mailSender.send(mensaje);
    }

    public void enviarSolicitudRechazada(String destinatario, String razonSocial, String motivo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(mailFrom);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Solicitud de radicación rechazada");
        mensaje.setText(
                "Estimado/a representante de " + razonSocial + ",\n\n" +
                        "Lamentablemente su solicitud de radicación fue rechazada.\n\n" +
                        "Motivo: " + motivo + "\n\n" +
                        "Parque Industrial - SGPIV"
        );
        mailSender.send(mensaje);
    }

    public void enviarRequiereModificacion(String destinatario, String razonSocial, String motivo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(mailFrom);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Solicitud requiere modificaciones");
        mensaje.setText(
                "Estimado/a representante de " + razonSocial + ",\n\n" +
                        "Su solicitud de radicación requiere algunas modificaciones antes de continuar.\n\n" +
                        "Motivo: " + motivo + "\n\n" +
                        "Por favor, ingrese al sistema y realice los cambios solicitados.\n\n" +
                        "Parque Industrial - SGPIV"
        );
        mailSender.send(mensaje);
    }
}