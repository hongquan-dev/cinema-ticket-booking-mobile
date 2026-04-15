import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { useEffect } from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import "./global.css";

SplashScreen.preventAutoHideAsync();

export default function Layout() {
  useEffect(() => {
    SplashScreen.hideAsync();
  }, []);

  return (
    <SafeAreaProvider>
      <Stack screenOptions={{ headerShown: false }}>
        {/* Group Tab */}
        <Stack.Screen name="(tabs)" />

        {/* Group Auth (Login/Signup) */}
        <Stack.Screen name="(auth)/login" />
        <Stack.Screen name="(auth)/signup" />

        {/* Group Screens */}
        <Stack.Screen name="(details)/moviedetails" />
        <Stack.Screen name="(screens)/selectshowtime" />
        <Stack.Screen name="(screens)/booking" />
        <Stack.Screen name="(screens)/payment" />
        <Stack.Screen name="(details)/newsdetails" />

        {/* Group Account */}
        <Stack.Screen name="(account)/notifications" />
        <Stack.Screen name="(details)/notificationdetails" />
        <Stack.Screen name="(account)/profile" />
        <Stack.Screen name="(account)/changepassword" />
        <Stack.Screen name="(account)/terms_policy" />
        <Stack.Screen name="(account)/business_info" />

      </Stack>
    </SafeAreaProvider>
  );
}