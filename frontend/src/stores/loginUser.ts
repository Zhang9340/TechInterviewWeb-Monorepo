import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "@/stores/index";
import ACCESS_ENUM from "@/access/accessEnum";

// 默认用户
const DEFAULT_USER: API.LoginUserVO = {
  userName: "Not Login",
  userProfile: "No profile",
  userAvatar: "/assets/notLoginUser.png",
  userRole: ACCESS_ENUM.NOT_LOGIN,
};

/**
 * loginUser global state
 */
export const loginUserSlice = createSlice({
  name: "loginUser",
  initialState: DEFAULT_USER,
  reducers: {
    setLoginUser: (state, action: PayloadAction<API.LoginUserVO>) => {
      return {
        ...action.payload,
      };
    },
  },
});

// change state
export const { setLoginUser } = loginUserSlice.actions;

export default loginUserSlice.reducer;
