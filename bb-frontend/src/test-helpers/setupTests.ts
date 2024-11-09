import { cleanup } from "@testing-library/react";
import { afterAll, afterEach, beforeAll, vi } from "vitest";
import server from "./server";

beforeAll(() => {
  server.listen();
  vi.stubEnv("VITE_BASE_URL", "http://127.0.0.1/api");
});
afterEach(() => {
  cleanup();
  server.resetHandlers();
});
afterAll(() => {
  vi.unstubAllEnvs();
  server.close();
});
