import "@testing-library/jest-dom";
import { ChakraProvider, defaultSystem } from "@chakra-ui/react";
import { ThemeProvider } from "next-themes";
import { ReactNode } from "react";
import { MemoryRouter } from "react-router-dom";
import { vi } from "vitest";

//A wrapper with all the providers needed in order to render the pages for testing
const TestProviders = (props: { children: ReactNode }) => {
  const { children } = props;

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

  vi.mock("../components/ui/toaster", () => ({
    toaster: {
      create: vi.fn(),
    },
  }));

  return (
    <ChakraProvider value={defaultSystem}>
      <ThemeProvider attribute="class" disableTransitionOnChange>
        <MemoryRouter>{children}</MemoryRouter>
      </ThemeProvider>
    </ChakraProvider>
  );
};

export default TestProviders;
