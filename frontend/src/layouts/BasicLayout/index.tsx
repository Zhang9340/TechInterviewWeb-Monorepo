"use client";
import {
  GithubFilled,
  LogoutOutlined,
  SearchOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { ProLayout } from "@ant-design/pro-components";
import { Dropdown, Input, message, theme } from "antd";
import React, { useState } from "react";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import Link from "next/link";
import GlobalFooter from "@/app/components/GlobalFooter";
import "./index.css";
import menus from "../../../config/menu";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/stores";
import getAccessibleMenus from "@/access/menuAccess";
import MdEditor from "@/app/components/MdEditor";
import MdViewer from "@/app/components/MdViewer";
import { setLoginUser } from "@/stores/loginUser";
import { userLogoutUsingPost } from "@/api/userController";
import { DEFAULT_USER } from "@/constant/user";
import { SearchInput } from "@/layouts/BasicLayout/compoents/SearchInput";

interface Props {
  children: React.ReactNode;
}
export default function BasicLayout({ children }: Props) {
  const pathname = usePathname();

  const loginUser = useSelector((state: RootState) => state.loginUser);
  const dispatch = useDispatch<AppDispatch>();
  const router = useRouter();
  /**
   * user Logout
   */
  const userLogout = async () => {
    try {
      await userLogoutUsingPost();
      message.success("Logged out successfully");
      dispatch(setLoginUser(DEFAULT_USER));
      router.push("/");
    } catch (e) {
      // @ts-ignore
      message.error("Operation failed, " + e.message);
    }
    return;
  };

  return (
    <div
      id="basicLayout"
      style={{
        height: "100vh",
        overflow: "auto",
      }}
    >
      <ProLayout
        layout="top"
        title="Tech Interview Preparation"
        location={{
          pathname,
        }}
        logo={
          <Image
            src="/assets/Icon.jpeg"
            height={32}
            width={32}
            alt="Icon-zzy"
          />
        }
        avatarProps={{
          src:
            loginUser.userAvatar ||
            "https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg",
          size: "small",
          title: loginUser.userName || "Login",
          render: (props, dom) => {
            if (!loginUser.id) {
              return (
                <div
                  onClick={() => {
                    router.push("/user/login");
                  }}
                >
                  {dom}
                </div>
              );
            }
            return (
              <Dropdown
                menu={{
                  items: [
                    {
                      key: "userCenter",
                      icon: <UserOutlined />,
                      label: "Profile",
                    },
                    {
                      key: "logout",
                      icon: <LogoutOutlined />,
                      label: "logout",
                    },
                  ],
                  onClick: async (event: { key: React.Key }) => {
                    const { key } = event;

                    if (key === "logout") {
                      await userLogout();
                    } else if (key == "userCenter") {
                      router.push("/user/center");
                    }
                  },
                }}
              >
                {dom}
              </Dropdown>
            );
          },
        }}
        actionsRender={(props) => {
          if (props.isMobile) return [];
          return [
            <SearchInput key="search" />,
            <a key="github" href="https://github.com/Zhang9340" target="_blank">
              <GithubFilled key="GithubFilled" />
            </a>,
          ];
        }}
        headerTitleRender={(logo, title, _) => {
          return (
            <a>
              {logo}
              {title}
            </a>
          );
        }}
        //Footer
        footerRender={() => {
          return <GlobalFooter />;
        }}
        onMenuHeaderClick={(e) => console.log(e)}
        // menu item
        menuDataRender={() => {
          return getAccessibleMenus(loginUser, menus);
        }}
        menuItemRender={(item, dom) => (
          <Link href={item.path || "/"} target={item.target}>
            {dom}
          </Link>
        )}
      >
        {/*<MdEditor value={text} onChange={setText}></MdEditor>*/}
        {/*<MdViewer value={text}></MdViewer>*/}
        {children}
      </ProLayout>
    </div>
  );
}
