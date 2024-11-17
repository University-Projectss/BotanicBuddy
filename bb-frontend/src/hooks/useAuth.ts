import { apiClient } from "@/apiClient";
import { AuthenticatedAccount } from "@/types";
import { useEffect, useState } from "react";

const useAuth = () => {
  const [authenticatedAccount, setAuthenticatedAccount] =
    useState<AuthenticatedAccount | null>(null);

  const fetchAccount = async () => {
    try {
      const token = localStorage.getItem("bbToken");

      if (!token && window.location.pathname !== "/login") {
        window.location.replace("/login");
        return;
      }
      const { data } = await apiClient.get<AuthenticatedAccount>("/profile");
      setAuthenticatedAccount(data);
    } catch (error) {
      console.log(error);
      setAuthenticatedAccount(null);
    }
  };

  useEffect(() => {
    fetchAccount();
  }, []);

  return { authenticatedAccount, setAuthenticatedAccount, fetchAccount };
};

export default useAuth;
