import { useParams } from "react-router-dom";
import { Box, Button, Flex, Image, Text } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/apiClient";

interface PlantDetailResponse {
  id: string;
  commonName: string;
  scientificName: string;
  family: string;
  photoUrl: string;
  uploadDate: string;
}

const PlantDetail = () => {
  const { plantId } = useParams<{ plantId: string }>();

  const { data, isFetching, isError } = useQuery({
    queryKey: ["plantDetail", plantId],
    queryFn: async () => {
      const { data } = await apiClient.get<PlantDetailResponse>(
        `/plants/${plantId}`
      );
      return data;
    },
  });

  if (isFetching) {
    return (
      <Flex justifyContent="center" alignItems="center" height="100vh">
        <Text>Loading...</Text>
      </Flex>
    );
  }

  if (isError || !data) {
    return (
      <Flex justifyContent="center" alignItems="center" height="100vh">
        <Text color="red.500" fontWeight={600}>
          Failed to load plant details.
        </Text>
      </Flex>
    );
  }

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
          <Box>
            <Text fontSize="lg" mb={2}>
              <strong>Scientific Name:</strong> {data.scientificName}
            </Text>
            <Text fontSize="lg" mb={2}>
              <strong>Family:</strong> {data.family}
            </Text>
            <Text fontSize="lg" mb={2}>
              <strong>Upload Date:</strong> {new Date(data.uploadDate).toDateString()}
            </Text>
          </Box>
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
              onClick={() => console.log("Plant watered successfully")}
            >
              💧 Water
            </Button>
            <Button
              onClick={() => console.log("Soil changed successfully")}
            >
              🌱 Change Soil
            </Button>
            <Button
              onClick={() => console.log("Plant deleted successfully")}
            >
              🗑️ Delete
            </Button>
          </Flex>
        </Flex>
      </Flex>

      <Box mt={12} width="100%" maxWidth="1200px">
        <Text fontSize="2xl" fontWeight={600} mb={4}>
          History
        </Text>
        <Text fontStyle="italic" color="gray.500">
          Plant history will be displayed here.
        </Text>
      </Box>
    </Flex>
  );
};

export default PlantDetail;
