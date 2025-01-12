import { AuthContext } from "@/App";
import { ColorModeToggle } from "@/color-mode-toggle";
import { Avatar } from "@/components/ui/avatar";
import { Toaster } from "@/components/ui/toaster";
import { ClientOnly, Flex, Image, Skeleton, Button, Text } from "@chakra-ui/react";
import { useContext, useState } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";
import AddPlantButton from "./Home/AddPlantButton";

import {
  MenuRoot,
  MenuTrigger,
  MenuContent,
  MenuItem,
} from "@/components/ui/menu";

import {
  DialogRoot,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogBody,
  DialogFooter,
  DialogActionTrigger,
  DialogCloseTrigger,
} from "@/components/ui/dialog";

import { apiClient } from "@/apiClient";
import { Switch } from "@/components/ui/switch";


const MainLayout = () => {
  const { authenticatedAccount } = useContext(AuthContext);
  const navigate = useNavigate();

  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isEmailNotificationsEnabled, setIsEmailNotificationsEnabled] =
    useState(false); 

  const handleLogout = () => {
    localStorage.removeItem("bbToken");

    if (window.location.pathname !== "/login") {
      window.location.replace("/login");
    }
  };

  const handleDeleteAccount = async () => {
    try {
      await apiClient.delete("/accounts");
      localStorage.removeItem("bbToken");

      if (window.location.pathname !== "/login") {
        window.location.replace("/login");
      }
    } catch (err) {
      console.error("Failed to delete account:", err);
    }
  };

  const handleToggleEmailNotifications = async () => {
    try {
      await apiClient.post("/weather-alerts-toggle");

      setIsEmailNotificationsEnabled((prevState) => !prevState);
    } catch (err) {
      console.error("Failed to toggle email notifications:", err);
    }
  };

  return (
    <Flex
      direction="column"
      alignItems="center"
      justifyContent="flex-start"
      position="relative"
      px={6}
      width="100vw"
    >
      <Flex
        width="100%"
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        position="sticky"
        top={0}
        height="10vh"
      >
        <Image
          cursor="pointer"
          onClick={() => {
            navigate("/");
          }}
          src={logo}
          height="50px"
          objectFit="contain"
        />

        <Flex direction="row" alignItems="center" justify="center" gap={4}>
          <AddPlantButton />
          <Link
            to="/chat"
            style={{
              textDecoration:
                window.location.pathname === "/chat" ? "underline" : "",
            }}
          >
            Chat
          </Link>

          <Link
            to="/feed"
            style={{
              textDecoration:
                window.location.pathname === "/feed" ? "underline" : "",
            }}
          >
            Feed
          </Link>

          <ClientOnly fallback={<Skeleton w="10" h="10" rounded="md" />}>
            <ColorModeToggle />
          </ClientOnly>

          <MenuRoot>
            <MenuTrigger asChild>
              <Avatar
                src={authenticatedAccount?.photoUrl}
                name={authenticatedAccount?.username}
                cursor="pointer"
              />
            </MenuTrigger>
            <MenuContent>

            <MenuItem value="email-notifications" closeOnSelect={false}>
            <Flex
              alignItems="center"
              justifyContent="space-between"
              width="100%"
              gap={4} 
            >
              <Text>Receive Email Notifications</Text>
              <Switch
                checked={isEmailNotificationsEnabled}
                onChange={(e) => {
                  e.stopPropagation(); 
                  handleToggleEmailNotifications();
                }}
              />
            </Flex>
          </MenuItem>

              <MenuItem value="logout" onClick={handleLogout}>
                Logout
              </MenuItem>
              <MenuItem
                value="delete"
                color="red.500"
                onClick={() => setIsDeleteDialogOpen(true)}
              >
                Delete Account
              </MenuItem>

            </MenuContent>
          </MenuRoot>
        </Flex>
      </Flex>

      <Outlet />
      <Toaster />

      <DialogRoot role="alertdialog" open={isDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Account Deletion</DialogTitle>
          </DialogHeader>
          <DialogBody>
            <p>
              Are you sure you want to delete your account? This action cannot
              be undone, and all your data will be permanently removed.
            </p>
          </DialogBody>
          <DialogFooter>
            <DialogActionTrigger asChild>
              <Button
                variant="outline"
                onClick={() => setIsDeleteDialogOpen(false)}
              >
                Cancel
              </Button>
            </DialogActionTrigger>
            <Button
              colorScheme="red"
              onClick={async () => {
                await handleDeleteAccount();
                setIsDeleteDialogOpen(false);
              }}
            >
              Delete Account
            </Button>
          </DialogFooter>
          <DialogCloseTrigger />
        </DialogContent>
      </DialogRoot>
    </Flex>
  );
};

export default MainLayout;