import {
  Box,
  ClientOnly,
  Flex,
  Skeleton,
  Text,
  Image,
  Spacer,
  Input,
} from "@chakra-ui/react";
import { Field } from "@/components/ui/field";
import { ColorModeToggle } from "../../color-mode-toggle";
import logo from "../../assets/logo.png";
import flowerGirl from "../../assets/flower-girl.svg";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { InputGroup } from "@/components/ui/input-group";
import { FaUser } from "react-icons/fa6";
import { RiLockPasswordFill } from "react-icons/ri";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Toaster, toaster } from "@/components/ui/toaster";
import { useNavigate } from "react-router-dom";
import { apiClient } from "@/apiClient";
import { Button } from "@/components/ui/button";

const loginSchema = z.object({
  email: z.string().email("Invalid email"),
  password: z.string().min(8, "Password must have at least 8 characters"),
});

type LoginSchemaType = z.infer<typeof loginSchema>;

const Login = () => {
  const navigate = useNavigate();
  const [authType, setAuthType] = useState<"login" | "register">("login");

  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm<LoginSchemaType>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

  const loginMutation = useMutation({
    mutationFn: async (formData: LoginSchemaType) => {
      const res = await apiClient.post("/auth/login", formData);
      return res;
    },
    onSuccess: () => {
      navigate("/");
    },
    onError: () => {
      toaster.create({ description: "Could not login", type: "error" });
    },
  });

  return (
    <Flex height="100vh">
      <Flex
        direction="column"
        alignItems="center"
        justifyContent="center"
        flex={1}
      >
        <Flex
          direction="column"
          gap={4}
          as="form"
          onSubmit={handleSubmit((data) => {
            loginMutation.mutate(data);
          })}
        >
          <Text textStyle="5xl">
            {authType[0].toUpperCase() + authType.slice(1)}
          </Text>

          <Field
            label="Email"
            invalid={!!errors.email}
            errorText={errors.email?.message}
          >
            <InputGroup width="100%" startElement={<FaUser />}>
              <Input
                data-testid="email-input"
                {...register("email")}
                placeholder="Enter your email"
              />
            </InputGroup>
          </Field>

          <Field
            label="Password"
            invalid={!!errors.password}
            errorText={errors.password?.message}
          >
            <InputGroup width="100%" startElement={<RiLockPasswordFill />}>
              <Input
                data-testid="password-input"
                {...register("password")}
                type="password"
                placeholder="Enter your password"
              />
            </InputGroup>
          </Field>
          <Button
            data-testid="submit-button"
            colorPalette="green"
            type="submit"
            loading={loginMutation.isPending}
          >
            {authType[0].toUpperCase() + authType.slice(1) + "!"}
          </Button>

          <Spacer />

          <Text>
            {authType === "login"
              ? "Don't have an account?"
              : "Already have an account?"}{" "}
            <Text
              data-testid="switch-auth-button"
              as="span"
              fontWeight="bold"
              textDecoration="underline"
              cursor="pointer"
              onClick={() => {
                setAuthType(authType === "login" ? "register" : "login");
              }}
            >
              {authType === "login" ? "Create one!" : "Login!"}
            </Text>
          </Text>
        </Flex>
      </Flex>

      <Flex
        direction="column"
        alignItems="center"
        justifyContent="space-between"
        flex={1}
      >
        <Spacer />
        <Image src={logo} height="200px" objectFit="contain" />
        <Spacer />
        <Image src={flowerGirl} height="50vh" objectFit="contain" />
      </Flex>

      <Toaster />
      <Box pos="absolute" top="4" right="4">
        <ClientOnly fallback={<Skeleton w="10" h="10" rounded="md" />}>
          <ColorModeToggle />
        </ClientOnly>
      </Box>
    </Flex>
  );
};

export default Login;
