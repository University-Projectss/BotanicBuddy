import { apiClient } from "@/apiClient";
import { FeedResponse, Post } from "@/types/feed";
import {
  Flex,
  IconButton,
  Image,
  Separator,
  Spinner,
  Stack,
  Text,
} from "@chakra-ui/react";
import {
  InfiniteData,
  useInfiniteQuery,
  useMutation,
} from "@tanstack/react-query";
import dayjs from "dayjs";
import { useMemo } from "react";
import { IoMdHeart, IoMdHeartEmpty } from "react-icons/io";
import newPost from "../../assets/new-post.svg";
import NewPostButton from "./NewPostButton";
import { Button } from "@/components/ui/button";
import { queryClient } from "@/App";
import { toaster } from "@/components/ui/toaster";

const Feed = () => {
  const {
    data: feed,
    fetchNextPage,
    isLoading,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery<FeedResponse>({
    queryKey: ["feed"],
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.isLast ? null : lastPage.pageNumber + 1,
    queryFn: async ({ pageParam = 0 }) => {
      const { data } = await apiClient.get<FeedResponse>(
        `/posts?pageNumber=${pageParam}&pageSize=20`
      );
      return data;
    },
  });

  const posts = useMemo(
    () => (feed ? feed?.pages.flatMap((page) => page.posts) : []),
    [feed]
  );

  //optimistic update
  const likeMutation = useMutation({
    mutationFn: async (post: Post) =>
      await apiClient.patch(`/posts/${post.id}/like`),
    onMutate: async (post: Post) => {
      await queryClient.cancelQueries({ queryKey: ["feed"] });

      const previousFeed = posts;

      await queryClient.setQueryData(
        ["feed"],
        (data: InfiniteData<FeedResponse>) => ({
          ...data,
          pages: data.pages.map((page) => ({
            ...page,
            posts: page.posts.map((p) => ({
              ...p,
              likedByUser: p.id === post.id ? !p.likedByUser : p.likedByUser,
              totalLikes:
                p.id === post.id
                  ? p.likedByUser
                    ? p.totalLikes - 1
                    : p.totalLikes + 1
                  : p.totalLikes,
            })),
          })),
        })
      );

      return { previousFeed };
    },
    onError: (error: any, _, context) => {
      queryClient.setQueryData(["feed"], context?.previousFeed);
      toaster.create({
        description: error.response.data.message,
        type: "error",
      });
    },
  });

  return (
    <>
      <NewPostButton />
      <Stack
        w="100%"
        maxWidth="500px"
        position="relative"
        h="90vh"
        overflowY="scroll"
        gap={8}
        separator={<Separator />}
        alignItems="center"
        pb={4}
        css={{
          "&::-webkit-scrollbar": {
            display: "none",
          },
        }}
      >
        {isLoading && <Spinner />}
        {posts.length === 0 ? (
          <Flex direction="column" textAlign="center" gap={2} marginTop={40}>
            <Image src={newPost} height="200px" objectFit="contain" />
            <Text>There are no posts. You can create the first one!</Text>
          </Flex>
        ) : (
          posts.map((post) => (
            <Stack key={post.id} width="100%" gap={2}>
              <Image src={post.photoUrl} width="100%" />
              <Stack
                direction="row"
                alignItems="center"
                justifyContent="space-between"
                px={4}
              >
                <Stack direction="row" alignItems="center">
                  <IconButton
                    variant="ghost"
                    onClick={() => likeMutation.mutate(post)}
                  >
                    <Text>{post.totalLikes}</Text>
                    {post.likedByUser ? (
                      <IoMdHeart color="red" />
                    ) : (
                      <IoMdHeartEmpty />
                    )}
                  </IconButton>
                </Stack>
                <Text fontSize="small" colorPalette="gray">
                  {dayjs(post.uploadDate).format("DD.MM.YYYY")}
                </Text>
              </Stack>
              <Text>
                <Text
                  colorPalette="green"
                  fontWeight="bold"
                  as="span"
                  marginRight={2}
                >
                  {post.author}
                </Text>
                {post.content}
              </Text>
            </Stack>
          ))
        )}
        {hasNextPage && (
          <Button
            colorPalette="green"
            width="fit-content"
            loading={isFetchingNextPage}
            onClick={() => {
              void fetchNextPage();
            }}
          >
            Load more posts
          </Button>
        )}
      </Stack>
    </>
  );
};

export default Feed;
