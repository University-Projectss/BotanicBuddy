import { Flex } from "@chakra-ui/react";
import PaginatedSection from "./PaginatedSection";
import Weather from "./Weather";

const Home = () => {
  return (
    <Flex
      direction="column"
      alignItems="center"
      gap={10}
      width="60%"
      maxWidth="1200px"
    >
      <Weather />

      <PaginatedSection />
    </Flex>
  );
};

export default Home;
