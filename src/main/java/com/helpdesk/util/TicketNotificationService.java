package com.helpdesk.util;

import com.helpdesk.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TicketNotificationService {

    private static final Logger LOGGER = Logger.getLogger(TicketNotificationService.class.getName());

    private TicketNotificationService() {
    }

    public static void notifyTicketCreated(String subject, String category, String priority, String createdBy, String assignedTo) {
        StringBuilder body = new StringBuilder();
        body.append("A ticket has been created.\n\n")
                .append("Subject: ").append(subject).append("\n")
                .append("Category: ").append(category).append("\n")
                .append("Priority: ").append(priority).append("\n")
                .append("Created By: ").append(createdBy).append("\n")
                .append("Assigned To: ").append(normalizeAssignedTo(assignedTo)).append("\n");

        notifyAdmins("New Ticket Created", body.toString());
        EmailUtil.sendEmail(createdBy, "Ticket Created Successfully", body.toString());
        notifyAssignedAgent(assignedTo, "New Ticket Created", body.toString());
    }

    public static void notifyTicketAssigned(String subject, String category, String assignedTo, String createdBy) {
        String body = "A ticket has been assigned.\n\n"
                + "Subject: " + subject + "\n"
                + "Category: " + category + "\n"
                + "Assigned To: " + assignedTo + "\n";

        notifyAdmins("Ticket Assigned", body);
        EmailUtil.sendEmail(createdBy, "Ticket Assigned", body);
        notifyAssignedAgent(assignedTo, "Ticket Assigned", body);
    }

    public static void notifyTicketUpdated(String subject, String category, String status, String assignedTo, String createdBy, String agentResponse) {
        String body = "A ticket has been updated.\n\n"
                + "Subject: " + subject + "\n"
                + "Category: " + category + "\n"
                + "Status: " + status + "\n"
                + "Assigned To: " + normalizeAssignedTo(assignedTo) + "\n"
                + "Agent Response: " + (agentResponse == null || agentResponse.isBlank() ? "No response provided" : agentResponse) + "\n";

        notifyAdmins("Ticket Updated", body);
        EmailUtil.sendEmail(createdBy, "Ticket Updated", body);
        notifyAssignedAgent(assignedTo, "Ticket Updated", body);
    }

    public static void notifyAccountUpdated(String oldEmail, String newEmail, String role) {
        String body = "Your HelpDesk System account details were updated.\n\n"
                + "Role: " + (role == null || role.isBlank() ? "Unknown" : role) + "\n"
                + "Email: " + (newEmail == null || newEmail.isBlank() ? oldEmail : newEmail) + "\n"
                + "If you did not request this change, please contact support immediately.\n";

        sendToDistinctRecipients(oldEmail, newEmail, "HelpDesk System Account Updated", body);
    }

    private static void notifyAdmins(String subject, String body) {
        for (String email : loadEmailsByRole("admin")) {
            EmailUtil.sendEmail(email, subject, body);
        }
    }

    private static void notifyAssignedAgent(String assignedTo, String subject, String body) {
        String agentEmail = findUserEmailByUsernameAndRole(assignedTo, "agent");
        if (agentEmail != null) {
            EmailUtil.sendEmail(agentEmail, subject, body);
        }
    }

    private static String findUserEmailByUsernameAndRole(String username, String role) {
        if (username == null || username.isBlank()) {
            return null;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE username = ? AND role = ?")) {
            ps.setString(1, username);
            ps.setString(2, role);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("email") : null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to resolve recipient for username=" + username, e);
            return null;
        }
    }

    private static Set<String> loadEmailsByRole(String role) {
        Set<String> emails = new LinkedHashSet<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE role = ? ORDER BY email")) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("email");
                    if (email != null && !email.isBlank()) {
                        emails.add(email);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load emails for role=" + role, e);
        }
        return emails;
    }

    private static String normalizeAssignedTo(String assignedTo) {
        return assignedTo == null || assignedTo.isBlank() ? "Unassigned" : assignedTo;
    }

    private static void sendToDistinctRecipients(String firstEmail, String secondEmail, String subject, String body) {
        Set<String> recipients = new LinkedHashSet<>();
        if (firstEmail != null && !firstEmail.isBlank()) {
            recipients.add(firstEmail.trim());
        }
        if (secondEmail != null && !secondEmail.isBlank()) {
            recipients.add(secondEmail.trim());
        }
        for (String recipient : recipients) {
            EmailUtil.sendEmail(recipient, subject, body);
        }
    }
}
