import { apiClient } from "@/apiClient";
import {
  PaginationItems,
  PaginationNextTrigger,
  PaginationPrevTrigger,
  PaginationRoot,
} from "@/components/ui/pagination";
import { Plant, PlantsResponse } from "@/types/plant";
import {
  Box,
  Flex,
  HStack,
  Image,
  Separator,
  Spinner,
  Text,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const PaginatedSection = () => {
  const [plantsList, setPlantsList] = useState<Plant[]>([]);
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);
  const pageSize = 4;

  const { isFetching, refetch } = useQuery({
    queryKey: ["plants"],
    queryFn: async () => {
      const { data } = await apiClient.get<PlantsResponse>("/plants", {
        params: { pageNumber: pageNumber - 1, pageSize },
      });

      setPlantsList(data.plants);
      setTotalElements(data.totalElements);

      return data;
    },
  });

  useEffect(() => {
    refetch();
  }, [pageNumber]);

  return plantsList.length ? (
    <Flex direction="column" gap={6} alignItems="center" width="100%">
      <Text fontWeight={600} fontSize="3xl" textAlign="left" width="100%">
        My Plants
      </Text>
      <Separator />
      <Flex
        direction="row"
        alignItems="center"
        justifyContent="center"
        gap={16}
        flexWrap="wrap"
      >
        {plantsList.map((plant) => (
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
            <Image
              src={plant.photoUrl}
              width={200}
              height={200}
              _hover={{ width: 220, height: 220 }}
              transition="ease-in"
              transitionDuration="0.1s"
            />
            <Flex
              py={2}
              position="absolute"
              bottom={0}
              width="100%"
              bgColor="rgba(0, 0, 0, 0.5)"
              alignItems="center"
              justifyContent="center"
            >
              <Text color="white" fontWeight={600}>
                {plant.commonName}
              </Text>
            </Flex>
          </Box>
        ))}
      </Flex>
      <PaginationRoot
        count={totalElements}
        pageSize={pageSize}
        defaultPage={1}
        page={pageNumber}
        onPageChange={(e) => setPageNumber(e.page)}
      >
        <HStack>
          <PaginationPrevTrigger />
          <PaginationItems />
          <PaginationNextTrigger />
        </HStack>
      </PaginationRoot>
    </Flex>
  ) : isFetching ? (
    <Spinner />
  ) : (
    <Text>There is no plant here</Text>
  );
};

export default PaginatedSection;
