"use client";

import { Button, Form, Input, InputNumber, message } from "antd";
import React, { useState } from "react";
import { aiGenerateQuestionsUsingPost } from "@/api/questionController";
import "./index.css";

interface Props {}

/**
 * AI Generated Questions Page
 *
 * @param props
 * @constructor
 */
const AiGenerateQuestionPage: React.FC<Props> = (props) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  /**
   * Submit handler
   *
   * @param values
   */
  const doSubmit = async (values: API.QuestionAIGenerateRequest) => {
    const hide = message.loading("Processing...");
    setLoading(true);

    try {
      await aiGenerateQuestionsUsingPost(values);
      hide();
      message.success("Operation successful");
    } catch (error: any) {
      hide();
      message.error("Operation failed: " + error.message);
    }

    setLoading(false);
  };

  return (
    <div id="aiGenerateQuestionPage">
      <h2>AI Generate Questions</h2>
      <Form form={form} style={{ marginTop: 24 }} onFinish={doSubmit}>
        <Form.Item label="Topic" name="questionType">
          <Input placeholder="e.g. Java" />
        </Form.Item>

        <Form.Item label="Number of Questions" name="number">
          <InputNumber defaultValue={10} max={50} min={1} />
        </Form.Item>

        <Form.Item>
          <Button
            loading={loading}
            style={{ width: 180 }}
            type="primary"
            htmlType="submit"
          >
            Submit
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default AiGenerateQuestionPage;
