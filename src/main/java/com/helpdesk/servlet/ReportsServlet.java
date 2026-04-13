package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            ReportFilter filter = ReportFilter.from(request);
            ReportData data = loadReportData(conn, filter);

            if ("csv".equalsIgnoreCase(request.getParameter("export"))) {
                writeCsv(response, data, filter);
                return;
            }

            request.setAttribute("filterStart", filter.startText);
            request.setAttribute("filterEnd", filter.endText);
            request.setAttribute("reportRangeLabel", filter.describeRange());
            request.setAttribute("totalTickets", data.totalTickets);
            request.setAttribute("assignedTickets", data.assignedTickets);
            request.setAttribute("unassignedTickets", data.unassignedTickets);
            request.setAttribute("ratedTickets", data.ratedTickets);
            request.setAttribute("totalUsers", data.totalUsers);
            request.setAttribute("statusChart", data.statusCounts);
            request.setAttribute("createdTimeline", data.createdTimeline);
            request.setAttribute("categoryCounts", data.categoryCounts);
            request.setAttribute("roleCounts", data.roleCounts);
            request.setAttribute("topAgents", data.topAgents);
            request.setAttribute("recentUsers", data.recentUsers);
            request.setAttribute("recentTickets", data.recentTickets);

            request.getRequestDispatcher("/reports.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reports error");
        }
    }

    private ReportData loadReportData(Connection conn, ReportFilter filter) throws SQLException {
        ReportData data = new ReportData();

        data.totalTickets = count(conn, "SELECT COUNT(*) FROM tickets" + filter.whereClause, filter.params);
        data.assignedTickets = count(conn,
                "SELECT COUNT(*) FROM tickets" + filter.whereClause + " AND assigned_to IS NOT NULL AND assigned_to <> ''",
                filter.params);
        data.unassignedTickets = count(conn,
                "SELECT COUNT(*) FROM tickets" + filter.whereClause + " AND (assigned_to IS NULL OR assigned_to = '')",
                filter.params);
        data.ratedTickets = count(conn,
                "SELECT COUNT(*) FROM tickets" + filter.whereClause + " AND rating > 0",
                filter.params);
        data.totalUsers = count(conn, "SELECT COUNT(*) FROM users", new ArrayList<>());

        data.statusCounts = loadCountsByLookup(conn,
                "SELECT name FROM statuses ORDER BY id",
                "status",
                "SELECT status, COUNT(*) total FROM tickets" + filter.whereClause + " GROUP BY status",
                filter.params);
        data.createdTimeline = loadCreatedTimeline(conn, filter);
        data.categoryCounts = loadCountsByLookup(conn,
                "SELECT name FROM categories ORDER BY id",
                "category",
                "SELECT category, COUNT(*) total FROM tickets" + filter.whereClause + " GROUP BY category",
                filter.params);
        data.roleCounts = loadRoleCounts(conn);
        data.topAgents = loadTopAgents(conn, filter);
        data.recentUsers = loadRecentUsers(conn, filter);
        data.recentTickets = loadRecentTickets(conn, filter);

        return data;
    }

    private long count(Connection conn, String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = prepare(conn, sql, params);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private LinkedHashMap<String, Integer> loadCountsByLookup(Connection conn,
                                                              String lookupSql,
                                                              String lookupKey,
                                                              String totalsSql,
                                                              List<Object> params) throws SQLException {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(lookupSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString(1), 0);
            }
        }

        try (PreparedStatement ps = prepare(conn, totalsSql, params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString(1);
                int total = rs.getInt(2);
                if (key == null || key.trim().isEmpty()) {
                    key = "Unknown " + lookupKey;
                }
                counts.putIfAbsent(key, 0);
                counts.put(key, total);
            }
        }
        return counts;
    }

    private List<Map<String, String>> loadTopAgents(Connection conn, ReportFilter filter) throws SQLException {
        List<Map<String, String>> agents = new ArrayList<>();
        String sql = "SELECT assigned_to, COUNT(*) total FROM tickets"
                + filter.whereClause
                + " AND assigned_to IS NOT NULL AND assigned_to <> ''"
                + " GROUP BY assigned_to ORDER BY total DESC, assigned_to ASC LIMIT 8";
        try (PreparedStatement ps = prepare(conn, sql, filter.params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("username", rs.getString("assigned_to"));
                row.put("total", String.valueOf(rs.getInt("total")));
                agents.add(row);
            }
        }
        return agents;
    }

    private List<Map<String, String>> loadRecentTickets(Connection conn, ReportFilter filter) throws SQLException {
        List<Map<String, String>> tickets = new ArrayList<>();
        String sql = "SELECT helpdesk_id, name, subject, category, assigned_to, status, "
                + "DATE_FORMAT(created_at, '%Y-%m-%d') AS created_date, "
                + "DATE_FORMAT(created_at, '%H:%i:%s') AS created_time "
                + "FROM tickets" + filter.whereClause + " ORDER BY created_at DESC, id DESC LIMIT 10";
        try (PreparedStatement ps = prepare(conn, sql, filter.params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("helpdeskId", rs.getString("helpdesk_id"));
                row.put("name", rs.getString("name"));
                row.put("subject", rs.getString("subject"));
                row.put("category", rs.getString("category"));
                row.put("assignedTo", rs.getString("assigned_to"));
                row.put("status", rs.getString("status"));
                row.put("createdDate", rs.getString("created_date"));
                row.put("createdTime", rs.getString("created_time"));
                tickets.add(row);
            }
        }
        return tickets;
    }

    private LinkedHashMap<String, Integer> loadCreatedTimeline(Connection conn, ReportFilter filter) throws SQLException {
        LinkedHashMap<String, Integer> timeline = new LinkedHashMap<>();
        String sql = "SELECT DATE(created_at) timeline_date, COUNT(*) total FROM tickets"
                + filter.whereClause
                + " GROUP BY DATE(created_at) ORDER BY timeline_date ASC";
        try (PreparedStatement ps = prepare(conn, sql, filter.params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("timeline_date");
                timeline.put(label, rs.getInt("total"));
            }
        }
        return timeline;
    }

    private LinkedHashMap<String, Integer> loadRoleCounts(Connection conn) throws SQLException {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        counts.put("admin", 0);
        counts.put("agent", 0);
        counts.put("user", 0);
        try (PreparedStatement ps = conn.prepareStatement("SELECT role, COUNT(*) total FROM users GROUP BY role");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("role"), rs.getInt("total"));
            }
        }
        return counts;
    }

    private List<Map<String, String>> loadRecentUsers(Connection conn, ReportFilter filter) throws SQLException {
        List<Map<String, String>> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT username, email, role, category, created_at FROM users ORDER BY created_at DESC, id DESC LIMIT 8");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("username", rs.getString("username"));
                row.put("email", rs.getString("email"));
                row.put("role", rs.getString("role"));
                row.put("category", rs.getString("category"));
                row.put("createdAt", rs.getString("created_at"));
                users.add(row);
            }
        }
        return users;
    }

    private PreparedStatement prepare(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        return ps;
    }

    private void writeCsv(HttpServletResponse response, ReportData data, ReportFilter filter) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"helpdesk-report.csv\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("HelpDesk System Report");
            out.println("Range," + csv(filter.describeRange()));
            out.println();
            out.println("Summary");
            out.println("Metric,Value");
            out.println("Total Tickets," + data.totalTickets);
            out.println("Assigned Tickets," + data.assignedTickets);
            out.println("Unassigned Tickets," + data.unassignedTickets);
            out.println("Rated Tickets," + data.ratedTickets);
            out.println("Total Users," + data.totalUsers);
            out.println();
            out.println("Status Breakdown");
            out.println("Status,Count");
            for (Map.Entry<String, Integer> entry : data.statusCounts.entrySet()) {
                out.println(csv(entry.getKey()) + "," + entry.getValue());
            }
            out.println();
            out.println("Category Breakdown");
            out.println("Category,Count");
            for (Map.Entry<String, Integer> entry : data.categoryCounts.entrySet()) {
                out.println(csv(entry.getKey()) + "," + entry.getValue());
            }
            out.println();
            out.println("User Roles");
            out.println("Role,Count");
            for (Map.Entry<String, Integer> entry : data.roleCounts.entrySet()) {
                out.println(csv(entry.getKey()) + "," + entry.getValue());
            }
            out.println();
            out.println("Recent Users");
            out.println("Username,Email,Role,Category");
            for (Map<String, String> user : data.recentUsers) {
                out.println(csv(user.get("username")) + ","
                        + csv(user.get("email")) + ","
                        + csv(user.get("role")) + ","
                        + csv(user.get("category")));
            }
            out.println();
            out.println("Recent Tickets");
            out.println("Helpdesk ID,Name,Subject,Category,Assigned To,Status,Created Date,Created Time");
            for (Map<String, String> ticket : data.recentTickets) {
                out.println(csv(ticket.get("helpdeskId")) + ","
                        + csv(ticket.get("name")) + ","
                        + csv(ticket.get("subject")) + ","
                        + csv(ticket.get("category")) + ","
                        + csv(ticket.get("assignedTo")) + ","
                        + csv(ticket.get("status")) + ","
                        + csv(ticket.get("createdDate")) + ","
                        + csv(ticket.get("createdTime")));
            }
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static final class ReportFilter {
        private final String startText;
        private final String endText;
        private final String whereClause;
        private final List<Object> params;

        private ReportFilter(String startText, String endText, String whereClause, List<Object> params) {
            this.startText = startText;
            this.endText = endText;
            this.whereClause = whereClause;
            this.params = params;
        }

        private static ReportFilter from(HttpServletRequest request) {
            String startText = safeTrim(request.getParameter("startDate"));
            String endText = safeTrim(request.getParameter("endDate"));
            List<Object> params = new ArrayList<>();
            StringBuilder where = new StringBuilder(" WHERE 1=1");

            LocalDate start = parseDate(startText);
            LocalDate end = parseDate(endText);
            if (start != null) {
                where.append(" AND DATE(created_at) >= ?");
                params.add(Date.valueOf(start));
                startText = start.toString();
            } else {
                startText = "";
            }
            if (end != null) {
                where.append(" AND DATE(created_at) <= ?");
                params.add(Date.valueOf(end));
                endText = end.toString();
            } else {
                endText = "";
            }

            return new ReportFilter(startText, endText, where.toString(), params);
        }

        private String describeRange() {
            if (!startText.isEmpty() && !endText.isEmpty()) {
                return startText + " to " + endText;
            }
            if (!startText.isEmpty()) {
                return "From " + startText;
            }
            if (!endText.isEmpty()) {
                return "Up to " + endText;
            }
            return "All time";
        }

        private static String safeTrim(String value) {
            return value == null ? "" : value.trim();
        }

        private static LocalDate parseDate(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }

    private static final class ReportData {
        private long totalTickets;
        private long assignedTickets;
        private long unassignedTickets;
        private long ratedTickets;
        private long totalUsers;
        private LinkedHashMap<String, Integer> statusCounts = new LinkedHashMap<>();
        private LinkedHashMap<String, Integer> createdTimeline = new LinkedHashMap<>();
        private LinkedHashMap<String, Integer> categoryCounts = new LinkedHashMap<>();
        private LinkedHashMap<String, Integer> roleCounts = new LinkedHashMap<>();
        private List<Map<String, String>> topAgents = new ArrayList<>();
        private List<Map<String, String>> recentUsers = new ArrayList<>();
        private List<Map<String, String>> recentTickets = new ArrayList<>();
    }
}
