import { createRoutesFromElements, Route } from "react-router-dom";
import Home from "../pages/Home";
import Login from "../pages/Login";
import MainLayout from "@/pages/MainLayout";
import PlantDetail from "@/pages/PlantDetail";
import Chat from "@/pages/Chat";

const Routes = () => {
  return createRoutesFromElements(
    <Route errorElement={<>Error?</>}>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Home />} />
        <Route path="/chat" element={<Chat />} />
        <Route path="/plant/:plantId" element={<PlantDetail />} />
      </Route>
      <Route path="/login" element={<Login />} />
    </Route>
  );
};

export default Routes;
