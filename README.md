# HelpDesk Maven Migration

This project was migrated from a NetBeans/Ant web application into a Maven-based Java web application that targets Apache Tomcat 9.

## What Changed

- Migrated the project to the standard Maven layout:
  - `src/main/java`
  - `src/main/resources`
  - `src/main/webapp`
- Replaced hardcoded database credentials with property and environment based configuration.
- Added connection pooling using Tomcat JDBC Pool.
- Added password hashing with PBKDF2 and automatic upgrade of legacy plaintext passwords after a successful login.
- Added request filtering so admin, agent, and user areas are protected consistently.
- Refactored ticket creation, agent management, profile updates, settings management, and agent ticket updates with stronger validation.
- Fixed broken JSP issues such as:
  - missing JSTL declarations
  - invalid login/register flows
  - settings category delete bug
  - broken agent ticket detail/update screen
- Added a database bootstrap script at `src/main/resources/db/schema.sql`.
- Removed the old Ant, Ivy, NetBeans, and generated output folders from the working project tree.

## Build

The project was verified with:

```powershell
mvn -o clean install
```

The generated WAR is:

```text
target/helpdesk.war
```

## Configuration

Default values live in:

```text
src/main/resources/helpdesk.properties
```

Override them with environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
DB_POOL_MAXSIZE
MAIL_ENABLED
MAIL_FROM
MAIL_PASSWORD
MAIL_HOST
MAIL_PORT
MAIL_STARTTLS
```

Example PowerShell session:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/helpdesk_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-password"
$env:MAIL_ENABLED="false"
mvn -o clean install
```

### Gmail SMTP Setup

To turn on email notifications, use a Gmail account with an app password:

```powershell
$env:MAIL_ENABLED="true"
$env:MAIL_FROM="youraccount@gmail.com"
$env:MAIL_PASSWORD="your-gmail-app-password"
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_STARTTLS="true"
```

If you prefer properties instead of environment variables, update `src/main/resources/helpdesk.properties` with the same values.

Use a Gmail app password, not your normal Gmail login password.

## Database Setup

Run the schema script against MySQL:

```powershell
mysql -u root -p < src/main/resources/db/schema.sql
```

The script creates:

- `users`
- `departments`
- `categories`
- `statuses`
- `tickets`

It also seeds baseline departments, categories, and ticket statuses.

## Deploy Automatically On Windows

If Docker is not installed, use the PowerShell deploy script:

```powershell
.\deploy.ps1
```

This will:

- build the WAR with Maven
- stop Tomcat
- copy the new WAR into `webapps`
- restart Tomcat
- open the login page in your browser

Then open:

```text
http://localhost:8080/helpdesk/login.jsp
```

If Tomcat is installed in a different folder, edit the `$tomcatRoot` value at the top of [deploy.ps1](deploy.ps1).

## Deploy Automatically With Docker

If Docker is installed, you can still run the full stack with:

```powershell
docker compose up --build
```

Then open:

```text
http://localhost:8080/helpdesk/login.jsp
```

The compose file starts:

- MySQL 8.4
- the HelpDesk WAR inside Tomcat 9
- the schema bootstrap automatically on first run

## Deploy To Tomcat Manually

If you prefer a traditional Tomcat install:

1. Build the WAR:

```powershell
mvn -o clean install
```

2. Copy `target/helpdesk.war` to your Tomcat `webapps` directory.

3. Start Tomcat and open:

```text
http://localhost:8080/helpdesk/login.jsp
```

## Notes About Dependencies

This environment could not reach Maven Central, so the migration was made fully buildable offline using locally available jars placed in the project `lib/` folder and packaged into the WAR where needed. The application still builds through Maven and produces a deployable artifact.
