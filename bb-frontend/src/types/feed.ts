export interface Post {
  id: string;
  title: string;
  content: string;
  photoUrl: string;
  uploadDate: string;
  author: string;
  totalLikes: number;
  likedByUser: boolean;
}

export interface FeedResponse {
  posts: Post[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}
