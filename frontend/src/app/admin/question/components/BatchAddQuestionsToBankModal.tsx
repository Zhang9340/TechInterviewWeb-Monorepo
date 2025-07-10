import { Button, Form, message, Modal, Select } from "antd";
import React, { useEffect, useState } from "react";
import { batchAddQuestionsToBankUsingPost } from "@/api/questionBankQuestionController";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";

interface Props {
  questionIdList?: number[];
  visible: boolean;
  onSubmit: () => void;
  onCancel: () => void;
}

/**
 * Modal for Batch Adding Questions to Question Bank
 * @param props
 * @constructor
 */
const BatchAddQuestionsToBankModal: React.FC<Props> = (props) => {
  const { questionIdList = [], visible, onSubmit, onCancel } = props;
  const [form] = Form.useForm();
  const [questionBankList, setQuestionBankList] = useState<
    API.QuestionBankVO[]
  >([]);

  // Fetch the list of question banks
  const getQuestionBankList = async () => {
    // The number of question banks is not large, so fetch all at once
    const pageSize = 200;

    try {
      const res = await listQuestionBankVoByPageUsingPost({
        pageSize,
        sortField: "createTime",
        sortOrder: "descend",
      });
      setQuestionBankList(res.data?.records ?? []);
    } catch (e) {
      message.error("Failed to fetch the list of question banks: " + e.message);
    }
  };

  useEffect(() => {
    getQuestionBankList();
  }, []);

  /**
   * Submit
   * @param fields
   */
  const doSubmit = async (fields: API.QuestionBankQuestionBatchAddRequest) => {
    const hide = message.loading("Processing...");
    const questionBankId = fields.questionBankId;
    try {
      await batchAddQuestionsToBankUsingPost({
        questionIdList,
        questionBankId,
      });
      hide();
      message.success("Operation successful");
      onSubmit?.();
    } catch (error: any) {
      hide();
      message.error("Operation failed: " + error.message);
    }
  };

  return (
    <Modal
      destroyOnClose
      title={"Batch Add Questions to Question Bank"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <Form form={form} style={{ marginTop: 24 }} onFinish={doSubmit}>
        <Form.Item label="Select Question Bank" name="questionBankId">
          <Select
            style={{ width: "100%" }}
            options={questionBankList.map((questionBank) => {
              return {
                label: questionBank.title,
                value: questionBank.id,
              };
            })}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default BatchAddQuestionsToBankModal;
