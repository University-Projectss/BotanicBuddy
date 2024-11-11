import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./src/test-helpers/setupTests.ts",
    coverage: {
      reporter: ["text", "lcov", "html"],
      include: ["src/**/*.{ts,tsx}"],
      exclude: [
        "src/**/*.test.{ts,tsx}",
        "src/**/__tests__/**",
        "src/**/__mocks__/**",
        "src/test-helpers/**",
        "src/router/**",
        "src/color-mode-toggle.tsx",
        "src/main.tsx",
        "node_modules/**",
        "dist/**",
      ],
    },
  },
  preview: {
    port: 5173,
  },
});
