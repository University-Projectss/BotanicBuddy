export type actionType =
  | "WATERING"
  | "CHANGE_SOIL"
  | "REGISTER_PLANT"
  | "DIFFERENT_TYPES_OF_PLANTS";

export const actionsNames = {
  WATERING: "Watering",
  CHANGE_SOIL: "Change soil",
  REGISTER_PLANT: "New plants",
  DIFFERENT_TYPES_OF_PLANTS: "Different types of plants",
};

export const actionsDescriptions = {
  WATERING:
    "Watering your plants regularly is essential for their growth. Earn badges as you care for your plants! 💧",
  CHANGE_SOIL:
    "Healthy soil keeps plants thriving. Show your dedication by earning badges as you refresh their soil. 🌞 ",
  REGISTER_PLANT:
    "Track your plant family! Earn badges as you register more plants in your collection! 🪴",
  DIFFERENT_TYPES_OF_PLANTS:
    "Celebrate biodiversity by collecting and registering different species of plants. Unlock badges as you explore nature's variety. 🌳 ",
};

export type levelType = "BRONZE" | "SILVER" | "GOLD" | "PLATINUM";

export interface Reward {
  level: levelType;
  action: actionType;
  description: string;
  requiredActionNumber: number;
  points: number;
  date: string;
}

export interface RewardsResponse {
  achievedRewards: Reward[];
  notAchievedRewards: Reward[];
  points: number;
}
