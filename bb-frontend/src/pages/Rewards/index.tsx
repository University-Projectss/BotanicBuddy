import { apiClient } from "@/apiClient";
import {
  actionsDescriptions,
  actionsNames,
  actionType,
  Reward,
  RewardsResponse,
} from "@/types/rewards";
import { Flex, Spinner, Text, Image, Box, Separator } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import bronze_change_soil from "../../assets/badges/bronze_change_soil.svg";
import bronze_different_types_of_plants from "../../assets/badges/bronze_different_types_of_plants.svg";
import bronze_register_plant from "../../assets/badges/bronze_register_plant.svg";
import bronze_watering from "../../assets/badges/bronze_watering.svg";

import gold_change_soil from "../../assets/badges/gold_change_soil.svg";
import gold_different_types_of_plants from "../../assets/badges/gold_different_types_of_plants.svg";
import gold_register_plant from "../../assets/badges/gold_register_plant.svg";
import gold_watering from "../../assets/badges/gold_watering.svg";

import platinum_change_soil from "../../assets/badges/platinum_change_soil.svg";
import platinum_different_types_of_plants from "../../assets/badges/platinum_different_types_of_plants.svg";
import platinum_register_plant from "../../assets/badges/platinum_register_plant.svg";
import platinum_watering from "../../assets/badges/platinum_watering.svg";

import silver_change_soil from "../../assets/badges/silver_change_soil.svg";
import silver_different_types_of_plants from "../../assets/badges/silver_different_types_of_plants.svg";
import silver_register_plant from "../../assets/badges/silver_register_plant.svg";
import silver_watering from "../../assets/badges/silver_watering.svg";

import { FaLock } from "react-icons/fa6";
import { Tooltip } from "@/components/ui/tooltip";

const badgeImages: Record<string, string> = {
  bronze_change_soil,
  bronze_different_types_of_plants,
  bronze_register_plant,
  bronze_watering,
  silver_change_soil,
  silver_different_types_of_plants,
  silver_register_plant,
  silver_watering,
  gold_change_soil,
  gold_different_types_of_plants,
  gold_register_plant,
  gold_watering,
  platinum_change_soil,
  platinum_different_types_of_plants,
  platinum_register_plant,
  platinum_watering,
};

const Rewards = () => {
  const { data: rewards, isLoading } = useQuery<RewardsResponse>({
    queryKey: ["rewards"],
    queryFn: async () => {
      const { data } = await apiClient.get("/rewards");

      return data;
    },
  });

  const filterRewardsByAction = (rewardsList: Reward[], action: actionType) => {
    return rewardsList.filter((r) => r.action === action);
  };

  if (isLoading) return <Spinner />;

  return (
    <Flex direction="column" gap={10} width="100%" maxWidth="1200px">
      <Flex alignItems="baseline">
        <Text fontWeight="bold" fontSize="7xl">
          {rewards?.points}
        </Text>
        <Text as="span" fontSize="2xl">
          points
        </Text>
      </Flex>
      {Object.keys(actionsNames).map((action) => (
        <Flex key={action} direction="column" width="100%" gap={3}>
          <Text fontSize="3xl">
            {actionsNames[action as keyof typeof actionsNames]}
          </Text>
          <Text opacity={0.8}>
            {actionsDescriptions[action as keyof typeof actionsDescriptions]}
          </Text>
          <Separator />
          <Flex gap={10}>
            {filterRewardsByAction(
              rewards?.achievedRewards ?? [],
              action as actionType
            ).map((reward) => (
              <Flex
                direction="column"
                alignItems="center"
                justifyContent="center"
                width="300px"
              >
                <Box position="relative" display="inline-block">
                  <Image
                    key={reward.action + reward.level}
                    src={
                      badgeImages[
                        `${reward.level.toLowerCase()}_${reward.action.toLowerCase()}`
                      ]
                    }
                    height="200px"
                    objectFit="contain"
                  />
                </Box>
                <Text fontSize="xl" textAlign="center">
                  {reward.description}
                </Text>
              </Flex>
            ))}
            {filterRewardsByAction(
              rewards?.notAchievedRewards ?? [],
              action as actionType
            ).map((reward) => (
              <Flex
                direction="column"
                alignItems="center"
                justifyContent="center"
                width="300px"
              >
                <Tooltip
                  content={`You need ${reward.requiredActionNumber} actions to unlock this reward.`}
                  openDelay={100}
                >
                  <Box position="relative" display="inline-block">
                    <Image
                      opacity={0.5}
                      key={reward.action + reward.level}
                      src={
                        badgeImages[
                          `${reward.level.toLowerCase()}_${reward.action.toLowerCase()}`
                        ]
                      }
                      height="200px"
                      objectFit="contain"
                    />
                    <Box
                      position="absolute"
                      top="50%"
                      left="50%"
                      transform="translate(-50%, -50%)"
                      fontSize="5xl"
                    >
                      <FaLock />
                    </Box>
                  </Box>
                </Tooltip>
              </Flex>
            ))}
          </Flex>
        </Flex>
      ))}
    </Flex>
  );
};

export default Rewards;
