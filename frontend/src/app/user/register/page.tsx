"use client";

import React from "react";
import { LoginForm, ProForm, ProFormText } from "@ant-design/pro-form";
import { message } from "antd";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import {
  userLoginUsingPost,
  userRegisterUsingPost,
} from "@/api/userController";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import { useDispatch } from "react-redux";
import "./index.css";

/**
 * user register Page
 * @param props
 */
const UserRegisterPage: React.FC = (props) => {
  const [form] = ProForm.useForm(); // Create a form instance
  const router = useRouter(); // Next.js router for navigation
  const dispatch = useDispatch<AppDispatch>(); // Redux dispatcher

  /**
   * Submit Handler
   * @param values
   */
  const doSubmit = async (values: any) => {
    try {
      const res = await userRegisterUsingPost(values); // Call login API
      if (res.data) {
        message.success("Registration successful!, Please login");

        router.replace("/user/login"); // Redirect to home page
        form.resetFields(); // Reset form fields
      }
    } catch (e: any) {
      message.error("Registration failed, " + e.message); // Show error message
    }
  };

  return (
    <div id="userRegisterPage">
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
        title="User Registration"
        subTitle="A platform for programmers to practice interview questions"
        onFinish={doSubmit}
        submitter={{
          searchConfig: {
            submitText: "Register",
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
        <ProFormText.Password
          name="checkPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"Confirm your password"}
          rules={[
            {
              required: true,
              message: "Please re-enter your password!",
            },
          ]}
        />
        <div
          style={{
            marginBlockEnd: 24,
            textAlign: "end",
          }}
        >
          Already have an account?
          <Link prefetch={false} href={"/user/login"}>
            Login now
          </Link>
        </div>
      </LoginForm>
    </div>
  );
};

export default UserRegisterPage;
