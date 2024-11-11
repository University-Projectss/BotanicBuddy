import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import TestProviders from "../../test-helpers/TestProviders";
import Login from ".";
import { useNavigate } from "react-router-dom";
import userEvent from "@testing-library/user-event";
import server from "../../test-helpers/server";
import { http, HttpResponse } from "msw";
import { toaster } from "../../components/ui/toaster";

describe("Login tests", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("Should navigate to '/' after login finish successfully", async () => {
    const navigate = vi.fn();
    (useNavigate as Mock).mockReturnValue(navigate);

    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    server.use(
      http.post(`*/auth/login`, () => {
        return HttpResponse.json({});
      })
    );

    const emailInput = screen.getByTestId("email-input");
    const passwordInput = screen.getByTestId("password-input");

    await userEvent.type(emailInput, "robert@email.com");
    await userEvent.type(passwordInput, "Abcd1234!");

    const submitButton = screen.getByTestId("submit-button");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(navigate).toHaveBeenCalledWith("/");
    });
  });

  it("Display errors on invalid input", async () => {
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    const submitButton = screen.getByTestId("submit-button");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Invalid email")).toBeInTheDocument();
      expect(
        screen.getByText("Password must have at least 8 characters")
      ).toBeInTheDocument();
    });
  });

  it("Display toaster when login fail", async () => {
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    server.use(
      http.post(`*/auth/login`, () => {
        return HttpResponse.json({}, { status: 400 });
      })
    );

    await waitFor(() => {
      expect(toaster.create).toHaveBeenCalled();
    });
  });

  it("Display register message after user switch", async () => {
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    const switchAuthButton = screen.getByTestId("switch-auth-button");
    await userEvent.click(switchAuthButton);

    await waitFor(() => {
      expect(screen.getByText("Register")).toBeInTheDocument();
    });
  });
});
