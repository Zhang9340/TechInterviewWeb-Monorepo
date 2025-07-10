import { Form, message, Modal, Select } from "antd";
import React, { useEffect, useState } from "react";
import {
  addQuestionBankQuestionUsingPost,
  listQuestionBankQuestionVoByPageUsingPost,
  removeQuestionBankQuestionUsingPost,
} from "@/api/questionBankQuestionController";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";

interface Props {
  questionId?: number;
  visible: boolean;
  onCancel: () => void;
}

/**
 * Update Question's Associated Question Bank Modal
 * @param props
 * @constructor
 */
const UpdateBankModal: React.FC<Props> = (props) => {
  const { questionId, visible, onCancel } = props;
  const [form] = Form.useForm();
  const [questionBankList, setQuestionBankList] = useState<
    API.QuestionBankVO[]
  >([]);

  // Fetch the list of associated question banks
  const getCurrentQuestionBankIdList = async () => {
    try {
      const res = await listQuestionBankQuestionVoByPageUsingPost({
        questionId,
        pageSize: 20,
      });
      const list = (res.data?.records ?? []).map((item) => item.questionBankId);
      console.log(list);
      form.setFieldValue("questionBankIdList" as any, list);
    } catch (e) {
      message.error(
        "Failed to fetch the list of associated question banks, " + e.message,
      );
    }
  };

  useEffect(() => {
    if (questionId) {
      getCurrentQuestionBankIdList();
    }
  }, [questionId]);

  // Fetch the list of question banks
  const getQuestionBankList = async () => {
    // The number of question banks is small, fetch all at once
    const pageSize = 200;

    try {
      const res = await listQuestionBankVoByPageUsingPost({
        pageSize,
        sortField: "createTime",
        sortOrder: "descend",
      });
      setQuestionBankList(res.data?.records ?? []);
    } catch (e) {
      message.error("Failed to fetch the question bank list, " + e.message);
    }
  };

  useEffect(() => {
    getQuestionBankList();
  }, []);

  return (
    <Modal
      destroyOnClose
      title={"Update Associated Question Bank"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <Form form={form} style={{ marginTop: 24 }}>
        <Form.Item label="Associated Question Bank" name="questionBankIdList">
          <Select
            mode="multiple"
            style={{ width: "100%" }}
            options={questionBankList.map((questionBank) => {
              return {
                label: questionBank.title,
                value: questionBank.id,
              };
            })}
            onSelect={async (value) => {
              const hide = message.loading("Updating...");
              try {
                await addQuestionBankQuestionUsingPost({
                  questionId,
                  questionBankId: value,
                });
                hide();
                message.success("Successfully linked question to the bank");
              } catch (error: any) {
                hide();
                message.error(
                  "Failed to link question to the bank, " + error.message,
                );
              }
            }}
            onDeselect={async (value) => {
              const hide = message.loading("Updating...");
              try {
                await removeQuestionBankQuestionUsingPost({
                  questionId,
                  questionBankId: value,
                });
                hide();
                message.success("Successfully unlinked question from the bank");
              } catch (error: any) {
                hide();
                message.error(
                  "Failed to unlink question from the bank, " + error.message,
                );
              }
            }}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
export default UpdateBankModal;
