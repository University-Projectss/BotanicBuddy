import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: "licenta-fmi.firebaseapp.com",
  projectId: "licenta-fmi",
  storageBucket: "licenta-fmi.appspot.com",
  messagingSenderId: "1041916442718",
  appId: "1:1041916442718:web:760a5f79b23bf012cb9ccf",
};

export const app = initializeApp(firebaseConfig);

export const storage = getStorage(app);
