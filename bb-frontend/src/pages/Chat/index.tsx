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
import { useEffect, useState } from "react";
import { IoMdSend } from "react-icons/io";
import emptyChat from "../../assets/empty-chat.svg";
import { LuFlower2 } from "react-icons/lu";


const Chat = () => {

  const CHAT_URL = import.meta.env.VITE_CHAT_URL;

  const [conversation, setConversation] = useState<string[]>([]);
  const [inputValue, setInputValue] = useState<string>("");
  const [socket, setSocket] = useState<WebSocket | null>(null);

  useEffect(() => {
    const ws = new WebSocket(CHAT_URL);
    let isWebSocketOpen = false;

    ws.onmessage = (event) => {
      isWebSocketOpen = true;
      const message = event.data;
  
      setConversation((prev) => {
        if (prev.length === 0 || (prev.length % 2 !== 0)) {
          // If no messages exist or the last message is already a user message, start a new GPT response
          return [...prev, message];
        } else {
          // Concatenate to the last GPT response
          const updatedConversation = [...prev];
          updatedConversation[updatedConversation.length - 1] += message;
          return updatedConversation;
        }
      });
    };

    setSocket(ws);

    return () => {
      if (isWebSocketOpen && ws.readyState === WebSocket.OPEN) {
        ws.close();
      }
    };
  }, []);

  const sendMessage = () => {
    if (inputValue.trim() === "" || !socket) return;
    setConversation((prev) => [...prev, inputValue]);
    socket.send(inputValue);
    setInputValue("");
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      sendMessage();
    }
  };

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
                  key={`user-message-${index}`}
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
                <Flex 
                  key={`gpt-message-${index}`} 
                  alignItems="flex-start" 
                  gap={2} 
                  alignSelf="flex-start"
                >
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
                    <Text whiteSpace="pre-wrap">{message}</Text>
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
          <IconButton size="md" variant="ghost" onClick={sendMessage}>
            <IoMdSend />
          </IconButton>
        }
      >
        <Input
          placeholder="Ask a question"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyPress}
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
