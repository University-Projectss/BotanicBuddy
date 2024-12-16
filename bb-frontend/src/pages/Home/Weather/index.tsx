import { apiClient } from "@/apiClient";
import { useQuery } from "@tanstack/react-query";

function formatDate(date: Date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are zero-based
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

const Weather = () => {
  const { data: forecast, isLoading } = useQuery({
    queryKey: ["forecast"],
    retry: false,
    queryFn: async () => {
      const { data } = await apiClient.get(
        `/forecast/${formatDate(new Date())}`
      );
      console.log(data);

      return data;
    },
  });

  return <div>{forecast}</div>;
};

export default Weather;
