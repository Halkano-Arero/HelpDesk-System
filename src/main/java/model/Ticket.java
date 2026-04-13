package model;

public class Ticket {
    private int id;
    private String helpdesk_id;
    private String name;
    private String subject;
    private String assigned_to;
    private String created_at;
    private String status;

    public Ticket(int id, String helpdesk_id, String name, String subject,
                  String assigned_to, String created_at, String status) {
        this.id = id;
        this.helpdesk_id = helpdesk_id;
        this.name = name;
        this.subject = subject;
        this.assigned_to = assigned_to;
        this.created_at = created_at;
        this.status = status;
    }

    public int getId() { return id; }
    public String getHelpdeskId() { return helpdesk_id; }
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public String getAssignedTo() { return assigned_to; }
    public String getCreatedAt() { return created_at; }
    public String getStatus() { return status; }
}
