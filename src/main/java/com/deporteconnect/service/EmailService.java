package com.deporteconnect.service;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String INSTITUTIONAL_REPORT_EMAIL = "ctapizarro2000@gmail.com";
    private static final DateTimeFormatter EVENT_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@deporteconnect.local}")
    private String from;

    @Value("${spring.mail.host:localhost}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private Integer mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public void notifyActivityReported(Activity activity, ActivityReport report, long reportCount) {
        log.info("EmailService.notifyActivityReported ejecutado: activityId={}, reportCount={}", activity.getId(), reportCount);
        sendToOrganizer(
                activity,
                "Tu actividad recibio un reporte",
                buildActivityReportedBody(activity, report, reportCount)
        );
    }

    public void notifyActivityUnderReview(Activity activity, long reportCount) {
        log.info("EmailService.notifyActivityUnderReview ejecutado: activityId={}, reportCount={}", activity.getId(), reportCount);
        sendToOrganizer(
                activity,
                "Tu actividad quedo en revision",
                buildUnderReviewBody(activity, reportCount)
        );
    }

    public void notifyInstitutionalActivityReport(Activity activity, ActivityReport report) {
        log.info("EmailService.notifyInstitutionalActivityReport ejecutado: activityId={}, reportId={}", activity.getId(), report.getId());
        sendToAddress(
                activity,
                INSTITUTIONAL_REPORT_EMAIL,
                "Nuevo reporte de actividad - DeporteConnect",
                buildInstitutionalReportBody(activity, report)
        );
    }

    private void sendToOrganizer(Activity activity, String subject, String body) {
        String organizerEmail = activity.getOrganizer() == null ? null : activity.getOrganizer().getEmail();
        log.info(
                "Preparando email: enabled={}, host={}, port={}, usernameConfigured={}, from={}, to={}, subject={}",
                mailEnabled,
                mailHost,
                mailPort,
                !isBlank(mailUsername),
                from,
                organizerEmail,
                subject
        );

        if (!mailEnabled) {
            log.warn("Email deshabilitado por configuracion: MAIL_ENABLED no esta en true. activityId={}", activity.getId());
            return;
        }
        if (activity.getOrganizer() == null || isBlank(organizerEmail)) {
            log.warn("No se envio email: actividad {} no tiene organizador con email", activity.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(organizerEmail);
            message.setSubject(subject);
            message.setText(body);
            log.info("Enviando email SMTP: activityId={}, to={}, subject={}", activity.getId(), organizerEmail, subject);
            mailSender.send(message);
            log.info("Email SMTP enviado correctamente: activityId={}, to={}", activity.getId(), organizerEmail);
        } catch (Exception e) {
            log.error("No se pudo enviar email de actividad {}: {}", activity.getId(), e.getMessage(), e);
        }
    }

    private void sendToAddress(Activity activity, String to, String subject, String body) {
        log.info(
                "Preparando email institucional: enabled={}, host={}, port={}, usernameConfigured={}, from={}, to={}, subject={}",
                mailEnabled,
                mailHost,
                mailPort,
                !isBlank(mailUsername),
                from,
                to,
                subject
        );

        if (!mailEnabled) {
            log.warn("Email institucional deshabilitado por configuracion: MAIL_ENABLED no esta en true. activityId={}", activity.getId());
            return;
        }
        if (isBlank(to)) {
            log.warn("No se envio email institucional: destinatario vacio. activityId={}", activity.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            log.info("Enviando email institucional SMTP: activityId={}, to={}, subject={}", activity.getId(), to, subject);
            mailSender.send(message);
            log.info("Email institucional SMTP enviado correctamente: activityId={}, to={}", activity.getId(), to);
        } catch (Exception e) {
            log.error("No se pudo enviar email institucional de actividad {}: {}", activity.getId(), e.getMessage(), e);
        }
    }

    private String buildActivityReportedBody(Activity activity, ActivityReport report, long reportCount) {
        String description = isBlank(report.getDescription())
                ? "No se entrego descripcion adicional."
                : report.getDescription();

        return """
                Hola %s,

                Tu actividad recibio un reporte en DeporteConnect.

                Actividad: %s
                Fecha: %s
                Motivo del reporte: %s
                Descripcion: %s
                Reportes acumulados: %d

                Revisaremos la informacion. Si la actividad acumula mas reportes, podria quedar temporalmente en revision.

                Equipo DeporteConnect
                """.formatted(
                organizerName(activity),
                activity.getTitle(),
                formatEventAt(activity),
                report.getReason(),
                description,
                reportCount
        );
    }

    private String buildInstitutionalReportBody(Activity activity, ActivityReport report) {
        return """
                Nuevo reporte de actividad recibido.

                ID de la actividad: %d
                Titulo de la actividad: %s
                Motivo del reporte: %s
                Usuario que reporto: %s
                Fecha/hora del reporte: %s

                Equipo DeporteConnect
                """.formatted(
                activity.getId(),
                activity.getTitle(),
                report.getReason(),
                reporterInfo(report),
                report.getCreatedAt() == null ? "Fecha no disponible" : report.getCreatedAt().format(EVENT_FORMAT)
        );
    }

    private String buildUnderReviewBody(Activity activity, long reportCount) {
        return """
                Hola %s,

                Tu actividad quedo temporalmente en revision por acumulacion de reportes.

                Actividad: %s
                Fecha: %s
                Reportes acumulados: %d

                Mientras este en revision, la actividad no aparecera en el feed de actividades disponibles.

                Equipo DeporteConnect
                """.formatted(
                organizerName(activity),
                activity.getTitle(),
                formatEventAt(activity),
                reportCount
        );
    }

    private String organizerName(Activity activity) {
        String name = activity.getOrganizer() == null ? null : activity.getOrganizer().getFullName();
        return isBlank(name) ? "organizador" : name;
    }

    private String formatEventAt(Activity activity) {
        return activity.getEventAt() == null ? "Fecha no disponible" : activity.getEventAt().format(EVENT_FORMAT);
    }

    private String reporterInfo(ActivityReport report) {
        if (report.getReporter() == null) {
            return "Usuario no disponible";
        }
        String name = report.getReporter().getFullName();
        String email = report.getReporter().getEmail();
        if (isBlank(name)) return isBlank(email) ? "Usuario no disponible" : email;
        return isBlank(email) ? name : "%s <%s>".formatted(name, email);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
