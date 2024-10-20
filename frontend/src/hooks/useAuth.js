import {useQuery} from "@tanstack/react-query";
import axios from "axios";

function useAuth() {

  const API_URL = "http://localhost:8080";
  const API = axios.create({
    baseURL: API_URL,
    withCredentials: true
  });

  function getUser() {
    return API.get("/api/v1/user").then(response => response.data);
  }

  const {data: auth, isLoading, isError} = useQuery({
    queryKey: ["user"],
    queryFn: getUser,
    staleTime: Infinity,
    retry: false
  });

  return {auth, isLoading, isError};
}

export default useAuth;
