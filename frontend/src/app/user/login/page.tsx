"use client";

import React from "react";
import { LoginForm, ProForm, ProFormText } from "@ant-design/pro-form";
import { message } from "antd";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { userLoginUsingPost } from "@/api/userController";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import { useDispatch } from "react-redux";
import "./index.css";

/**
 * user Login Page
 * @param props
 */
const UserLoginPage: React.FC = (props) => {
  const [form] = ProForm.useForm(); // Create a form instance
  const router = useRouter(); // Next.js router for navigation
  const dispatch = useDispatch<AppDispatch>(); // Redux dispatcher

  /**
   * Submit Handler
   * @param values
   */
  const doSubmit = async (values: any) => {
    try {
      const res = await userLoginUsingPost(values); // Call login API
      if (res.data) {
        message.success("Login successful!");
        // Save user login state
        dispatch(setLoginUser(res.data));
        router.replace("/"); // Redirect to home page
        form.resetFields(); // Reset form fields
      }
    } catch (e: any) {
      message.error("Login failed, " + e.message); // Show error message
    }
  };

  return (
    <div id="userLoginPage">
      <LoginForm<API.UserAddRequest>
        form={form}
        logo={
          <Image
            src="/assets/Icon.jpeg"
            alt="InterviewPrep"
            width={44}
            height={44}
          />
        }
        title="User Login"
        subTitle="A platform for programmers to practice interview questions"
        onFinish={doSubmit}
        submitter={{
          searchConfig: {
            submitText: "Login",
          },
        }}
      >
        <ProFormText
          name="userAccount"
          fieldProps={{
            size: "large",
            prefix: <UserOutlined />,
          }}
          placeholder={"Enter your account"}
          rules={[
            {
              required: true,
              message: "Please enter your account!",
            },
          ]}
        />
        <ProFormText.Password
          name="userPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"Enter your password"}
          rules={[
            {
              required: true,
              message: "Please enter your password!",
            },
          ]}
        />
        <div
          style={{
            marginBlockEnd: 24,
            textAlign: "end",
          }}
        >
          Don't have an account yet?
          <Link prefetch={false} href={"/user/register"}>
            Register now
          </Link>
        </div>
      </LoginForm>
    </div>
  );
};

export default UserLoginPage;
