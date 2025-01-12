import { useState, useEffect } from "react"
import { Link } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"

import { apiClient } from "@/apiClient"
import {
  PaginationItems,
  PaginationNextTrigger,
  PaginationPrevTrigger,
  PaginationRoot,
} from "@/components/ui/pagination"
import { Plant, PlantsResponse } from "@/types/plant"

import {
  Box,
  Flex,
  HStack,
  Image,
  Separator,
  Spinner,
  Text,
} from "@chakra-ui/react"

import { Tabs } from "@chakra-ui/react"
const { Root: TabsRoot, List: TabsList, Trigger: TabsTrigger, Content: TabsContent } = Tabs

import { LuFolder, LuCheck } from "react-icons/lu"

export default function PaginatedSection() {
  const [plantsList, setPlantsList] = useState<Plant[]>([])
  const [pageNumber, setPageNumber] = useState<number>(1)
  const [totalElements, setTotalElements] = useState<number>(0)
  const pageSize = 4

  const [currentTab, setCurrentTab] = useState("active")
  const isArchivedBool = currentTab === "archived"

  const { isFetching, refetch } = useQuery({
    queryKey: ["plants", pageNumber, isArchivedBool],
    queryFn: async () => {
      const { data } = await apiClient.get<PlantsResponse>("/plants", {
        params: {
          pageNumber: pageNumber - 1,
          pageSize,
          isArchived: isArchivedBool,
        },
      })
      setPlantsList(data.plants)
      setTotalElements(data.totalElements)
      return data
    },
  })

  useEffect(() => {
    refetch()
  }, [pageNumber, currentTab, refetch])

  const hasPlants = plantsList.length > 0

  return (
    <Flex direction="column" gap={6} alignItems="center" width="100%">
      <Text fontWeight={600} fontSize="3xl" textAlign="left" width="100%">
        My Plants
      </Text>
      <TabsRoot
        value={currentTab}
        onValueChange={(val) => {
          setCurrentTab(val.value)
          setPageNumber(1)
        }}
        style={{ width: "100%" }} 
      >
        <TabsList w="full" display="flex">
          <TabsTrigger
            flex="1"
            display="flex"
            justifyContent="center"
            alignItems="center"
            value="active"
          >
            <LuCheck style={{ marginRight: 5 }} />
            Active Plants
          </TabsTrigger>

          <TabsTrigger
            flex="1"
            display="flex"
            justifyContent="center"
            alignItems="center"
            value="archived"
          >
            <LuFolder style={{ marginRight: 5 }} />
            Archived Plants
          </TabsTrigger>
        </TabsList>

        <TabsContent
          value="active"
          style={{ width: "100%", minHeight: "400px" }} 
        >
          {renderPlantsOrSpinner(
            hasPlants,
            isFetching,
            plantsList,
            pageNumber,
            setPageNumber,
            totalElements,
            pageSize
          )}
        </TabsContent>

        <TabsContent
          value="archived"
          style={{ width: "100%", minHeight: "400px" }}
        >
          {renderPlantsOrSpinner(
            hasPlants,
            isFetching,
            plantsList,
            pageNumber,
            setPageNumber,
            totalElements,
            pageSize
          )}
        </TabsContent>
      </TabsRoot>
    </Flex>
  )
}

function renderPlantsOrSpinner(
  hasPlants: boolean,
  isFetching: boolean,
  plantsList: Plant[],
  pageNumber: number,
  setPageNumber: (p: number) => void,
  totalElements: number,
  pageSize: number
) {
  if (hasPlants) {
    return (
      <>
        <Flex
          direction="row"
          alignItems="center"
          justifyContent="center"
          gap={16}
          flexWrap="wrap"
          mt={4}
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
          <HStack mt={4} justifyContent="center">
            <PaginationPrevTrigger />
            <PaginationItems />
            <PaginationNextTrigger />
          </HStack>
        </PaginationRoot>
      </>
    )
  } else if (isFetching) {
    return <Spinner />
  } else {
    return <Text>There is no plant here</Text>
  }
}
