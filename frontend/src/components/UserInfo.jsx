import useAuth from "../hooks/useAuth.js";
import Loader from "./Loader.jsx";

function UserInfo() {

  const {auth, isLoading} = useAuth();

  if (isLoading) {
    return (
        <Loader/>
    )
  }

  if (auth?.sub) {
    return (
        <>
          <p>You are logged in as {auth.sub}</p>
          <form action={"http://localhost:8080/logout"} method={"POST"}>
            <button>Logout</button>
          </form>
        </>
    )
  }

  function login() {
    location.href = "http://localhost:8080/oauth2/authorization/auth-server";
  }

  return (
      <button onClick={() => login()}>
        Login
      </button>
  )
}

export default UserInfo;
