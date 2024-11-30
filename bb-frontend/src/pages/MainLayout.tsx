import { AuthContext } from "@/App";
import { ColorModeToggle } from "@/color-mode-toggle";
import { Avatar } from "@/components/ui/avatar";
import { Toaster } from "@/components/ui/toaster";
import { ClientOnly, Flex, Image, Skeleton } from "@chakra-ui/react";
import { useContext } from "react";
import { Outlet } from "react-router-dom";
import logo from "../assets/logo.png";

const MainLayout = () => {
  const { authenticatedAccount } = useContext(AuthContext);
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
        height="100px"
      >
        <Image src={logo} height="50px" objectFit="contain" />

        <Flex direction="row" alignItems="center" justify="center" gap={4}>
          <ClientOnly fallback={<Skeleton w="10" h="10" rounded="md" />}>
            <ColorModeToggle />
          </ClientOnly>

          <Avatar
            src={authenticatedAccount?.photoUrl}
            name={authenticatedAccount?.username}
            cursor="pointer"
          />
        </Flex>
      </Flex>
      <Outlet />
      <Toaster />
    </Flex>
  );
};

export default MainLayout;
