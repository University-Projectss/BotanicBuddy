import "@testing-library/jest-dom";
import { ChakraProvider, defaultSystem } from "@chakra-ui/react";
import { ThemeProvider } from "next-themes";
import { ReactNode } from "react";
import { MemoryRouter } from "react-router-dom";
import { vi } from "vitest";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthContext } from "@/App";

//A wrapper with all the providers needed in order to render the pages for testing
const TestProviders = (props: { children: ReactNode }) => {
  const { children } = props;

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        staleTime: 5 * (60 * 1000), // 5 mins
      },
    },
  });

  Object.defineProperty(window, "matchMedia", {
    writable: true,
    value: vi.fn().mockImplementation((query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  });

  vi.mock("react-router-dom", async (importOriginal) => ({
    ...(await importOriginal()),
    useNavigate: vi.fn(),
  }));

  vi.mock("firebase/storage", () => ({
    ref: vi.fn(),
    uploadBytes: vi.fn(),
    getDownloadURL: vi.fn(),
    getStorage: vi.fn(),
  }));

  vi.mock("../components/ui/toaster", () => ({
    toaster: {
      create: vi.fn(),
    },
    Toaster: () => <div data-testid="toaster" />,
  }));

  return (
    <QueryClientProvider client={queryClient}>
      <AuthContext.Provider
        value={{
          authenticatedAccount: null,
          setAuthenticatedAccount: vi.fn(),
          fetchAccount: vi.fn(),
        }}
      >
        <ChakraProvider value={defaultSystem}>
          <ThemeProvider attribute="class" disableTransitionOnChange>
            <MemoryRouter>{children}</MemoryRouter>
          </ThemeProvider>
        </ChakraProvider>
      </AuthContext.Provider>
    </QueryClientProvider>
  );
};

export default TestProviders;
