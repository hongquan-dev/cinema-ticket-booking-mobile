# Mobile Cinema Ticket Booking App

*[Đọc phiên bản Tiếng Việt tại đây](README.vi.md)*

Welcome to the **Mobile Cinema Ticket Booking App**! This is a mobile application designed for booking cinema tickets, and this mobile app is exclusively for Customers. For the complete system including both Admin and Customer views, see: https://github.com/hongquan-dev/cinema-ticket-booking-web

## 🏗️ Technologies Used

This project is divided into two parts (Backend, Frontend):

### 1. Backend (`backend-spring-boot`)

- **Framework:** Spring Boot 3.4.3
- **Language:** Java 21
- **Build Tool:** Maven 3.9.11
- **Database:** PostgreSQL 18
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **ORM:** Spring Data JPA
- **Other Tools/Libraries:** Lombok, Cloudinary (image storage), JavaMail (sending email notifications)
- **Validation:** Jakarta Bean Validation (Hibernate Validator)

### 2. Frontend (`frontend-react-native`)

- **Framework:** React Native + Expo
- **Language:** TypeScript
- **CSS Framework:** NativeWind (Tailwind CSS for React Native)
- **Routing:** Expo Router
- **HTTP Client:** Axios
- **Animations:** React Native Reanimated
- **UI Components:** Lucide React Native, Expo Vector Icons
- **Utilities:** AsyncStorage, Expo SecureStore

## 🚀 Installation & Running the Project

### System Requirements

- **Java:** 21 or higher (Java 21 is recommended, Java 25 has conflicts with Lombok)
- **Apache Maven:** 3.8.x or higher
- **Node.js:** 18.x or higher (Latest LTS version is recommended)
- **pnpm:** 8.x or higher
- **PostgreSQL:** 13+ (15.x or 16.x is recommended)

### 1. Running the Backend (Spring Boot)

1. **Navigate to the backend directory:**

   ```bash
   cd backend-spring-boot
   ```
2. **Configure `src/main/resources/application.properties`**
- Create a database named `cinema_booking_db` in PostgreSQL, port `5432`
- Navigate to the directory: `src/main/resources/`
- Create a new file named `application.properties`
- Copy the content below into the newly created `application.properties` file:

   ```bash
   # ====== APPLICATION INFORMATION ======
   spring.application.name=cinema-booking-backend

   # ====== DATABASE CONFIGURATION (PostgreSQL) ======
   spring.datasource.url=jdbc:postgresql://localhost:5432/cinema_booking_db
   spring.datasource.username=your-postgresql-username
   spring.datasource.password=your-postgresql-password
   spring.datasource.driver-class-name=org.postgresql.Driver

   # ====== JPA / HIBERNATE CONFIGURATION ======
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # ====== SERVER CONFIGURATION ======
   server.port=8080
   app.backend.url=http://localhost:8080
   jwt.secret=your-very-secure-secret-key-that-is-at-least-32-characters-long-2026

   # Write the IPv4 Wi-Fi address to allow phone same network as computer to call the API
   # Note: The phone running Expo and the computer running backend must be on the same Wi-Fi network
   # Change last number in IP to *
   app.cors.allowed-origin=http://[YOUR_COMPUTER_IP_INTERNET]:* 

   # Write your computer's IPv4 Wi-Fi address for verify email
   app.backendForEmail.url=http://[YOUR_COMPUTER_IP_INTERNET]:8080

   # ====== CLOUDINARY CONFIGURATION (Image Upload Service) ======
   cloudinary.cloud_name=your-cloudinary-name
   cloudinary.api_key=your-cloudinary-key
   cloudinary.api_secret=your-cloudinary-secret

   # ===== JACKSON CONFIGURATION ======
   spring.jackson.date-format=yyyy-MM-dd
   spring.servlet.multipart.max-file-size=5MB
   spring.servlet.multipart.max-request-size=5MB

   # ====== MAIL CONFIGURATION ======
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email
   spring.mail.password=your-application-password

   # ====== SMTP MAIL PROPERTIES ======
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   spring.mail.properties.mail.smtp.starttls.required=true
   spring.mail.properties.mail.smtp.connectiontimeout=5000
   spring.mail.properties.mail.smtp.timeout=5000
   spring.mail.properties.mail.smtp.writetimeout=5000
   ```

3. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```
   *The backend will run at:* `http://localhost:8080`

### 2️. Running the Frontend (React Native)

1. **Navigate to the frontend directory:**

    ```bash
    cd frontend-react-native
    ```

2. **Install dependencies:**

    ```bash
    pnpm install
    ```

*(If you don't have pnpm, install it using: `npm install -g pnpm`)*

3. **Start the app:**

    ```bash
    npx expo start
    ```

4. **You can press 'w' to view it on the web**

    ```bash
    w
    ```

**The frontend will run at:** `http://localhost:8081` (Web) or by scanning the QR code with the Expo Go app
