import { addQuestionUsingPost } from "@/api/questionController";
import { ProColumns, ProTable } from "@ant-design/pro-components";
import { message, Modal } from "antd";
import React from "react";
interface Props {
  visible: boolean;
  columns: ProColumns<API.Question>[];
  onSubmit: (values: API.QuestionAddRequest) => void;
  onCancel: () => void;
}

/**
 * Add a new question
 * @param fields
 */
const handleAdd = async (fields: API.QuestionAddRequest) => {
  const hide = message.loading("Adding...");
  try {
    await addQuestionUsingPost(fields);
    hide();
    message.success("Created successfully");
    return true;
  } catch (error: any) {
    hide();
    message.error("Creation failed, " + error.message);
    return false;
  }
};

/**
 * Create modal
 * @param props
 * @constructor
 */
const CreateModal: React.FC<Props> = (props) => {
  const { visible, columns, onSubmit, onCancel } = props;

  return (
    <Modal
      destroyOnClose
      title={"Create"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <ProTable
        type="form"
        columns={columns}
        onSubmit={async (values: API.QuestionAddRequest) => {
          const success = await handleAdd(values);
          if (success) {
            onSubmit?.(values);
          }
        }}
      />
    </Modal>
  );
};
export default CreateModal;
