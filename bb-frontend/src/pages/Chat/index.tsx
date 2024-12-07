import { InputGroup } from "@/components/ui/input-group";
import {
  Box,
  Flex,
  Icon,
  IconButton,
  Image,
  Input,
  Text,
} from "@chakra-ui/react";
import { useState } from "react";
import { IoMdSend } from "react-icons/io";
import emptyChat from "../../assets/empty-chat.svg";
import { LuFlower2 } from "react-icons/lu";

// [
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
//   "What plant is the best in house? What plant is the best in house? What plant is the best in house? ",
//   "The best plant you want to keep in your house is a biiiiig rose that looks very good and smells awesome, i hope you will get one soon",
// ];

const Chat = () => {
  const [conversation, setConversation] = useState<string[]>([]);

  return (
    <Flex
      position="relative"
      direction="column"
      height="90vh"
      width="100%"
      maxWidth="900px"
      alignItems="center"
      justifyContent="space-between"
      py={4}
      gap={4}
    >
      <Flex
        direction="column"
        width="100%"
        height="100%"
        justifyContent={conversation.length === 0 ? "center" : "flex-start"}
        gap={6}
        pr={2}
        overflowY="scroll"
        css={{
          "&::-webkit-scrollbar": {
            width: "2px",
          },
          "&::-webkit-scrollbar-thumb": {
            background: "gray.500",
            borderRadius: "4px",
            width: "2px",
          },
        }}
      >
        {conversation.length === 0 ? (
          <Flex direction="column" textAlign="center" gap={2}>
            <Image src={emptyChat} height="200px" objectFit="contain" />
            <Text>Start a new conversation</Text>
          </Flex>
        ) : (
          conversation.map((message, index) => {
            if (index % 2 == 0) {
              //user's message
              return (
                <Flex
                  bgColor={{ base: "gray.100", _dark: "gray.900" }}
                  borderRadius="lg"
                  maxW="60%"
                  alignSelf="flex-end"
                  padding={4}
                >
                  <Text>{message}</Text>
                </Flex>
              );
            } else {
              //gpt's response
              return (
                <Flex alignItems="flex-start" gap={2} alignSelf="flex-start">
                  <Box
                    border="2px solid"
                    borderColor={{ base: "green.500", _dark: "green.700" }}
                    p={1}
                    borderRadius="lg"
                  >
                    <Icon
                      size="xl"
                      color={{ base: "green.500", _dark: "green.700" }}
                    >
                      <LuFlower2 />
                    </Icon>
                  </Box>
                  <Flex
                    bgColor={{ base: "gray.100", _dark: "gray.900" }}
                    borderRadius="lg"
                    maxW="60%"
                    padding={4}
                    border="2px solid"
                    borderColor={{ base: "green.500", _dark: "green.700" }}
                  >
                    <Text>{message}</Text>
                  </Flex>
                </Flex>
              );
            }
          })
        )}
      </Flex>

      <InputGroup
        width="100%"
        endElement={
          <IconButton size="md" variant="ghost">
            <IoMdSend />
          </IconButton>
        }
      >
        <Input
          placeholder="Ask a question"
          borderRadius="full"
          size="lg"
          colorPalette="green"
          boxShadow="0px 0px 11.9px 2px rgba(0, 191, 111, 0.48)"
        />
      </InputGroup>
    </Flex>
  );
};

export default Chat;
