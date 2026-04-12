import { Redirect } from 'expo-router';

export default function Index() {
  const isLoggedIn = true;

  if (!isLoggedIn) {
    return <Redirect href="/(auth)/login" />;
  }

  return <Redirect href="/(tabs)/home" />;
}