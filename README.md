### Prerequisites
* **FFmpeg**: This application uses FFmpeg for video processing tasks like extracting video duration and metadata.
    * **Install FFmpeg**:
        * On **Ubuntu**: `sudo apt install ffmpeg`
        * On **Windows**: [Download FFmpeg](https://www.gyan.dev/ffmpeg/builds/) and add it to your system PATH.

Ensure FFmpeg is correctly installed and accessible via the command line before running the application.

### Database Setup
* **PostgreSQL Configuration**:
    * Install PostgreSQL on your system (version 12 or higher recommended)
    * Create a new database:
      ```sql
      CREATE DATABASE your_database_name;
      ```
    * Default credentials (customize these in your properties file):
      ```
      username: your_username
      password: your_password
      port: 5432 (default PostgreSQL port)
      ```
    * Ensure your PostgreSQL server is running and accessible

### Application Properties Configuration

#### Local Environment Setup
1. Create `application-local.properties` in `src/main/resources/` with the following settings:

```properties
# Server Configuration
# You can change the port as needed
server.port=your_port_number
app.domain=http://localhost:${server.port}
app.frontend.base-url=http://localhost:${server.port}
app.backend.base-url=http://localhost:${server.port}

# Database Configuration
# Modify these values according to your database setup
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate settings
spring.jpa.hibernate.ddl-auto=update  # Options: create, create-drop, validate, update
spring.jpa.show-sql=true             # Set to false in production

# Email Configuration
# Replace with your email service credentials
spring.mail.host=your_smtp_host
spring.mail.port=your_smtp_port
spring.mail.username=your_email_username
spring.mail.password=your_email_password

# Attachment Configuration
# Update this URL according to your server configuration
attach.url=http://localhost:${server.port}/api/attach
```

#### Main Application Properties
Create or update `application.properties` with:

```properties
spring.application.name=youtube-backend
spring.profiles.active=local    # Options: local, dev, prod

# Flyway Configuration
spring.flyway.enabled=false     # Set to true if you want to use Flyway migrations
spring.flyway.baselineOnMigrate=true
spring.flyway.baselineVersion=0

# Email Settings
registration.confirmation.deadline.minutes=10
registration.max.resend.attempts=3
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# File Upload Configuration
# Adjust these values based on your requirements
spring.servlet.multipart.max-file-size=1000MB
spring.servlet.multipart.max-request-size=1000MB
attach.upload.folder=attaches   # Specify your preferred upload directory
```

### Setup Instructions
1. **Clone the Repository**
   ```bash
   git clone [repository-url]
   cd [project-directory]
   ```

2. **Database Preparation**
    * Install PostgreSQL if not already installed
    * Create a new database
    * Update the database configuration in `application-local.properties`

3. **Configure Application Properties**
    * Copy the provided properties templates
    * Update all placeholder values (marked with `your_*`)
    * Ensure all paths and URLs are correct for your environment

4. **Environment Setup**
    * Install FFmpeg and verify it's accessible from command line:
      ```bash
      ffmpeg -version
      ```
    * Set up Java Development Kit (JDK 17 or later recommended)
    * Install Maven or ensure it's available in your system

5. **Run the Application**
    * Using Maven:
      ```bash
      ./mvnw spring-boot:run
      ```
    * Or using your IDE:
        * Import as Maven project
        * Run the main application class

### Important Notes
* **Port Configuration**: The default port (8090) can be changed in the properties file
* **Database Name**: The database name 'youtube' is just an example; use any name you prefer
* **Email Service**:
    * The example uses Mailtrap (good for testing)
    * For production, configure with your actual SMTP server details
* **File Upload Limits**:
    * Default is set to 1000MB
    * Adjust based on your server capacity and requirements
* **Security**:
    * Debug logging is enabled by default
    * Consider disabling in production
    * Change all default credentials before deploying
* **Profiles**:
    * Application uses Spring profiles (local, dev, prod)
    * Create separate properties files for each environment

### System Requirements
* **Java**: JDK 17 or later
* **PostgreSQL**: 12.0 or later
* **Maven**: 3.6.0 or later
* **FFmpeg**: Latest stable version
* **Storage**: Sufficient for your upload folder capacity
* **Memory**: Minimum 4GB RAM recommended

### Development Environment Recommendations
* **IDE**: IntelliJ IDEA (recommended), Eclipse, or VS Code
* **Database Tools**: pgAdmin or DBeaver for database management
* **API Testing**: Postman or similar API testing tool
* **Git**: Latest version for version control

### Common Issues and Solutions
* **Database Connection Failed**:
    * Verify PostgreSQL is running
    * Check credentials and database name
    * Ensure correct port configuration

* **FFmpeg Not Found**:
    * Verify FFmpeg is in system PATH
    * Try reinstalling FFmpeg
    * Check command line access

* **Upload Issues**:
    * Verify upload directory permissions
    * Check file size limits in properties
    * Ensure sufficient disk space