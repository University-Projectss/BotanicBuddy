import { useParams } from "react-router-dom";
import { Box, Button, Flex, Icon, Image, Text } from "@chakra-ui/react";
import { apiClient } from "@/apiClient";
import { FiInfo } from "react-icons/fi";
import { Tooltip } from "@/components/ui/tooltip";
import { PlantDetailResponse } from "@/types/plant";

import {
  TimelineConnector,
  TimelineContent,
  TimelineDescription,
  TimelineItem,
  TimelineRoot,
  TimelineTitle,
} from "@/components/ui/timeline";

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

import { LuLeaf, LuGlassWater, LuDelete, LuCheck } from "react-icons/lu";
import { useEffect, useState } from "react";

const wateringMessages = [
  "Your plant got a nice drink! 💦",
  "Watering complete! Your plant is happy and hydrated! 💧",
  "Your plant just had a refreshing drink! 💧",
  "Your plant is watered and ready to grow! 🌿",
  "Your plant is feeling great after a good watering! 🌸",
  "Your plant just enjoyed a nice drink of water! 💧",
  "Watering done! Your plant is all set! 🌱",
];

const soilMessages = [
  "Soil changed! Your plant is ready for new growth! 🌱",
  "Fresh soil for your plant! It's thriving! 🌿",
  "A fresh layer of soil for your plant! 🌾",
  "Soil change complete! Your plant is feeling better already! 🌸",
  "Soil refreshed! Your plant is ready to grow! 🌻",
  "Soil change done! Your plant is set for the next phase! 🌼",
  "Your plant got fresh soil! Time for new growth! 🌱",
];

const getRandomMessage = (messages: string[]) => {
  const index = Math.floor(Math.random() * messages.length);
  return messages[index];
};

const getTimelineData = (action: string) => {
  switch (action) {
    case "WATER":
      return {
        icon: <LuGlassWater />,
        title: getRandomMessage(wateringMessages), 
      };
    case "CHANGE_SOIL":
      return {
        icon: <LuLeaf />,
        title: getRandomMessage(soilMessages),
      };
    case "DELETE":
      return {
        icon: <LuDelete />,
        title: "Plant Deleted",
      };
    default:
      return {
        icon: <LuCheck />,
        title: action,
      };
  }
};

export default function PlantDetail() {
  const { plantId } = useParams<{ plantId: string }>();

  const [plantData, setPlantData] = useState<PlantDetailResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    const fetchPlant = async () => {
      try {
        const response = await apiClient.get<PlantDetailResponse>(`/plants/${plantId}`);
        setPlantData(response.data);
      } catch (err) {
        console.error(err);
        setError("Failed to load plant details.");
      } finally {
        setLoading(false);
      }
    };

    fetchPlant();
  }, [plantId]);

  const handleAction = async (actionType: string) => {
    try {
      await apiClient.patch(`/plants/${plantId}`, { actionType });
      console.log(`Action "${actionType}" completed successfully.`);

      const response = await apiClient.get<PlantDetailResponse>(`/plants/${plantId}`);
      setPlantData(response.data);
    } catch (err) {
      console.error(`Action "${actionType}" failed:`, err);
    }
  };

  if (loading) {
    return (
      <Flex justifyContent="center" alignItems="center" height="100vh">
        <Text>Loading...</Text>
      </Flex>
    );
  }

  if (error || !plantData) {
    return (
      <Flex justifyContent="center" alignItems="center" height="100vh">
        <Text color="red.500" fontWeight={600}>
          {error || "Failed to load plant details."}
        </Text>
      </Flex>
    );
  }

  const data = plantData;
  const isArchived = data.isArchived;

  return (
    <Flex
      direction="column"
      align="center"
      justify="center"
      width="100%"
      padding={8}
    >
      <Flex
        direction="row"
        alignItems="flex-start"
        justifyContent="space-between"
        width="100%"
        maxWidth="1200px"
        gap={8}
      >
        <Box flex={1}>
          <Text fontSize="4xl" fontWeight={700} mb={4}>
            {data.commonName}
          </Text>
          <Flex direction="column" gap={2} fontSize="lg">
            <Text>
              <strong>Scientific Name:</strong> {data.scientificName}
            </Text>
            <Text>
              <strong>Family:</strong> {data.family}
            </Text>
            <Text>
              <strong>Upload Date:</strong>{" "}
              {new Date(data.uploadDate).toDateString()}
            </Text>
            <Flex gap={2} alignItems="center">
              <Text>
                <strong>Water frequency:</strong> {data.wateringFrequency} days
              </Text>
              <Tooltip content="Number of days between watering">
                <Icon color="gray.500" cursor="pointer">
                  <FiInfo />
                </Icon>
              </Tooltip>
            </Flex>
            <Text>
              <strong>Light:</strong> {data.light}
            </Text>
            <Text>
              <strong>Soil:</strong> {data.soil}
            </Text>
            <Text>
              <strong>Temperature:</strong> {data.temperature} °C
            </Text>
            <Text>
              <strong>Care Recommendation:</strong> {data.careRecommendation}
            </Text>
          </Flex>
        </Box>

        <Flex direction="column" align="center" gap={4}>
          <Image
            src={data.photoUrl}
            alt={data.commonName}
            borderRadius="20px"
            boxShadow="md"
            width="300px"
            height="300px"
            mb={4}
          />
          <Flex direction="row" gap={4}>
            <Button
              disabled={isArchived}
              onClick={() => handleAction("WATER")}
            >
              💧 Water
            </Button>
            <Button
              disabled={isArchived}
              onClick={() => handleAction("CHANGE_SOIL")}
            >
              🌱 Change Soil
            </Button>

            <DialogRoot role="alertdialog" open={isOpen}>
              <DialogTrigger asChild>
                <Button
                  disabled={isArchived}
                  colorScheme="red"
                  onClick={() => setIsOpen(true)}
                >
                  🗑️ Archive
                </Button>
              </DialogTrigger>

              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Are you sure?</DialogTitle>
                </DialogHeader>
                <DialogBody>
                  <p>
                    This action cannot be undone.
                    <br />
                    It will archive your plant and remove it from the active list.
                  </p>
                </DialogBody>
                <DialogFooter>
                  <DialogActionTrigger asChild>
                    <Button variant="outline">Cancel</Button>
                  </DialogActionTrigger>

                  <Button
                    colorScheme="red"
                    onClick={async () => {
                      await handleAction("ARCHIVE");
                      setIsOpen(false);
                    }}
                  >
                    Archive
                  </Button>
                </DialogFooter>
                <DialogCloseTrigger />
              </DialogContent>
            </DialogRoot>
          </Flex>
        </Flex>
      </Flex>

      <Box mt={12} width="100%" maxWidth="1200px">
        <Text fontSize="2xl" fontWeight={600} mb={4}>
          History
        </Text>
        <TimelineRoot maxW="600px">
          {data.careHistory?.map((item, index) => {
            const { icon, title } = getTimelineData(item.action);
            return (
              <TimelineItem key={index}>
                <TimelineConnector>{icon}</TimelineConnector>
                <TimelineContent>
                  <TimelineTitle>{title}</TimelineTitle>
                  <TimelineDescription>
                    {item.date} at {item.time}
                  </TimelineDescription>
                </TimelineContent>
              </TimelineItem>
            );
          })}
        </TimelineRoot>
      </Box>
    </Flex>
  );
}
