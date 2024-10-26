import { createRoutesFromElements, Route } from "react-router-dom";
import Home from "../pages/Home";

const Routes = () => {
  return createRoutesFromElements(
    <Route errorElement={undefined}>
      <Route path="/" element={<Home />} />
    </Route>
  );
};

export default Routes;
