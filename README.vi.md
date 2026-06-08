# Ứng dụng Đặt vé Xem phim trên điện thoại di động

*[Read the English version here](README.md)*

Chào mừng bạn đến với **Ứng dụng Đặt vé Xem phim trên điện thoại di động**! Đây là một ứng dụng di động được thiết kế để đặt vé xem phim, ứng dụng di động này chỉ bao gồm Khách hàng. Hệ thống gồm cả Quản lý và Khách hàng xem chi tiết tại: https://github.com/hongquan-dev/cinema-ticket-booking-web

## 🏗️ Công nghệ sử dụng

Dự án này được chia thành hai phần (Backend, Frontend):

### 1. Backend (`backend-spring-boot`)

- **Framework:** Spring Boot 3.4.3
- **Ngôn ngữ:** Java 21
- **Công cụ Build:** Maven 3.9.11
- **Cơ sở dữ liệu:** PostgreSQL 18
- **Bảo mật:** Spring Security & JWT (JSON Web Tokens)
- **ORM:** Spring Data JPA
- **Công cụ/Thư viện khác:** Lombok, Cloudinary (lưu trữ hình ảnh), JavaMail (gửi email thông báo)
- **Validation:** Jakarta Bean Validation (Hibernate Validator)

### 2. Frontend (`frontend-react-native`)

- **Framework:** React Native + Expo
- **Ngôn ngữ:** TypeScript
- **CSS Framework:** NativeWind (Tailwind CSS cho React Native)
- **Routing:** Expo Router
- **HTTP Client:** Axios
- **Animations:** React Native Reanimated
- **UI Components:** Lucide React Native, Expo Vector Icons
- **Utilities:** AsyncStorage, Expo SecureStore

## 🚀 Hướng dẫn cài đặt và chạy dự án

### Yêu cầu hệ thống

- **Java:** 21 trở lên (Khuyến nghị dùng Java 21, Java 25 bị xung đột với Lombok)
- **Apache Maven:** 3.8.x trở lên
- **Node.js:** 18.x trở lên (Khuyến khích dùng bản LTS mới nhất)
- **pnpm:** 8.x trở lên
- **PostgreSQL:** 13+ (Khuyên dùng 15.x hoặc 16.x)

### 1. Khởi chạy Backend (Spring Boot)

1. **Di chuyển vào thư mục backend:**

   ```bash
   cd backend-spring-boot
   ```
2. **Cấu hình `src/main/resources/application.properties`**
- Tạo 1 database tên là `cinema_booking_db` trong postgres, cổng `5432`
- Di chuyển tới thư mục: `src/main/resources/`
- Tạo mới một tệp tin tên là `application.properties`
- Sao chép nội dung dưới đây vào file `application.properties` vừa tạo:

   ```bash
   # ====== APPLICATION INFORMATION ======
   spring.application.name=cinema-booking-backend

   # ====== DATABASE CONFIGURATION (PostgreSQL) ======
   spring.datasource.url=jdbc:postgresql://localhost:5432/cinema_booking_db
   spring.datasource.username=tai-khoan-posgresql-cua-ban
   spring.datasource.password=mat-khau-posgresql-cua-ban
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
   cloudinary.cloud_name=cloudinary-ten-cua-ban
   cloudinary.api_key=cloudinary-key-cua-ban
   cloudinary.api_secret=cloudinary-secret-cua-ban

   # ===== JACKSON CONFIGURATION ======
   spring.jackson.date-format=yyyy-MM-dd
   spring.servlet.multipart.max-file-size=5MB
   spring.servlet.multipart.max-request-size=5MB

   # ====== MAIL CONFIGURATION ======
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=email-cua-ban
   spring.mail.password=mat-khau-application-password-cua-ban

   # ====== SMTP MAIL PROPERTIES ======
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   spring.mail.properties.mail.smtp.starttls.required=true
   spring.mail.properties.mail.smtp.connectiontimeout=5000
   spring.mail.properties.mail.smtp.timeout=5000
   spring.mail.properties.mail.smtp.writetimeout=5000
   ```

3. **Khởi chạy ứng dụng:**

   ```bash
   mvn spring-boot:run
   ```
   *Backend sẽ chạy tại:* `http://localhost:8080`

### 2️. Khởi chạy Frontend (React Native)

1. **Di chuyển vào thư mục frontend:**

    ```bash
    cd frontend-react-native
    ```

2. **Cài đặt thư viện:**

    ```bash
    pnpm install
    ```

*(Nếu chưa có pnpm, cài bằng: `npm install -g pnpm`)*

3. **Khởi chạy:**

    ```bash
    npx expo start
    ```

4. **Bạn có thể nhấn 'w' để xem trên web**

    ```bash
    w
    ```

**Frontend sẽ chạy tại:** `http://localhost:8081` (Web) hoặc quét mã QR bằng ứng dụng Expo Go

