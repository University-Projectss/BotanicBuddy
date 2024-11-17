import { createRoutesFromElements, Route } from "react-router-dom";
import Home from "../pages/Home";
import Login from "../pages/Login";

const Routes = () => {
  return createRoutesFromElements(
    <Route errorElement={<>Error?</>}>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
    </Route>
  );
};

export default Routes;
