import { RouterProvider, createBrowserRouter } from "react-router-dom";
import Routes from "./Routes";

const Router = () => {
  const router = createBrowserRouter(Routes());
  return <RouterProvider router={router} />;
};

export default Router;
