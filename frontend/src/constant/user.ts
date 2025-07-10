import AccessEnum from "@/access/accessEnum";

export const DEFAULT_USER: API.LoginUserVO = {
  userName: "Not login",
  userProfile: "None",
  userAvatar: "/assets/notLoginUser.png",
  userRole: AccessEnum.NOT_LOGIN,
};




export const USER_ROLE_ENUM = {


  USER: "user",


  ADMIN: "admin",


};






export const USER_ROLE_TEXT_MAP = {


  user: "User",


  admin: "Admin",


}
