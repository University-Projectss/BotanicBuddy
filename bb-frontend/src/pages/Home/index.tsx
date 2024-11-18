import {
  Box,
  Center,
  ClientOnly,
  Skeleton,
  Spinner,
  useDisclosure,
} from "@chakra-ui/react";
import { ColorModeToggle } from "../../color-mode-toggle";
import { Button } from "@/components/ui/button";
import {
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  FileUploadDropzone,
  FileUploadList,
  FileUploadRoot,
} from "@/components/ui/file-button";
import { FileAcceptDetails } from "node_modules/@chakra-ui/react/dist/types/components/file-upload/namespace";
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "@/firebase-config";
import { useContext, useState } from "react";
import { AuthContext } from "@/App";
import { apiClient } from "@/apiClient";
import { toaster, Toaster } from "@/components/ui/toaster";

const Home = () => {
  const { authenticatedAccount } = useContext(AuthContext);
  const { open, onOpen, onClose } = useDisclosure();
  const [isProcessingImage, setIsProcessingImage] = useState<boolean>(false);
  const handleImageUpload = async (details: FileAcceptDetails) => {
    try {
      setIsProcessingImage(true);

      const image = details.files[0];
      const params = new URLSearchParams({
        "nb-results": "2",
        lang: "ro",
        "api-key": import.meta.env.VITE_PLANT_API_KEY,
      });
      const formData = new FormData();
      formData.append("images", image);
      const res = await fetch(
        `https://my-api.plantnet.org/v2/identify/all?${params.toString()}`,
        {
          method: "POST",
          body: formData,
          headers: {},
        }
      );
      const data = await res.json();
      console.log(data);

      let documentRef = ref(
        storage,
        `${authenticatedAccount?.email}/${image.name}`
      );

      await uploadBytes(documentRef, image);
      let documentUrl = await getDownloadURL(documentRef);

      await apiClient.post("/plants", {
        photoUrl: documentUrl,
        commonName: data.results[0].species.commonNames[0],
        scientificName: data.results[0].species.scientificName,
        family: data.results[0].species.family.scientificName,
      });
      toaster.create({
        description: "Plant added successfully!",
        type: "success",
      });
      onClose();
    } catch (error) {
      console.log(error);
      toaster.create({
        description: (error as Error).message,
        type: "error",
      });
    }
    setIsProcessingImage(false);
  };

  return (
    <Box textAlign="center" fontSize="xl" pt="30vh">
      <DialogRoot placement="center" motionPreset="slide-in-bottom" open={open}>
        <DialogTrigger asChild>
          <Button colorPalette="green" onClick={onOpen}>
            + Add Plant
          </Button>
        </DialogTrigger>

        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add plant</DialogTitle>
            <DialogCloseTrigger onClick={onClose} />
          </DialogHeader>

          <DialogBody>
            {isProcessingImage ? (
              <Box pos="absolute" inset="0" bg="bg/80">
                <Center h="full">
                  <Spinner color="teal.500" />
                </Center>
              </Box>
            ) : (
              <FileUploadRoot
                accept={["image/png", "image/jpeg"]}
                maxW="xl"
                alignItems="stretch"
                maxFiles={1}
                onFileAccept={handleImageUpload}
              >
                <FileUploadDropzone
                  label="Drag and drop here to upload"
                  description=".png, .jpeg up to 5MB"
                />
                <FileUploadList />
              </FileUploadRoot>
            )}
          </DialogBody>
        </DialogContent>
      </DialogRoot>

      <Toaster />
      <Box pos="absolute" top="4" right="4">
        <ClientOnly fallback={<Skeleton w="10" h="10" rounded="md" />}>
          <ColorModeToggle />
        </ClientOnly>
      </Box>
    </Box>
  );
};

export default Home;
