export interface PlantsResponse {
  plants: Plant[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

export interface Plant {
  id: string;
  commonName: string;
  scientificName: string;
  family: string;
  photoUrl: string;
  uploadDate: string;
}

export interface PlantDetailResponse {
  id: string;
  commonName: string;
  scientificName: string;
  family: string;
  photoUrl: string;
  uploadDate: string;
  careRecommendation: string;
  wateringFrequency: number;
  light: string;
  soil: string;
  temperature: string;
}
