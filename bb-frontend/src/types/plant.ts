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
