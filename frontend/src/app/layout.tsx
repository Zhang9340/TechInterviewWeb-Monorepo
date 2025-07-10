"use client";
import "./globals.css";
import React, { useCallback, useEffect } from "react";
import { AntdRegistry } from "@ant-design/nextjs-registry";
import BasicLayout from "@/layouts/BasicLayout";
import { Provider, useDispatch } from "react-redux";
import store, { AppDispatch } from "@/stores";
import { getLoginUserUsingGet } from "@/api/userController";
import { setLoginUser } from "@/stores/loginUser";
import AccessLayout from "@/access/AccessLayout";
import { ConfigProvider } from "antd";
import en_US from "antd/locale/en_US";
/**
 * Layout for executing initialization logic (encapsulates an additional layer)
 * @param children - The child components to render
 * @constructor
 */
const InitLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  /**
   * Global initialization function. Any code that needs to be executed globally and only once
   * can be written here.
   */
  const dispatch = useDispatch<AppDispatch>();
  const doInitLoginUser = useCallback(async () => {
    const res = await getLoginUserUsingGet();
    if (res.data) {
      dispatch(setLoginUser(res.data));
    } else {
    }
  }, []);

  // Execute only once
  useEffect(() => {
    doInitLoginUser();
  }, []);

  return <>{children}</>;
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <ConfigProvider locale={en_US}>
          <AntdRegistry>
            <Provider store={store}>
              <InitLayout>
                <AccessLayout>
                  <BasicLayout>{children}</BasicLayout>
                </AccessLayout>
              </InitLayout>
            </Provider>
          </AntdRegistry>
        </ConfigProvider>
      </body>
    </html>
  );
}
