# Mobile Cinema Ticket Booking App

*[Đọc phiên bản Tiếng Việt tại đây](README.vi.md)*

Welcome to the **Mobile Cinema Ticket Booking App**! This is a mobile application designed for booking cinema tickets, and this mobile app is exclusively for Customers. For the complete system including both Admin and Customer views, see: https://github.com/hongquan-dev/cinema-ticket-booking-web

## 🏗️ Technologies Used

This project is divided into two parts (Backend, Frontend):

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
