import { apiClient } from "@/apiClient";
import { queryClient } from "@/App";
import { Button } from "@/components/ui/button";
import {
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Field } from "@/components/ui/field";
import {
  FileUploadList,
  FileUploadRoot,
  FileUploadTrigger,
} from "@/components/ui/file-button";
import { toaster } from "@/components/ui/toaster";
import { IconButton, Stack, Textarea, useDisclosure } from "@chakra-ui/react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { HiUpload } from "react-icons/hi";
import { IoIosAdd } from "react-icons/io";
import { z } from "zod";
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "@/firebase-config";
import useAuth from "@/hooks/useAuth";

const newPostSchema = z.object({
  photoUrl: z.string().min(1, "Image is required"),
  content: z.string().min(1, "Description is required"),
});

type NewPostSchemaType = z.infer<typeof newPostSchema>;

const NewPostButton = () => {
  const { authenticatedAccount } = useAuth();
  const { open, onOpen, onClose } = useDisclosure();

  const {
    register,
    formState: { errors },
    handleSubmit,
    setValue,
    trigger,
  } = useForm<NewPostSchemaType>({
    resolver: zodResolver(newPostSchema),
    defaultValues: { photoUrl: "", content: "" },
    mode: "onSubmit",
  });

  const newPostMutation = useMutation({
    mutationFn: async (formData: NewPostSchemaType) => {
      await apiClient.post("/posts", {
        ...formData,
        title: "",
      });
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["feed"] });
      onClose();
    },
    onError: (error: any) => {
      toaster.create({
        description: error.response.data.message,
        type: "error",
      });
    },
  });

  return (
    <DialogRoot placement="center" motionPreset="slide-in-bottom" open={open}>
      <DialogTrigger asChild>
        <IconButton
          colorPalette="green"
          rounded="full"
          position="absolute"
          bottom={10}
          right={10}
          onClick={onOpen}
          size="2xl"
        >
          <IoIosAdd scale={10} />
        </IconButton>
      </DialogTrigger>

      <DialogContent position="absolute">
        <DialogHeader>
          <DialogTitle>New post</DialogTitle>
          <DialogCloseTrigger onClick={onClose} />
        </DialogHeader>

        <DialogBody>
          <Stack gap={4}>
            <Field
              label="Image"
              invalid={!!errors.photoUrl}
              errorText={errors.photoUrl?.message}
            >
              <FileUploadRoot
                accept={["image/png", "image/jpeg"]}
                maxW="lg"
                alignItems="stretch"
                maxFiles={1}
                onFileAccept={async (details) => {
                  let documentRef = ref(
                    storage,
                    `${authenticatedAccount?.email}/post-${details.files[0].name}`
                  );

                  await uploadBytes(documentRef, details.files[0]);
                  let documentUrl = await getDownloadURL(documentRef);

                  setValue("photoUrl", documentUrl);
                  trigger();
                }}
              >
                <FileUploadTrigger asChild>
                  <Button variant="outline" size="sm" width="fit-content">
                    <HiUpload /> Upload file
                  </Button>
                </FileUploadTrigger>
                <FileUploadList />
              </FileUploadRoot>
            </Field>

            <Field
              label="Description"
              invalid={!!errors.content}
              errorText={errors.content?.message}
            >
              <Textarea
                colorPalette="green"
                placeholder="Write here a short description"
                maxH={200}
                minH={100}
                {...register("content")}
              />
            </Field>
          </Stack>
        </DialogBody>

        <DialogFooter>
          <Button
            colorPalette="green"
            onClick={handleSubmit((data) => {
              trigger();
              newPostMutation.mutate(data);
              console.log(data);
            })}
          >
            Post
          </Button>
        </DialogFooter>
      </DialogContent>
    </DialogRoot>
  );
};

export default NewPostButton;
