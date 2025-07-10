import { Button, Form, Input, message } from "antd";
import { editUserUsingPost } from "@/api/userController";
import React from "react";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/stores";
import loginUser, { setLoginUser } from "@/stores/loginUser";

interface Props {
  user: API.LoginUserVO;
}

/**
 * User Information Edit Form
 * @constructor
 */
const UserInfoEditForm = (props: Props) => {
  const dispatch = useDispatch<AppDispatch>();
  const [form] = Form.useForm();
  const { user } = props;

  // Set initial form values with user info
  form.setFieldsValue(user);

  /**
   * Submit handler
   * @param values
   */
  const doSubmit = async (values: API.UserEditRequest) => {
    const hide = message.loading("Processing...");
    try {
      await editUserUsingPost(values);
      hide();
      message.success("Operation successful");
      dispatch(setLoginUser({ ...user, ...values }));
    } catch (error: any) {
      hide();
      message.error("Operation failed, " + error.message);
    }
  };

  return (
    <Form
      form={form}
      style={{ marginTop: 24, maxWidth: 480 }}
      labelCol={{ span: 4 }}
      labelAlign="left"
      onFinish={doSubmit}
    >
      <Form.Item label="Phone Number" name="phoneNumber">
        <Input placeholder="Please enter phone number" />
      </Form.Item>
      <Form.Item label="Email" name="email">
        <Input placeholder="Please enter email" />
      </Form.Item>
      <Form.Item label="Grade" name="grade">
        <Input placeholder="Please enter grade" />
      </Form.Item>
      <Form.Item label="Work Experience" name="workExperience">
        <Input placeholder="Please enter work experience" />
      </Form.Item>
      <Form.Item label="Expertise" name="expertiseDirection">
        <Input placeholder="Please enter area of expertise" />
      </Form.Item>
      <Form.Item>
        <Button style={{ width: 180 }} type="primary" htmlType="submit">
          Submit
        </Button>
      </Form.Item>
    </Form>
  );
};

export default UserInfoEditForm;
