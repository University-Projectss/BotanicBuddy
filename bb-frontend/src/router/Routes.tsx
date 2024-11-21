import { createRoutesFromElements, Route } from "react-router-dom";
import Home from "../pages/Home";
import Login from "../pages/Login";
import MainLayout from "@/pages/MainLayout";

const Routes = () => {
  return createRoutesFromElements(
    <Route errorElement={<>Error?</>}>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Home />} />
      </Route>
      <Route path="/login" element={<Login />} />
    </Route>
  );
};

export default Routes;
