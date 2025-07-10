import { useSelector } from "react-redux";
import { RootState } from "@/stores";
import { usePathname } from "next/navigation";
import checkAccess from "@/access/checkAccess";
import Forbidden from "@/app/forbidden";
import React from "react";

import AccessEnum from "@/access/accessEnum";
import { findAllMenuItemByPath } from "../../config/menu";

/**
 * Unified Access Control Interceptor
 * @param children React node to render
 * @constructor
 */
const AccessLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const pathname = usePathname(); // Get the current route's pathname
  const loginUser = useSelector((state: RootState) => state.loginUser); // Get the current logged-in user from Redux state

  // Permission verification
  const menu = findAllMenuItemByPath(pathname) || {}; // Find the menu item for the current route
  const needAccess = menu?.access ?? AccessEnum.NOT_LOGIN; // Get the required access level for the menu or default to NOT_LOGIN
  const canAccess = checkAccess(loginUser, needAccess); // Check if the user has the required access level

  // If the user doesn't have permission, render the Forbidden page
  if (!canAccess) {
    return <Forbidden />;
  }

  // If the user has permission, render the children
  return <>{children}</>;
};

export default AccessLayout;
