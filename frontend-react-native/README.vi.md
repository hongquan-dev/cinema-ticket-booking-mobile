# Ứng dụng Đặt vé Xem phim trên điện thoại di động

*[Read the English version here](README.md)*

Chào mừng bạn đến với **Ứng dụng Đặt vé Xem phim trên điện thoại di động**! Đây là một ứng dụng di động được thiết kế để đặt vé xem phim, ứng dụng di động này chỉ bao gồm Khách hàng. Hệ thống gồm cả Quản lý và Khách hàng xem chi tiết tại: https://github.com/hongquan-dev/cinema-ticket-booking-web

## 🏗️ Công nghệ sử dụng

Dự án này được chia thành hai phần (Backend, Frontend):

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

