import { apiClient } from "@/apiClient";
import { FeedResponse } from "@/types/feed";
import { Flex, IconButton, Image, Stack, Text } from "@chakra-ui/react";
import { useInfiniteQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import { useMemo } from "react";
import { IoIosAdd } from "react-icons/io";
import { IoMdHeartEmpty } from "react-icons/io";
import newPost from "../../assets/new-post.svg";

const Feed = () => {
  const {
    data: feed,
    fetchNextPage,
    isLoading,
    isError,
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

  return (
    <Stack
      maxWidth="1200px"
      h="90vh"
      overflowY="scroll"
      css={{
        "&::-webkit-scrollbar": {
          display: "none",
        },
      }}
    >
      <IconButton
        colorPalette="green"
        rounded="full"
        position="absolute"
        bottom={10}
        right={10}
      >
        <IoIosAdd />
      </IconButton>
      {posts.length === 0 ? (
        <Flex direction="column" textAlign="center" gap={2} marginTop={40}>
          <Image src={newPost} height="200px" objectFit="contain" />
          <Text>There are no posts. You can create the first one!</Text>
        </Flex>
      ) : (
        posts.map((post) => (
          <Stack>
            <Image src={post.photoUrl} width="100%" />
            <Stack
              direction="row"
              alignItems="center"
              justifyContent="space-between"
              px={4}
            >
              <Stack direction="row" alignItems="center">
                <Text>{post.totalLikes}</Text>
                <IconButton>
                  <IoMdHeartEmpty />
                </IconButton>
              </Stack>
              <Text fontSize="small">
                {dayjs(post.uploadDate).format("DD.MM.YYYY")}
              </Text>
            </Stack>
            <Text>
              <Text colorPalette="green" fontWeight="bold" as="span">
                {post.author}
              </Text>
              {post.content}
            </Text>
          </Stack>
        ))
      )}
    </Stack>
  );
};

export default Feed;
