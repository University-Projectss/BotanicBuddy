import server from "@/test-helpers/server";
import TestProviders from "@/test-helpers/TestProviders";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { describe, expect, it, Mock, vi } from "vitest";
import Login from ".";
import { useNavigate } from "react-router-dom";
import { getDownloadURL } from "firebase/storage";
import { toaster } from "@/components/ui/toaster";

describe("Login page", () => {
  it("perform login flow", async () => {
    const navigate = vi.fn();
    (useNavigate as Mock).mockReturnValue(navigate);
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    server.use(
      http.post("*/auth/login", () => {
        return HttpResponse.json({});
      })
    );

    const emailInput = screen.getByTestId("email-input");
    const passwordInput = screen.getByTestId("password-input");
    await userEvent.type(emailInput, "robert@cst.ro");
    await userEvent.type(passwordInput, "123456789");

    const submitButton = screen.getByTestId("submit-button");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(navigate).toHaveBeenCalledWith("/");
    });
  });

  it("perform register flow", async () => {
    const navigate = vi.fn();
    (useNavigate as Mock).mockReturnValue(navigate);
    (getDownloadURL as Mock).mockImplementation(() => "some-url");
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    server.use(
      http.post("*/accounts", () => {
        return HttpResponse.json({});
      })
    );

    server.use(
      http.post("*/auth/login", () => {
        return HttpResponse.json({});
      })
    );

    const changeAuthFlowButton = screen.getByTestId("switch-auth-button");
    await userEvent.click(changeAuthFlowButton);

    const emailInput = screen.getByTestId("email-input");
    await userEvent.type(emailInput, "robert@cst.ro");

    const passwordInput = screen.getByTestId("password-input");
    await userEvent.type(passwordInput, "123456789");

    const usernameInput = screen.getByTestId("username-input");
    await userEvent.type(usernameInput, "Username");

    const fileInput = screen.getByLabelText(/Profile image/i);
    const file = new File(["mock content"], "example.png", {
      type: "image/png",
    });

    await userEvent.upload(fileInput, file);

    const submitButton = screen.getByTestId("submit-button");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(navigate).toHaveBeenCalledWith("/");
    });
  });

  it("shows toaster when endpoint fails", async () => {
    render(
      <TestProviders>
        <Login />
      </TestProviders>
    );

    server.use(
      http.post("*/auth/login", () => {
        return HttpResponse.json({}, { status: 400 });
      })
    );

    const emailInput = screen.getByTestId("email-input");
    const passwordInput = screen.getByTestId("password-input");
    await userEvent.type(emailInput, "robert@cst.ro");
    await userEvent.type(passwordInput, "123456789");

    const submitButton = screen.getByTestId("submit-button");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(toaster.create).toHaveBeenCalled();
    });
  });
});
