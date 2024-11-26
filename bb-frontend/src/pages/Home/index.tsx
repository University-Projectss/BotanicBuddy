import { AuthContext, queryClient } from "@/App";
import { apiClient } from "@/apiClient";
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
import { toaster } from "@/components/ui/toaster";
import { storage } from "@/firebase-config";
import { PlantsResponse } from "@/types/plant";
import {
  Box,
  Center,
  Flex,
  Image,
  Spinner,
  Text,
  useDisclosure,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { FileAcceptDetails } from "node_modules/@chakra-ui/react/dist/types/components/file-upload/namespace";
import { useContext, useState } from "react";
import { Link } from "react-router-dom";

const Home = () => {
  const { authenticatedAccount } = useContext(AuthContext);
  const { open, onOpen, onClose } = useDisclosure();
  const [isProcessingImage, setIsProcessingImage] = useState<boolean>(false);

  const { data: plantsResponse } = useQuery({
    queryKey: ["plants"],
    queryFn: async () => {
      const { data } = await apiClient.get<PlantsResponse>("/plants", {
        params: { pageNumber: 0, pageSize: 100 },
      });

      return data;
    },
  });

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

      if (data.error) {
        toaster.create({
          description: data.message,
          type: "error",
        });
      } else {
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
        queryClient.invalidateQueries({ queryKey: ["plants"] });
      }
      onClose();
    } catch (error) {
      toaster.create({
        description: (error as Error).message,
        type: "error",
      });
    }
    setIsProcessingImage(false);
  };

  return (
    <Flex direction="column" gap={10}>
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

      <Flex direction="row" gap={32} flexWrap="wrap">
        {plantsResponse &&
          plantsResponse.plants.map((plant) => (
            <Box
              key={plant.id}
              position="relative"
              width={200}
              height={200}
              borderRadius={20}
              overflow="hidden"
              as={Link}
              //@ts-expect-error
              to={`/plant/${plant.id}`}
            >
              <Image src={plant.photoUrl} width={200} height={200} />
              <Flex
                py={2}
                position="absolute"
                bottom={0}
                width="100%"
                bgColor="rgba(0, 0, 0, 0.5)"
                alignItems="center"
                justifyContent="center"
              >
                <Text color="white">{plant.commonName}</Text>
              </Flex>
            </Box>
          ))}
      </Flex>
    </Flex>
  );
};

export default Home;
