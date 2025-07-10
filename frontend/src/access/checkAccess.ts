import ACCESS_ENUM from "@/access/accessEnum";

/**
 * Check Permissions (Determine if the currently logged-in user has a specific permission)
 * @param loginUser The currently logged-in user
 * @param needAccess The required permission
 * @return boolean Whether the user has the required permission
 */
const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess = ACCESS_ENUM.NOT_LOGIN,
) => {
  // Get the permissions of the currently logged-in user (if there is no loginUser, it means the user is not logged in)
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;

  // Allow access if no login is required
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }

  // If access requires the user to be logged in
  if (needAccess === ACCESS_ENUM.USER) {
    // If the user is not logged in, they do not have permission
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }

  // If administrator privileges are required
  if (needAccess === ACCESS_ENUM.ADMIN) {
    // If the user is not an administrator, they do not have permission
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }

  return true; // The user has the required permission
};

export default checkAccess;
