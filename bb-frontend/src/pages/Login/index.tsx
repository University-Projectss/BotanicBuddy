import { apiClient } from "@/apiClient";
import { Button } from "@/components/ui/button";
import { CloseButton } from "@/components/ui/close-button";
import { Field } from "@/components/ui/field";
import {
  FileInput,
  FileUploadClearTrigger,
  FileUploadLabel,
  FileUploadRoot,
} from "@/components/ui/file-button";
import { InputGroup } from "@/components/ui/input-group";
import { Toaster, toaster } from "@/components/ui/toaster";
import { storage } from "@/firebase-config";
import {
  Box,
  ClientOnly,
  Flex,
  Image,
  Input,
  Skeleton,
  Spacer,
  Text,
} from "@chakra-ui/react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { useContext, useState } from "react";
import { useForm } from "react-hook-form";
import { FaUser } from "react-icons/fa6";
import { LuFileUp } from "react-icons/lu";
import { RiCharacterRecognitionFill, RiLockPasswordFill } from "react-icons/ri";
import { useNavigate } from "react-router-dom";
import { z } from "zod";
import flowerGirl from "../../assets/flower-girl.svg";
import logo from "../../assets/logo.png";
import { ColorModeToggle } from "../../color-mode-toggle";
import { AuthContext } from "@/App";

const loginSchema = z.object({
  email: z.string().email("Invalid email"),
  password: z.string().min(8, "Password must have at least 8 characters"),
  username: z.string().min(3, "Username must have at least 3 characters"),
  photoUrl: z.string().min(1, "Profile image is required"),
});

type LoginSchemaType = z.infer<typeof loginSchema>;

const Login = () => {
  const navigate = useNavigate();
  const { fetchAccount } = useContext(AuthContext);
  const [authType, setAuthType] = useState<"login" | "register">("login");

  const {
    register,
    formState: { isValid, errors },
    handleSubmit,
    setValue,
    watch,
    trigger,
  } = useForm<LoginSchemaType>({
    resolver: zodResolver(
      authType === "register"
        ? loginSchema
        : loginSchema.pick({ email: true, password: true })
    ),
    defaultValues: { email: "", password: "", username: "", photoUrl: "" },
    mode: "onBlur",
  });
  const email = watch("email");

  const authMutations = useMutation({
    mutationFn: async (formData: LoginSchemaType) => {
      if (authType === "login") {
        const { data } = await apiClient.post(
          "/auth/login",
          {},
          { auth: { username: formData.email, password: formData.password } }
        );
        return data;
      } else {
        await apiClient.post("/accounts", formData);
        const { data } = await apiClient.post(
          "/auth/login",
          {},
          { auth: { username: formData.email, password: formData.password } }
        );
        return data;
      }
    },
    onSuccess: async (token) => {
      localStorage.setItem("bbToken", token);
      await fetchAccount();
      navigate("/");
    },
    onError: (error) => {
      toaster.create({ description: error.message, type: "error" });
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
          width="60%"
          direction="column"
          gap={4}
          as="form"
          onSubmit={handleSubmit((data) => {
            authMutations.mutate(data);
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

          {authType === "register" && (
            <>
              <Field
                label="Username"
                invalid={!!errors.username}
                errorText={errors.username?.message}
              >
                <InputGroup
                  width="100%"
                  startElement={<RiCharacterRecognitionFill />}
                >
                  <Input
                    data-testid="username-input"
                    placeholder="Choose a username"
                    {...register("username")}
                  />
                </InputGroup>
              </Field>

              <Field
                invalid={!!errors.photoUrl}
                errorText={errors.photoUrl?.message}
              >
                <FileUploadRoot
                  maxFiles={1}
                  gap="1"
                  accept="image/*"
                  onFileAccept={async (details) => {
                    let documentRef = ref(
                      storage,
                      `${email}/${details.files[0].name}`
                    );

                    await uploadBytes(documentRef, details.files[0]);
                    let documentUrl = await getDownloadURL(documentRef);

                    setValue("photoUrl", documentUrl);
                    trigger();
                  }}
                >
                  <FileUploadLabel>Profile image</FileUploadLabel>
                  <InputGroup
                    w="full"
                    startElement={<LuFileUp />}
                    endElement={
                      <FileUploadClearTrigger asChild>
                        <CloseButton
                          me="-1"
                          size="xs"
                          variant="plain"
                          focusVisibleRing="inside"
                          focusRingWidth="2px"
                          pointerEvents="auto"
                          color="fg.subtle"
                        />
                      </FileUploadClearTrigger>
                    }
                  >
                    <FileInput />
                  </InputGroup>
                </FileUploadRoot>
              </Field>
            </>
          )}

          <Button
            data-testid="submit-button"
            colorPalette="green"
            type="submit"
            loading={authMutations.isPending}
            disabled={!isValid}
          >
            {authType[0].toUpperCase() + authType.slice(1) + "!"}
          </Button>

          <Spacer />

          <Text width="100%" textAlign="center">
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
