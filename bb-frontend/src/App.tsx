import { ChakraProvider, defaultSystem } from "@chakra-ui/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "next-themes";
import Router from "./router";
import useAuth from "./hooks/useAuth";
import { createContext } from "react";

export const AuthContext = createContext<ReturnType<typeof useAuth>>(null!);
export const queryClient = new QueryClient();

const App = () => {
  const authData = useAuth();
  return (
    <QueryClientProvider client={queryClient}>
      <ChakraProvider value={defaultSystem}>
        <ThemeProvider attribute="class" disableTransitionOnChange>
          <AuthContext.Provider value={authData}>
            <Router />
          </AuthContext.Provider>
        </ThemeProvider>
      </ChakraProvider>
    </QueryClientProvider>
  );
};

export default App;
